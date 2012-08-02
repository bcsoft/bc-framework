package cn.bc.identity.dao;

import java.util.List;

import cn.bc.core.dao.CrudDao;
import cn.bc.identity.domain.ActorHistory;

/**
 * Actor隶属信息的变动历史Dao接口
 * 
 * @author dragon
 * 
 */
public interface ActorHistoryDao extends CrudDao<ActorHistory> {
	/**
	 * 获取Actor当前的隶属信息,如果没有就自动创建一个
	 * 
	 * @param actorId
	 * @return
	 */
	ActorHistory loadCurrent(Long actorId);

	/**
	 * 根据编码获取，如用户历史
	 * 
	 * @param actorCode
	 * @return
	 */
	ActorHistory loadByCode(String actorCode);

	/**
	 * 获取指定编码的姓名信息
	 * 
	 * @param actorCode
	 * @return
	 */
	List<String> findNames(List<String> actorCode);
}
