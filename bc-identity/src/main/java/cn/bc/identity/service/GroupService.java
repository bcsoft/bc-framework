package cn.bc.identity.service;

import cn.bc.identity.domain.Actor;

import java.util.List;

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
	 * @param belongIds 岗位隶属的上级Id
	 * @param userIds 岗位所分配的用户id
	 */
	Actor save(Actor group, Long[] belongIds, Long[] userIds);

	/**
	 * 通过岗位名称获得岗位集合
	 *
	 * @param belongIds 岗位隶属的上级Id
	 * @param names 岗位名称
	 * @param relationTypes  关联的类型，对应ActorRelation的type属性
	 * @param followerTypes 岗位的类型，对应Actor的type属性
	 * @return
	 */
	List<Actor> findByNames(Long[] belongIds, String[] names,
			Integer[] relationTypes, Integer[] followerTypes);

	/**
	 * 通过岗位名称获得岗位集合
	 *
	 * @param belongIds 岗位隶属的上级Id
	 * @param codes 岗位编码
	 * @param relationTypes  关联的类型，对应ActorRelation的type属性
	 * @param followerTypes 岗位的类型，对应Actor的type属性
	 * @return
	 */
	List<Actor> findByCodes(Long[] belongIds, String[] codes,
			Integer[] relationTypes, Integer[] followerTypes);
}
