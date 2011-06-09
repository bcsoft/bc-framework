package cn.bc.identity.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.dao.ActorRelationDao;
import cn.bc.identity.domain.ActorRelation;

/**
 * Actor关系Service接口的实现
 * 
 * @author dragon
 * 
 */
public class ActorRelationServiceImpl extends DefaultCrudService<ActorRelation>
		implements ActorRelationService {
	private ActorRelationDao actorRelationDao;

	@Autowired
	public void setActorRelationDao(ActorRelationDao actorRelationDao) {
		this.actorRelationDao = actorRelationDao;
		this.setCrudDao(actorRelationDao);
	}

	public List<ActorRelation> findByMaster(Integer type, Long masterId) {
		return this.actorRelationDao.findByMaster(type, masterId);
	}

	public ActorRelation load(Integer type, Long masterId, Long followerId) {
		return this.actorRelationDao.load(type, masterId, followerId);
	}

	public List<ActorRelation> findByFollower(Integer type, Long followerId) {
		return this.actorRelationDao.findByFollower(type, followerId);
	}
}
