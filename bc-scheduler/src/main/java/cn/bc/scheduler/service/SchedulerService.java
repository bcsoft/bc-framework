package cn.bc.scheduler.service;

import java.util.List;

import org.quartz.Scheduler;

import cn.bc.scheduler.domain.ScheduleLog;
import cn.bc.scheduler.domain.TriggerCfg;

/**
 * 任务调度Service的接口定义
 * 
 * @author dragon
 * @since 2011-08-30
 */
public interface SchedulerService {
	/**
	 * 获取所有可用的触发器配置
	 * 
	 * @return
	 */
	public List<TriggerCfg> findAllEnabledTriggerCfg();

	/**
	 * 保存任务调度日志信息
	 * 
	 * @param scheduleLog
	 */
	public void saveScheduleLog(ScheduleLog scheduleLog);

	/**
	 * 终止所有调度
	 */
	public void stop();

	/**
	 * 设置调度者
	 * 
	 * @param scheduler
	 */
	public void setScheduler(Scheduler scheduler);

	/**
	 * 停止指定的触发器
	 * 
	 * @param triggerCfgId
	 */
	public void stopTrigger(long triggerCfgId);
}