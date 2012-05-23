package cn.bc.report.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.Assert;

import cn.bc.core.util.DateUtils;
import cn.bc.docs.domain.Attach;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.report.domain.ReportHistory;
import cn.bc.report.domain.ReportTask;
import cn.bc.report.domain.ReportTemplate;
import cn.bc.report.web.struts2.ReportAction;
import cn.bc.scheduler.domain.ScheduleLog;
import cn.bc.scheduler.service.SchedulerService;
import cn.bc.template.service.TemplateService;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridExporter;

/**
 * @author dragon
 * 
 */
public class RunReportTemplateJobBean extends QuartzJobBean {
	private static final Log logger = LogFactory
			.getLog(RunReportTemplateJobBean.class);
	private ActorHistory executor;// 报表任务的执行者，通常为系统管理员
	private SchedulerService schedulerService;
	private JpaTemplate jpaTemplate;
	private ReportTask reportTask;// 要执行的报表任务
	private ScheduleLog scheduleLog;
	private TemplateService templateService;
	private ReportHistoryService reportHistoryService;

	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	public void setReportHistoryService(
			ReportHistoryService reportHistoryService) {
		this.reportHistoryService = reportHistoryService;
	}

	public void setExecutor(ActorHistory executor) {
		this.executor = executor;
	}

	public void setJpaTemplate(JpaTemplate jpaTemplate) {
		this.jpaTemplate = jpaTemplate;
	}

	public void setReportTask(ReportTask reportTask) {
		this.reportTask = reportTask;
	}

	public void setSchedulerService(SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

	private void stopInError(JobExecutionContext context) {
		try {
			logger.error("因发生异常,终止报表任务\"" + reportTask.getName() + "\"的后续调度");
			// 把老的任务给停止、删除
			String jobName = "REPORT_TASK_JOB" + reportTask.getId();
			context.getScheduler()
					.unscheduleJob(jobName, ReportTask.GROUP_NAME);
			context.getScheduler().deleteJob(jobName, ReportTask.GROUP_NAME);
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void errorLog(JobExecutionContext context, Exception ex) {
		// 记录异常日志
		scheduleLog.setSuccess(false);
		scheduleLog.setEndDate(Calendar.getInstance());
		scheduleLog.setErrorType(ex.getClass().getName());
		StringWriter strWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(strWriter);
		ex.printStackTrace(writer);
		writer.close();
		scheduleLog.setMsg(strWriter.toString());
		this.schedulerService.saveScheduleLog(scheduleLog);
		logger.error("报表任务\"" + reportTask.getName()
				+ "\"执行过程发生错误,请检查任务的错误日志信息");

		if (!reportTask.isIgnoreError())
			stopInError(context);
	}

	/**
	 * Invoke the method via the MethodInvoker.
	 */
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		Date fromDate = new Date();
		logger.warn("正在执行报表任务\"" + reportTask.getName());

		// 创建日志
		scheduleLog = new ScheduleLog();
		scheduleLog.setStartDate(Calendar.getInstance());
		scheduleLog.setCfgName(reportTask.getName());
		scheduleLog.setCfgGroup(ReportTask.GROUP_NAME);
		scheduleLog.setCfgBean("RunReportTemplateJobBean");
		scheduleLog.setCfgMethod("runReportTemplate");
		scheduleLog.setCfgCron(reportTask.getCron());

		try {
			// 执行报表模板并返回生成的历史报表
			ReportTemplate tpl = this.reportTask.getTemplate();
			Assert.notNull(tpl, "报表任务配置的报表模板为空！");
			ReportHistory h = buildReportHistory(tpl, "xls",
					this.templateService, this.jpaTemplate);
			
			// 设置报表历史的一些信息
			h.setAuthor(this.executor);
			h.setSourceType("报表任务");
			h.setSourceId(this.reportTask.getId());
			
			// 保存报表历史
			this.reportHistoryService.save(h);
			
			context.setResult(null);

			// 记录成功日志
			scheduleLog.setSuccess(true);
			scheduleLog.setEndDate(Calendar.getInstance());
			scheduleLog.setErrorType(null);
			scheduleLog.setMsg(null);
			this.schedulerService.saveScheduleLog(scheduleLog);
		} catch (Exception ex) {
			// 记录异常日志
			errorLog(context, ex);
		}
		logger.warn("报表任务\"" + reportTask.getName() + "\"执行完毕,耗时"
				+ DateUtils.getWasteTime(fromDate));
	}

	/**
	 * 执行报表模板并返回生成的历史报表
	 * 
	 * @param tpl
	 * @param extension
	 * @param templateService
	 * @param jpaTemplate
	 * @return
	 * @throws Exception
	 */
	public static ReportHistory buildReportHistory(ReportTemplate tpl,
			String extension, TemplateService templateService,
			JpaTemplate jpaTemplate) throws Exception {
		Calendar now = Calendar.getInstance();
		JSONObject config = tpl.getConfigJson();
		Assert.notNull(config, "报表模板的详细配置为空！");
		logger.info("runReportTemplate:id=" + tpl.getId() + ",name="
				+ tpl.getName());

		String datedir = new SimpleDateFormat("yyyyMM").format(now.getTime());

		// 不含路径的文件名
		String fileName = new SimpleDateFormat("yyyyMMddHHmmssSSSS").format(now
				.getTime()) + "." + extension;
		// 文件绝对路径
		String realpath = Attach.DATA_REAL_PATH + "/"
				+ ReportHistory.DATA_SUB_PATH + "/" + datedir + "/" + fileName;

		// 构建文件要保存到的目录
		File file = new File(realpath);
		if (!file.getParentFile().exists()) {
			if (logger.isWarnEnabled()) {
				logger.warn("mkdir=" + file.getParentFile().getAbsolutePath());
			}
			file.getParentFile().mkdirs();
		}

		// 导出报表结果到文件
		List<Column> columns = ReportAction.buildGridColumns(config);
		GridExporter exporter = new GridExporter();
		exporter.setIdLabel("序号");
		exporter.setTitle(tpl.getName());
		exporter.setColumns(columns);// 列配置
		exporter.setData(ReportAction
				.buildQuery(
						jpaTemplate,
						ReportAction.buildSqlObject(templateService, config,
								null, columns)).condition(null).list());// 数据
		exporter.setTemplateFile(ReportAction.buildExportTemplate(
				templateService, config));// 导出数据的模板
		FileOutputStream out = new FileOutputStream(file);
		if (logger.isDebugEnabled())
			logger.debug("runReportTemplate:"
					+ DateUtils.getWasteTime(now.getTime()));
		exporter.exportTo(out);

		// 保存一个历史报表
		ReportHistory h = new ReportHistory();
		h.setFileDate(now);
		h.setSuccess(true);
		h.setCategory(tpl.getCategory());
		h.setPath(datedir + "/" + fileName);
		h.setSubject(tpl.getName());

		return h;
	}
}
