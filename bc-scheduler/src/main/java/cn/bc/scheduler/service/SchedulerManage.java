package cn.bc.scheduler.service;

import java.util.Date;

/**
 * 调度任务管理器的接口定义，注意该接口相关的实现类不要配置事务管理
 *
 * @author dragon
 * @since 2011-08-30
 */
public interface SchedulerManage {
  /* 定时任务管理器是否处于禁用状态 */
  boolean isDisabled();

  /**
   * 停止调度
   *
   * @param scheduleJobId
   */
  void stopJob(Long scheduleJobId) throws Exception;

  /**
   * 删除调度，调度任务将被标记删除而不是永久删除
   *
   * @param scheduleJobId
   */
  void deleteJob(Long scheduleJobId) throws Exception;

  /**
   * 暂停调度
   *
   * @param scheduleJobId
   */
  void pauseJob(Long scheduleJobId) throws Exception;

  /**
   * 恢复停止的调度
   *
   * @param scheduleJobId
   */
  void resumeJob(Long scheduleJobId) throws Exception;

  /**
   * 计划任务的调度
   *
   * @param scheduleJobId
   * @return 任务的下次执行时间
   */
  Date scheduleJob(Long scheduleJobId) throws Exception;

  /**
   * 重新计划任务的调度
   *
   * @param scheduleJobId
   * @return 任务的下次执行时间
   */
  Date rescheduleJob(Long scheduleJobId) throws Exception;
}