package cn.bc.subscribe.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.subscribe.domain.Subscribe;

/**
 * 订阅Dao接口
 * 
 * @author lbj
 * 
 */
public interface SubscribeDao extends CrudDao<Subscribe> {
	

	/**
	 * 根据时间编码查找 订阅
	 * @param eventCode
	 * @return
	 */
	Subscribe loadByEventCode(String eventCode);
	
	/**
	 * 唯一性检测
	 * @param eventCode
	 * @param id
	 * @return
	 */
	boolean isUnique(String eventCode,Long id);
}
