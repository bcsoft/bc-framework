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
	 * 在所属部门下，通过岗位名称获得岗位集合
	 *
	 * @param belongIds 岗位隶属的上级Id，可以为空
	 * @param names 岗位名称，不为空
	 * @param relationTypes  关联的类型，对应ActorRelation的type属性，belongIds为空时为空
	 * @param followerTypes 岗位的类型，对应Actor的type属性，不为空
	 * @return
	 */
	List<Actor> findByNames(Long[] belongIds, String[] names,
			Integer[] relationTypes, Integer[] followerTypes);

	/**
	 * 通过岗位名称获得岗位集合
	 *
	 * @param belongIds 岗位隶属的上级Id，可以为空
	 * @param codes 岗位编码，不为空
	 * @param relationTypes  关联的类型，对应ActorRelation的type属性，belongIds为空时为空
	 * @param followerTypes 岗位的类型，对应Actor的type属性，不为空
	 * @return
	 */
	List<Actor> findByCodes(Long[] belongIds, String[] codes,
			Integer[] relationTypes, Integer[] followerTypes);
}
