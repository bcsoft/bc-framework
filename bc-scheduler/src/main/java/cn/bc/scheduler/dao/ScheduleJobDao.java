package cn.bc.scheduler.dao;

import java.util.List;

import cn.bc.core.dao.CrudDao;
import cn.bc.scheduler.domain.ScheduleJob;

/**
 * 调度任务的DAO接口
 * 
 * @author dragon
 * @since 2011-08-30
 */
public interface ScheduleJobDao extends CrudDao<ScheduleJob> {
	/**
	 * 获取所有可用的调度配置
	 * 
	 * @return
	 */
	public List<ScheduleJob> findAllEnabled();
	
	/**
	 * 获取所有调度配置
	 * 
	 * @return
	 */
	public List<ScheduleJob> findAll();
}
