package cn.bc.identity.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bc.identity.dao.ActorRelationDao;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorRelation;

/**
 * 岗位Service接口的实现
 * 
 * @author dragon
 * 
 */
public class GroupServiceImpl extends ActorServiceImpl implements GroupService {
	private ActorRelationDao actorRelationDao;

	public void setActorRelationDao(ActorRelationDao actorRelationDao) {
		this.actorRelationDao = actorRelationDao;
	}

	public Actor save(Actor group, Actor belong, Long[] userIds) {
		// 先保存获取id值
		group = super.save4belong(group, belong);// 这里已经处理了上级关系的保存

		// 处理分配的用户信息
		List<ActorRelation> curArs;
		curArs = this.actorRelationDao.findByMaster(ActorRelation.TYPE_BELONG,
				group.getId());
		Map<String, ActorRelation> userMap = new HashMap<String, ActorRelation>();
		for (ActorRelation ar : curArs)
			userMap.put(ar.getFollower().getId().toString(), ar);
		if (userIds != null && userIds.length > 0) {
			ActorRelation ar;
			List<ActorRelation> newArs = new ArrayList<ActorRelation>();
			for (Long userId : userIds) {
				ar = userMap.get(userId.toString());
				if (ar == null) {
					// 创建新的关系保存
					ar = new ActorRelation();
					ar.setMaster(group);
					ar.setFollower(this.load(userId));
					ar.setType(ActorRelation.TYPE_BELONG);
					newArs.add(ar);
				} else {
					// 已存在的排除不处理
					curArs.remove(ar);
				}
			}
			// 保存新增加的关系
			this.actorRelationDao.save(newArs);

			// 删除不再关联的关系
			this.actorRelationDao.delete(curArs);
		} else {
			// 删除现有的岗位-用户关联
			this.actorRelationDao.delete(curArs);
		}

		// 返回
		return group;
	}
}
