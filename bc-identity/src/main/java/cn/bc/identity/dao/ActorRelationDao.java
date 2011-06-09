package cn.bc.identity.dao;

import java.io.Serializable;
import java.util.List;

import cn.bc.core.dao.CrudDao;
import cn.bc.identity.domain.ActorRelation;

/**
 * 参与者Dao接口
 * @author dragon
 *
 */
public interface ActorRelationDao extends CrudDao<ActorRelation>{
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
	 * 获取主控方某类型的关联关系列表
	 * @param type 关联类型,不能为空
	 * @param followerId 从属方Id,不能为空
	 * @param masterTypes 主控方类型
	 * @return
	 */
	List<ActorRelation> findByFollower(Integer type, Long followerId, Integer[] masterTypes);
	List<ActorRelation> findByFollower(Integer type, Long followerId);
	
	/**
	 * 获取某关联关系列表
	 * @param mfIds 从属方或主控方的Id,不能为空
	 * @return
	 */
	List<ActorRelation> findByMasterOrFollower(Serializable[] mfIds);
	
	/**
	 * 删除关联关系
	 * @param mfIds 从属方或主控方的Id,不能为空
	 * @return
	 */
	void deleteByMasterOrFollower(Serializable[] mfIds);

	/**获取单一的隶属上级
	 * @param followerId 下级的id
	 * @param masterTypes 主控方类型
	 * @return
	 */
	ActorRelation load4Belong(Long followerId, Integer[] masterTypes);
	
	/**
	 * CRUD'D:删除对象
	 * @param actorRelation 对象
	 */
	void delete(ActorRelation actorRelation);
	
	/**
	 * CRUD'D:删除对象
	 * @param actorRelations 对象
	 */
	void delete(List<ActorRelation> actorRelations);
}
