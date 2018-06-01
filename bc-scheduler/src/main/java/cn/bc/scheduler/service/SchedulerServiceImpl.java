package cn.bc.scheduler.service;

import cn.bc.scheduler.dao.ScheduleJobDao;
import cn.bc.scheduler.dao.ScheduleLogDao;
import cn.bc.scheduler.domain.ScheduleJob;
import cn.bc.scheduler.domain.ScheduleLog;

import java.util.List;

/**
 * 任务调度Service的实现
 *
 * @author dragon
 * @since 2011-08-30
 */
public class SchedulerServiceImpl implements SchedulerService {
  private ScheduleJobDao scheduleJobDao;
  private ScheduleLogDao scheduleLogDao;

  public void setScheduleJobDao(ScheduleJobDao jobCfgDao) {
    this.scheduleJobDao = jobCfgDao;
  }

  public void setScheduleLogDao(ScheduleLogDao scheduleLogDao) {
    this.scheduleLogDao = scheduleLogDao;
  }

  public List<ScheduleJob> findAllEnabledScheduleJob() {
    return this.scheduleJobDao.findAllEnabled();
  }

  public List<ScheduleJob> findAllScheduleJob() {
    return this.scheduleJobDao.findAll();
  }

  public void saveScheduleLog(ScheduleLog scheduleLog) {
    this.scheduleLogDao.save(scheduleLog);
  }

  public void saveScheduleJob(ScheduleJob scheduleJob) {
    this.scheduleJobDao.save(scheduleJob);
  }

  public ScheduleJob loadScheduleJob(Long scheduleJobId) {
    return this.scheduleJobDao.load(scheduleJobId);
  }
}