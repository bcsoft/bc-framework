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
import org.springframework.orm.jpa.JpaTemplate;

import cn.bc.BCConstants;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.service.ActorHistoryService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.log.domain.OperateLog;
import cn.bc.log.service.OperateLogService;
import cn.bc.report.dao.ReportTaskDao;
import cn.bc.report.domain.ReportTask;
import cn.bc.scheduler.service.SchedulerService;
import cn.bc.template.service.TemplateService;

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
	private ActorHistoryService actorHistoryService;
	private JpaTemplate jpaTemplate;
	private TemplateService templateService;
	private ReportHistoryService reportHistoryService;
	private IdGeneratorService idGeneratorService;

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	@Autowired
	public void setReportHistoryService(
			ReportHistoryService reportHistoryService) {
		this.reportHistoryService = reportHistoryService;
	}

	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	@Autowired
	public void setJpaTemplate(JpaTemplate jpaTemplate) {
		this.jpaTemplate = jpaTemplate;
	}

	@Autowired
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Autowired
	public void setActorHistoryService(ActorHistoryService actorHistoryService) {
		this.actorHistoryService = actorHistoryService;
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
		logger.warn("scheduling reportTask:" + reportTask.getTemplate().getCategory() + "/" + reportTask.getName());

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
			jobDetail.getJobDataMap().put("jpaTemplate", this.jpaTemplate);
			jobDetail.getJobDataMap().put("templateService",
					this.templateService);
			jobDetail.getJobDataMap().put("reportHistoryService",
					this.reportHistoryService);
			jobDetail.getJobDataMap().put("executor",
					this.actorHistoryService.loadByCode("admin"));

			trigger = new CronTrigger(triggerName, ReportTask.GROUP_NAME,
					reportTask.getCron());
			nextDate = this.scheduler.scheduleJob(jobDetail, trigger);

			// 记录操作日志
			OperateLog worklog = new OperateLog();
			worklog.setType(OperateLog.TYPE_WORK);// 工作日志
			worklog.setWay(OperateLog.WAY_SYSTEM);
			worklog.setFileDate(Calendar.getInstance());
			worklog.setAuthor(this.actorHistoryService.loadByCode("admin"));// 超级管理员作为执行者
			worklog.setPtype(ReportTask.class.getSimpleName());
			worklog.setPid(String.valueOf(reportTask.getId()));
			worklog.setSubject("启动报表任务：" + reportTask.getName());
			worklog.setContent(null);
			worklog.setOperate(OperateLog.OPERATE_UPDATE);
			worklog.setUid(this.idGeneratorService.next("WorkLog"));
			this.operateLogService.save(worklog);
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
