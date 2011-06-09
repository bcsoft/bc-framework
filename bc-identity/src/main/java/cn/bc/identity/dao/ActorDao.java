package cn.bc.identity.dao;

import java.util.List;

import cn.bc.core.dao.CrudDao;
import cn.bc.identity.domain.Actor;
import cn.bc.security.domain.Module;

/**
 * 参与者Dao接口
 * @author dragon
 *
 */
public interface ActorDao extends CrudDao<Actor>{
	/**
	 * 根据编码获取，如用户的登录名
	 * @param actorCode actor的编码
	 * @return
	 */
	Actor loadByCode(String actorCode);

	/**
	 * 获取从属方的单一上级
	 * @param followerId 从属方id
	 * @param masterTypes 主控方的类型，对应Actor的type属性
	 * @return
	 */
	Actor loadBelong(Long followerId, Integer[] masterTypes);

	/**
	 * 获取从属方指定关联关系的主控方
	 * @param followerId 从属方id
	 * @param relationTypes 关联的类型，对应ActorRelation的type属性
	 * @param masterTypes 主控方的类型，对应Actor的type属性
	 * @return
	 */
	List<Actor> findMaster(Long followerId, Integer[] relationTypes, Integer[] masterTypes);
	
	/**
	 * 获取隶属指定actor的所有actor
	 * @param masterId 主控方id
	 * @param relationTypes 关联的类型，对应ActorRelation的type属性
	 * @param followerTypes 从属方的类型，对应Actor的type属性
	 * @return
	 */
	List<Actor> findFollower(Long masterId, Integer[] relationTypes, Integer[] followerTypes);

	/**
	 * 获取顶层单位信息
	 * @return
	 */
	List<Actor> findTopUnit();

	/**
	 * 获取隶属关系中的所有直接下级组织信息
	 * @param higherOrganizationId 上级组织的id
	 * @param lowerOrganizationTypes 下级组织的类型，对应Actor的type属性，默认为单位+部门+岗位
	 * @return
	 */
	List<Actor> findLowerOrganization(Long higherOrganizationId, Integer... lowerOrganizationTypes);

	/**
	 * 获取隶属关系中的所有直接上级组织信息
	 * @param lowerOrganizationId 下级组织的id
	 * @param higherOrganizationTypes 上级组织的类型，对应Actor的type属性，默认为单位+部门+岗位
	 * @return
	 */
	List<Actor> findHigherOrganization(Long lowerOrganizationId, Integer... higherOrganizationTypes);

	/**
	 * 获取直接隶属于组织的人员信息
	 * @param organizationId 上级组织的id
	 * @return
	 */
	List<Actor> findUser(Long organizationId);

	/**
	 * 获取隶属关系中的所有上级组织信息，包括上级的上级
	 * @param lowerOrganizationId 下级组织的id
	 * @param ancestorOrganizationTypes 上级组织的类型，对应Actor的type属性，默认为单位+部门+岗位
	 * @return
	 */
	List<Actor> findAncestorOrganization(Long lowerOrganizationId, Integer... ancestorOrganizationTypes);

	/**
	 * 获取隶属关系中的所有下级组织信息，包括下级的下级
	 * @param higherOrganizationId 上级组织的id
	 * @param descendantOrganizationTypes 下级组织的类型，对应Actor的type属性，默认为单位+部门+岗位
	 * @return
	 */
	List<Actor> findDescendantOrganization(Long higherOrganizationId, Integer... descendantOrganizationTypes);

	/**
	 * 获取隶属于组织的人员信息，包括组织的所有后代组织下的人员
	 * @param organizationId 上级组织的id
	 * @param descendantOrganizationTypes 下级组织的类型，对应Actor的type属性，默认为单位+部门+岗位
	 * @return
	 */
	List<Actor> findDescendantUser(Long organizationId, Integer... descendantOrganizationTypes);

	/**
	 * 获取actor可以使用的模块列表
	 * @param actorId
	 * @return
	 */
	List<Module> findCanUseModules(Long actorId);

	/**
	 * 获取所有单位信息
	 * @param statues 状态列表
	 * @return
	 */
	List<Actor> findAllUnit(Integer... statues);

	/**
	 * @param follower
	 * @param belong
	 */
	Actor save4belong(Actor follower, Actor belong);

	/**获取指定类型和状态的Actor
	 * @param actorTypes 类型列表
	 * @param actorStatues 状态列表
	 * @return
	 */
	List<Actor> find(Integer[] actorTypes, Integer[] actorStatues);
}
