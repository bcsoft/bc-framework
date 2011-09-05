package cn.bc.scheduler.service;

import java.util.List;

import cn.bc.scheduler.domain.ScheduleJob;
import cn.bc.scheduler.domain.ScheduleLog;

/**
 * 任务调度Service的接口定义
 * 
 * @author dragon
 * @since 2011-08-30
 */
public interface SchedulerService {
	/**
	 * 获取所有调度任务配置
	 * 
	 * @return
	 */
	List<ScheduleJob> findAllScheduleJob();
	
	/**
	 * 获取所有可用的调度任务配置
	 * 
	 * @return
	 */
	List<ScheduleJob> findAllEnabledScheduleJob();

	/**
	 * 保存调度任务
	 * 
	 * @param scheduleJob
	 */
	void saveScheduleJob(ScheduleJob scheduleJob);

	/**
	 * 保存调度日志
	 * 
	 * @param scheduleLog
	 */
	void saveScheduleLog(ScheduleLog scheduleLog);

	/**
	 * 加载调度任务配置
	 * @param scheduleJobId
	 * @return
	 */
	ScheduleJob loadScheduleJob(Long scheduleJobId);
}