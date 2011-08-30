package cn.bc.scheduler.spring;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.MethodInvoker;

import cn.bc.scheduler.domain.JobCfg;
import cn.bc.scheduler.domain.TriggerCfg;
import cn.bc.scheduler.service.SchedulerService;

/**
 * Spring SchedulerFactoryBean的扩展 将配置迁移到数据中
 * 
 * @author dragon
 * @since 2010-01-30
 */
public class SchedulerFactoryBeanEx extends SchedulerFactoryBean {
	private static final Log logger = LogFactory
			.getLog(SchedulerFactoryBeanEx.class);
	private ApplicationContext applicationContext;
	private SchedulerService schedulerService;
	private List<TriggerCfg> triggerCfgs; // 触发器配置列表

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
		super.setApplicationContext(applicationContext);
	}

	public void setSchedulerService(SchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

	/*
	 * 复写基类的方法，控制必须手工调用egrandInit方法初始化
	 * 
	 * @see
	 * org.springframework.scheduling.quartz.SchedulerFactoryBean#afterPropertiesSet
	 * ()
	 */
	public void afterPropertiesSet() throws Exception {
		// 获取所有触发器配置
		triggerCfgs = this.schedulerService.findAllEnabledTriggerCfg();
		if (triggerCfgs == null)
			triggerCfgs = new ArrayList<TriggerCfg>();
		logger.warn("加载调度任务配置：(count=" + triggerCfgs.size() + ")");
		Iterator<TriggerCfg> itor = triggerCfgs.iterator();
		TriggerCfg tc;
		while (itor.hasNext()) {
			tc = (TriggerCfg) itor.next();
			logger.warn("	id=" + tc.getId() + ",name=" + tc.getName()
					+ ",cron=" + tc.getCron());
		}

		// 初始化triggers属性
		Trigger[] triggers = this.buildTriggers(triggerCfgs);
		if (triggers == null || triggers.length == 0) {
			logger.warn("没有配置调度任务的触发器，不会执行任何调度任务！");
			return;
		}
		this.setTriggers(triggers);

		// 启动调度
		super.afterPropertiesSet();

		this.schedulerService.setScheduler(this.getScheduler());
	}

	private Trigger[] buildTriggers(List<TriggerCfg> triggerCfgs)
			throws Exception {
		if (triggerCfgs == null || triggerCfgs.isEmpty())
			return new Trigger[0];

		List<CronTriggerBean> triggerBeans = new ArrayList<CronTriggerBean>();
		TriggerCfg triggerCfg;
		JobCfg jobCfg;
		CronTriggerBeanEx trigger;
		// MethodInvokingJobDetailFactoryBeanEx jobDetailProxy;
		JobDetail jobDetail;
		for (int i = 0; i < triggerCfgs.size(); i++) {
			triggerCfg = (TriggerCfg) triggerCfgs.get(i);
			jobCfg = triggerCfg.getJobCfg();

			// 处理jobDetail
			MethodInvoker methodInvoker = new MethodInvoker();
			methodInvoker.setTargetObject(this.applicationContext
					.getBean(jobCfg.getBean()));
			methodInvoker.setTargetMethod(jobCfg.getMethod());
			methodInvoker.prepare();
			jobDetail = new JobDetail(jobCfg.getName(), null,
					MethodInvokingJobEx.class);
			jobDetail.getJobDataMap().put("methodInvoker", methodInvoker);// 记录方法调用器
			jobDetail.getJobDataMap().put("jobCfg", jobCfg);// 记录配置信息

			// 处理触发器
			trigger = (CronTriggerBeanEx) this.applicationContext
					.getBean("bcCronTrigger");
			trigger.setJobDetail(jobDetail);
			trigger.setName(triggerCfg.getName());
			trigger.getJobDataMap().put("triggerCfg", triggerCfg);// 记录配置信息
			trigger.getJobDataMap().put("schedulerService", schedulerService);
			try {
				trigger.setCronExpression(this.formatExpression(triggerCfg
						.getCron()));
			} catch (ParseException e) {
				logger.error("CronExpression解析错误，忽略该触发器继续：id="
						+ triggerCfg.getId() + ",name=" + triggerCfg.getName());
				continue;
			}
			trigger.bcInit();

			triggerBeans.add(trigger);

			if (logger.isDebugEnabled()) {
				logger.debug(i + ":triggerName=" + triggerCfg.getName()
						+ ",cron=" + triggerCfg.getCron() + ",triggerDesc="
						+ triggerCfg.getDescription() + ",triggerId="
						+ triggerCfg.getId());
				logger.debug("	job=" + jobCfg.getName() + ",jobBean="
						+ jobCfg.getBean() + ",jobMethod=" + jobCfg.getMethod()
						+ ",jobId=" + jobCfg.getId());
			}
		}

		Trigger[] triggers = new Trigger[triggerBeans.size()];
		triggerBeans.toArray(triggers);

		return triggers;
	}

	// 特殊处理
	private String formatExpression(String cronExpression) {
		if ("now".equalsIgnoreCase(cronExpression)) {// 特殊处理为启动后立即执行，只要用于方便测试
			// 获取当前的小时和分钟数，将cronExpression设置为当前时间推后30秒
			Calendar now = Calendar.getInstance();
			now.add(Calendar.SECOND, 30);// 推后30秒
			String c = String.valueOf(now.get(Calendar.SECOND));
			c += " " + now.get(Calendar.MINUTE);
			c += " " + now.get(Calendar.HOUR_OF_DAY);
			c += " * * ? *";
			logger.fatal("		formatExpression=" + c);
			return c;
		} else {// 按配置
			return cronExpression;
		}
	}
}
