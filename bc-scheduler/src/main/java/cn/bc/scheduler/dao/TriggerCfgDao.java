package cn.bc.scheduler.dao;

import java.util.List;

import cn.bc.core.dao.CrudDao;
import cn.bc.scheduler.domain.TriggerCfg;

/**
 * 触发器配置的DAO接口
 * 
 * @author dragon
 * @since 2011-08-30
 */
public interface TriggerCfgDao extends CrudDao<TriggerCfg> {
	/**
	 * 获取所有可用的触发器配置
	 * 
	 * @return
	 */
	public List<TriggerCfg> findAllEnabled();
}
