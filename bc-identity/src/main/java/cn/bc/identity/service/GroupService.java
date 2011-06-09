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
	 * @param belong 岗位隶属的上级
	 * @param userIds 岗位所分配的用户id
	 */
	Actor save(Actor group, Actor belong, Long[] userIds);
}
