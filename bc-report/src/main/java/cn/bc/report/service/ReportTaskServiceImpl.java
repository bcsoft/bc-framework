package cn.bc.report.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.BCConstants;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.log.domain.OperateLog;
import cn.bc.log.service.OperateLogService;
import cn.bc.report.dao.ReportTaskDao;
import cn.bc.report.domain.ReportTask;
import cn.bc.scheduler.service.SchedulerService;

/**
 * 报表任务Service接口的实现
 * 
 * @author lbj
 * 
 */
public class ReportTaskServiceImpl extends DefaultCrudService<ReportTask>
		implements ReportTaskService, InitializingBean {
	private static Log logger = LogFactory.getLog(ReportTaskServiceImpl.class);
	private OperateLogService operateLogService;
	private ReportTaskDao reportTaskDao;
	private Scheduler scheduler;
	private SchedulerService schedulerService;

	@Autowired
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Autowired
	public void setSchedulerService(SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

	@Autowired
	public void setOperateLogService(OperateLogService operateLogService) {
		this.operateLogService = operateLogService;
	}

	@Autowired
	public void setReportTaskDao(ReportTaskDao reportTaskDao) {
		this.setCrudDao(reportTaskDao);
		this.reportTaskDao = reportTaskDao;
	}

	public void afterPropertiesSet() throws Exception {
		// 立即计划所有可用的调度任务
		List<ReportTask> tasks = this.reportTaskDao.findAllEnabled();
		logger.warn("scheduling " + tasks.size() + " reportTasks");
		for (ReportTask task : tasks) {
			this.doStart(task.getId());
		}
	}

	public Date doStart(Long taskId) throws Exception {
		ReportTask reportTask = this.reportTaskDao.load(taskId);
		if (reportTask == null) {
			logger.warn("ignore unknowed reportTaskId:" + taskId);
			return null;
		}
		logger.warn("scheduling reportTask:" + reportTask.toString());

		Date nextDate;
		String triggerName = "REPORT_TASK_TRIGGER" + reportTask.getId();
		// 检测是否已经调度
		CronTrigger trigger = (CronTrigger) this.scheduler.getTrigger(
				triggerName, ReportTask.GROUP_NAME);
		if (null == trigger) {// Trigger不存在，就创建一个新的
			JobDetail jobDetail = new JobDetail("REPORT_TASK_JOB"
					+ reportTask.getId(), ReportTask.GROUP_NAME,
					RunReportTemplateJobBean.class);

			// 记录状态数据：要注入到RunReportTemplateJobBean的属性
			jobDetail.getJobDataMap().put("reportTask", reportTask);// 记录配置信息
			jobDetail.getJobDataMap().put("schedulerService",
					this.schedulerService);

			trigger = new CronTrigger(triggerName, ReportTask.GROUP_NAME,
					reportTask.getCron());
			nextDate = this.scheduler.scheduleJob(jobDetail, trigger);

			// 记录操作日志
			this.operateLogService.saveWorkLog(
					ReportTask.class.getSimpleName(),
					String.valueOf(reportTask.getId()),
					"启动报表任务：" + reportTask.getName(), null,
					OperateLog.OPERATE_UPDATE);
		} else {// Trigger已存在，更新相应的调度设置
			trigger.setCronExpression(reportTask.getCron());
			nextDate = this.scheduler.rescheduleJob(trigger.getName(),
					trigger.getGroup(), trigger);

			// 记录操作日志
			this.operateLogService.saveWorkLog(
					ReportTask.class.getSimpleName(),
					String.valueOf(reportTask.getId()),
					"重置报表任务：" + reportTask.getName(), null,
					OperateLog.OPERATE_UPDATE);
		}

		// 将任务的状态设置为正常
		if (reportTask.getStatus() != BCConstants.STATUS_ENABLED) {
			SystemContext context = SystemContextHolder.get();
			reportTask.setStatus(BCConstants.STATUS_ENABLED);
			reportTask.setModifiedDate(Calendar.getInstance());
			reportTask.setModifier(context.getUserHistory());
			this.reportTaskDao.save(reportTask);
		}

		return nextDate;
	}

	public void doStop(Long taskId) throws Exception {
		ReportTask reportTask = this.reportTaskDao.load(taskId);
		if (reportTask == null) {
			logger.warn("ignore unknowed reportTaskId:" + taskId);
			return;
		}

		// 删除调度
		String triggerName = "REPORT_TASK_TRIGGER" + reportTask.getId();
		CronTrigger trigger = (CronTrigger) this.scheduler.getTrigger(
				triggerName, ReportTask.GROUP_NAME);
		if (null != trigger) {
			this.scheduler.deleteJob("REPORT_TASK_JOB" + reportTask.getId(),
					ReportTask.GROUP_NAME);

			// 记录操作日志
			this.operateLogService.saveWorkLog(
					ReportTask.class.getSimpleName(),
					String.valueOf(reportTask.getId()),
					"停止报表任务：" + reportTask.getName(), null,
					OperateLog.OPERATE_UPDATE);
		}

		// 将任务的状态设置为禁用
		if (reportTask.getStatus() != BCConstants.STATUS_DISABLED) {
			SystemContext context = SystemContextHolder.get();
			reportTask.setStatus(BCConstants.STATUS_DISABLED);
			reportTask.setModifiedDate(Calendar.getInstance());
			reportTask.setModifier(context.getUserHistory());
			this.reportTaskDao.save(reportTask);

		}
	}
}
