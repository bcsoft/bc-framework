package cn.bc.scheduler.spring;

import cn.bc.scheduler.domain.ScheduleJob;
import cn.bc.scheduler.domain.ScheduleLog;
import cn.bc.scheduler.service.SchedulerService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean.MethodInvokingJob;
import org.springframework.util.MethodInvoker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;

import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerKey.triggerKey;

public class MethodInvokingJobEx extends MethodInvokingJob {
	private static final Logger logger = LoggerFactory.getLogger(MethodInvokingJobEx.class);
	private MethodInvoker methodInvoker;
	private SchedulerService schedulerService;
	private ScheduleJob scheduleJob;
	private ScheduleLog scheduleLog;

	public void setMethodInvoker(MethodInvoker methodInvoker) {
		this.methodInvoker = methodInvoker;
	}

	public void setSchedulerService(SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

	public void setScheduleJob(ScheduleJob jobCfg) {
		this.scheduleJob = jobCfg;
	}

	private String buildTime(long startTime) {
		long w = (new Date().getTime() - startTime);// + "毫秒"
		long h = 0, m = 0, s = 0, ms = 0;
		ms = w % 1000;// 毫秒
		s = w / 1000;// 秒

		m = s / 60;// 分
		s = s % 60;// 秒

		h = m / 60;// 时
		m = m % 60;// 分

		String msg = "";
		if (h > 0)
			msg += h + "小时";
		if (m > 0)
			msg += m + "分";
		if (s > 0)
			msg += s + "秒";
		if (ms > 0)
			msg += ms + "毫秒";
		return msg;
	}

	private void stopInError(JobExecutionContext context) {
		try {
			logger.error("因发生异常,终止任务\"{}\"的后续调度", scheduleJob.getName());
			// 把老的任务给停止、删除
			context.getScheduler().unscheduleJob(triggerKey(scheduleJob.getName(), scheduleJob.getGroupn()));
			context.getScheduler().deleteJob(jobKey(scheduleJob.getName(), scheduleJob.getGroupn()));
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
		logger.error("任务\"{}\"执行过程发生错误,请检查任务的错误日志信息", scheduleJob.getName());

		if (!scheduleJob.isIgnoreError())
			stopInError(context);
	}

	/**
	 * Invoke the method via the MethodInvoker.
	 */
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		long startTime = new Date().getTime();
		logger.warn("正在执行任务\"{}\"({})", scheduleJob.getName(), scheduleJob.getMemo());
		// 创建日志
		scheduleLog = new ScheduleLog();
		scheduleLog.setStartDate(Calendar.getInstance());
		scheduleLog.setCfgName(scheduleJob.getName());
		scheduleLog.setCfgGroup(scheduleJob.getGroupn());
		scheduleLog.setCfgBean(scheduleJob.getBean());
		scheduleLog.setCfgMethod(scheduleJob.getMethod());
		scheduleLog.setCfgCron(scheduleJob.getCron());

		try {
			context.setResult(this.methodInvoker.invoke());

			// 记录成功日志
			scheduleLog.setSuccess(true);
			scheduleLog.setEndDate(Calendar.getInstance());
			scheduleLog.setErrorType(null);
			scheduleLog.setMsg(null);
			this.schedulerService.saveScheduleLog(scheduleLog);
		} catch (InvocationTargetException ex) {
			// 记录异常日志
			errorLog(context, ex);

			// if (ex.getTargetException() instanceof JobExecutionException) {
			// // -> JobExecutionException, to be logged at info level by Quartz
			// throw (JobExecutionException) ex.getTargetException();
			// }
			// else {
			// // -> "unhandled exception", to be logged at error level by
			// Quartz
			// throw new JobMethodInvocationFailedException(this.methodInvoker,
			// ex.getTargetException());
			// }
		} catch (Exception ex) {
			// 记录异常日志
			errorLog(context, ex);

			// -> "unhandled exception", to be logged at error level by Quartz
			// throw new JobMethodInvocationFailedException(this.methodInvoker,
			// ex);
		}
		if (logger.isWarnEnabled())
			logger.warn("任务\"{}\"执行完毕,耗时{}", scheduleJob.getName(), this.buildTime(startTime));
	}
}
