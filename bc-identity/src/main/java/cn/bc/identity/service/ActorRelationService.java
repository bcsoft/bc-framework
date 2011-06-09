package cn.bc.identity.service;

import java.util.List;

import cn.bc.core.service.CrudService;
import cn.bc.identity.domain.ActorRelation;

/**
 * Actor关系Service接口
 * @author dragon
 *
 */
public interface ActorRelationService extends CrudService<ActorRelation>{
	/**
	 * 获取某个关联关系
	 * @param type 关联类型,不能为空
	 * @param masterId 主控方Id,不能为空
	 * @param followerId 从属方Id,不能为空
	 * @return
	 */
	ActorRelation load(Integer type, Long masterId, Long followerId);
	
	/**
	 * 获取主控方某类型的关联关系列表
	 * @param type 关联类型,不能为空
	 * @param masterId 主控方Id,不能为空
	 * @return
	 */
	List<ActorRelation> findByMaster(Integer type, Long masterId);
	
	/**
	 * 获取从属方某类型的关联关系列表
	 * @param type 关联类型,不能为空
	 * @param followerId 从属方Id,不能为空
	 * @return
	 */
	List<ActorRelation> findByFollower(Integer type, Long followerId);
}
