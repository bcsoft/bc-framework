package cn.bc.scheduler.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.MethodInvoker;

import cn.bc.core.exception.CoreException;
import cn.bc.scheduler.domain.ScheduleJob;
import cn.bc.scheduler.spring.MethodInvokingJobEx;

/**
 * 调度任务管理器的实现，注意该类不要配置事务管理
 * <p>
 * 该类在初始化时就会自动将可用的调度任务推送到调度计划中执行
 * </p>
 * 
 * @author dragon
 * @since 2011-08-30
 */
public class SchedulerManageImpl implements SchedulerManage,
		ApplicationContextAware, InitializingBean {
	private static final Log logger = LogFactory
			.getLog(SchedulerManageImpl.class);
	private Scheduler scheduler;
	private SchedulerService schedulerService;
	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void setSchedulerService(SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

	public void afterPropertiesSet() throws Exception {
		// 立即计划所有可用的调度任务
		List<ScheduleJob> jobs = this.schedulerService
				.findAllEnabledScheduleJob();
		logger.warn("scheduling " + jobs.size() + " jobs");
		for (ScheduleJob job : jobs) {
			this.scheduleJob(job.getId());
		}
	}

	public Date scheduleJob(Long scheduleJobId) throws Exception {
		ScheduleJob scheduleJob = this.schedulerService
				.loadScheduleJob(scheduleJobId);
		if (scheduleJob == null) {
			logger.warn("ignore unknowed scheduleJobId:" + scheduleJob);
			return null;
		}
		logger.warn("scheduling job:" + scheduleJob.toString());

		Date nextDate;
		// 检测是否已经调度
		CronTrigger trigger = (CronTrigger) this.scheduler.getTrigger(
				scheduleJob.getTriggerName(), scheduleJob.getGroupn());
		if (null == trigger) {
			// Trigger不存在，就创建一个新的
			MethodInvoker methodInvoker = new MethodInvoker();
			methodInvoker.setTargetObject(this.applicationContext
					.getBean(scheduleJob.getBean()));
			methodInvoker.setTargetMethod(scheduleJob.getMethod());
			methodInvoker.prepare();

			JobDetail jobDetail = new JobDetail(scheduleJob.getName(),
					scheduleJob.getGroupn(), MethodInvokingJobEx.class);

			// 记录状态数据
			jobDetail.getJobDataMap().put("methodInvoker", methodInvoker);// 记录方法调用器
			jobDetail.getJobDataMap().put("scheduleJob", scheduleJob);// 记录配置信息
			jobDetail.getJobDataMap().put("schedulerService",
					this.schedulerService);

			trigger = new CronTrigger(scheduleJob.getTriggerName(),
					scheduleJob.getGroupn(), scheduleJob.getCron());
			nextDate = this.scheduler.scheduleJob(jobDetail, trigger);
		} else {
			// Trigger已存在，更新相应的调度设置
			trigger.setCronExpression(scheduleJob.getCron());
			nextDate = this.scheduler.rescheduleJob(trigger.getName(),
					trigger.getGroup(), trigger);
		}

		// 将任务的状态设置为正常
		if (scheduleJob.getStatus() != ScheduleJob.STATUS_ENABLED) {
			scheduleJob.setStatus(ScheduleJob.STATUS_ENABLED);
			this.schedulerService.saveScheduleJob(scheduleJob);
		}

		return nextDate;
	}

	public Date rescheduleJob(Long scheduleJobId) throws Exception {
		ScheduleJob scheduleJob = this.schedulerService
				.loadScheduleJob(scheduleJobId);
		if (scheduleJob == null) {
			logger.warn("ignore unknowed scheduleJobId:" + scheduleJob);
			return null;
		}

		Date nextDate;
		// 检测是否已经调度
		CronTrigger trigger = (CronTrigger) this.scheduler.getTrigger(
				scheduleJob.getTriggerName(), scheduleJob.getGroupn());
		if (null == trigger) {
			throw new CoreException("trigger not exist:scheduleJobId="
					+ scheduleJobId);
		} else {
			// Trigger已存在，更新相应的调度设置
			trigger.setCronExpression(scheduleJob.getCron());
			nextDate = this.scheduler.rescheduleJob(trigger.getName(),
					trigger.getGroup(), trigger);
		}

		// 将任务的状态设置为正常
		if (scheduleJob.getStatus() != ScheduleJob.STATUS_ENABLED) {
			scheduleJob.setStatus(ScheduleJob.STATUS_ENABLED);
			this.schedulerService.saveScheduleJob(scheduleJob);
		}

		return nextDate;
	}

	public void stopJob(Long scheduleJobId) throws Exception {
		ScheduleJob scheduleJob = this.schedulerService
				.loadScheduleJob(scheduleJobId);
		if (scheduleJob == null) {
			logger.warn("ignore unknowed scheduleJobId:" + scheduleJob);
			return;
		}

		// 删除调度
		CronTrigger trigger = (CronTrigger) this.scheduler.getTrigger(
				scheduleJob.getTriggerName(), scheduleJob.getGroupn());
		if (null != trigger) {
			this.scheduler.deleteJob(scheduleJob.getName(),
					scheduleJob.getGroupn());
		}

		// 将任务的状态设置为禁用
		if (scheduleJob.getStatus() != ScheduleJob.STATUS_DISABLED) {
			scheduleJob.setStatus(ScheduleJob.STATUS_DISABLED);
			this.schedulerService.saveScheduleJob(scheduleJob);
		}
	}

	public void deleteJob(Long scheduleJobId) throws Exception {
		ScheduleJob scheduleJob = this.schedulerService
				.loadScheduleJob(scheduleJobId);
		if (scheduleJob == null) {
			logger.warn("ignore unknowed scheduleJobId:" + scheduleJob);
			return;
		}

		// 删除调度
		CronTrigger trigger = (CronTrigger) this.scheduler.getTrigger(
				scheduleJob.getTriggerName(), scheduleJob.getGroupn());
		if (null != trigger) {
			this.scheduler.deleteJob(scheduleJob.getName(),
					scheduleJob.getGroupn());
		}

		// 标记为删除状态
		scheduleJob.setStatus(ScheduleJob.STATUS_DELETED);
		this.schedulerService.saveScheduleJob(scheduleJob);
	}

	public void pauseJob(Long scheduleJobId) throws Exception {
		ScheduleJob scheduleJob = this.schedulerService
				.loadScheduleJob(scheduleJobId);
		if (scheduleJob == null) {
			logger.warn("ignore unknowed scheduleJobId:" + scheduleJob);
			return;
		}

		// 暂停调度
		CronTrigger trigger = (CronTrigger) this.scheduler.getTrigger(
				scheduleJob.getTriggerName(), scheduleJob.getGroupn());
		if (null != trigger) {
			this.scheduler.pauseJob(scheduleJob.getName(),
					scheduleJob.getGroupn());
		}
	}

	public void resumeJob(Long scheduleJobId) throws Exception {
		ScheduleJob scheduleJob = this.schedulerService
				.loadScheduleJob(scheduleJobId);
		if (scheduleJob == null) {
			logger.warn("ignore unknowed scheduleJobId:" + scheduleJob);
			return;
		}

		// 恢复调度
		CronTrigger trigger = (CronTrigger) this.scheduler.getTrigger(
				scheduleJob.getTriggerName(), scheduleJob.getGroupn());
		if (null != trigger) {
			this.scheduler.resumeJob(scheduleJob.getName(),
					scheduleJob.getGroupn());
		}
	}
}