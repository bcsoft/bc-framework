package cn.bc.scheduler.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import cn.bc.scheduler.dao.ScheduleLogDao;
import cn.bc.scheduler.dao.TriggerCfgDao;
import cn.bc.scheduler.domain.ScheduleLog;
import cn.bc.scheduler.domain.TriggerCfg;

/**
 * 任务调度Service的实现
 * 
 * @author dragon
 * @since 2011-08-30
 */
public class SchedulerServiceImpl implements SchedulerService {
	private static final Log logger = LogFactory
			.getLog(SchedulerServiceImpl.class);
	private TriggerCfgDao triggerCfgDao;
	private ScheduleLogDao scheduleLogDao;
	private Scheduler scheduler;

	public void settriggerCfgDao(TriggerCfgDao triggerCfgDao) {
		this.triggerCfgDao = triggerCfgDao;
	}

	public void setScheduleLogDao(ScheduleLogDao scheduleLogDao) {
		this.scheduleLogDao = scheduleLogDao;
	}

	public List<TriggerCfg> findAllEnabledTriggerCfg() {
		return this.triggerCfgDao.findAllEnabled();
	}

	public void saveScheduleLog(ScheduleLog scheduleLog) {
		this.scheduleLogDao.save(scheduleLog);
	}

	public void stop() {
		if (this.scheduler != null) {
			try {
				this.scheduler.shutdown();
			} catch (SchedulerException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void stopTrigger(long triggerCfgId) {
		try {
			TriggerCfg triggerCfg = this.loadTriggerCfg(triggerCfgId);
			if (triggerCfg == null)
				return;

			// 把任务给停止、删除
			scheduler.unscheduleJob(triggerCfg.getName(), null);
			scheduler.deleteJob(triggerCfg.getJobCfg().getName(), null);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private TriggerCfg loadTriggerCfg(long triggerCfgId) {
		return this.triggerCfgDao.load(triggerCfgId);
	}
}