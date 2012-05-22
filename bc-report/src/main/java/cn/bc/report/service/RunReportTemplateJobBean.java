package cn.bc.report.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.Assert;

import cn.bc.core.util.DateUtils;
import cn.bc.report.domain.ReportTask;
import cn.bc.report.domain.ReportTemplate;
import cn.bc.scheduler.domain.ScheduleLog;
import cn.bc.scheduler.service.SchedulerService;

public class RunReportTemplateJobBean extends QuartzJobBean {
	private static final Log logger = LogFactory
			.getLog(RunReportTemplateJobBean.class);
	private SchedulerService schedulerService;
	private ReportTask reportTask;
	private ScheduleLog scheduleLog;

	public void setReportTask(ReportTask reportTask) {
		this.reportTask = reportTask;
	}

	public void setSchedulerService(SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

	private void stopInError(JobExecutionContext context) {
		try {
			logger.error("因发生异常,终止任务\"" + reportTask.getName() + "\"的后续调度");
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
		logger.error("任务\"" + reportTask.getName() + "\"执行过程发生错误,请检查任务的错误日志信息");

		if (!reportTask.isIgnoreError())
			stopInError(context);
	}

	/**
	 * Invoke the method via the MethodInvoker.
	 */
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		Date fromDate = new Date();
		logger.warn("正在执行任务\"" + reportTask.getName());

		// 创建日志
		scheduleLog = new ScheduleLog();
		scheduleLog.setStartDate(Calendar.getInstance());
		scheduleLog.setCfgName(reportTask.getName());
		scheduleLog.setCfgGroup(ReportTask.GROUP_NAME);
		scheduleLog.setCfgBean("RunReportTemplateJobBean");
		scheduleLog.setCfgMethod("runReportTemplate");
		scheduleLog.setCfgCron(reportTask.getCron());

		try {
			// 运行报表任务对应的模板
			context.setResult(this.runReportTemplate());

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
		logger.warn("任务\"" + reportTask.getName() + "\"执行完毕,耗时"
				+ DateUtils.getWasteTime(fromDate));
	}

	protected Object runReportTemplate() {
		ReportTemplate tpl = this.reportTask.getTemplate();
		Assert.notNull(tpl);
		
		logger.fatal("runReportTemplate:id=" + tpl.getId() + ",name=" + tpl.getName());
		
		return null;
	}
}
