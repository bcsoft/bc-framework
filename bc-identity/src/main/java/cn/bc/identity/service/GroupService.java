package cn.bc.identity.service;

import cn.bc.identity.domain.Actor;

/**
 * 岗位Service接口
 * 
 * @author dragon
 * 
 */
public interface GroupService extends ActorService {
	/**
	 * 保存岗位信息
	 * @param group 岗位
	 * @param belongId 岗位隶属的上级Id
	 * @param userIds 岗位所分配的用户id
	 */
	Actor save(Actor group, Long[] belongIds, Long[] userIds);
}
