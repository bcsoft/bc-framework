package cn.bc.identity.service;

import cn.bc.core.service.CrudService;
import cn.bc.identity.domain.ActorHistory;

/**
 * Actor隶属信息的变动历史Service接口
 * 
 * @author dragon
 * 
 */
public interface ActorHistoryService extends CrudService<ActorHistory> {
	/**
	 * 获取Actor当前的隶属信息,如果没有就自动创建一个
	 * 
	 * @param actorId
	 * @return
	 */
	ActorHistory loadCurrent(Long actorId);
}