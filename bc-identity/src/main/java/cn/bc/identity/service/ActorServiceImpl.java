package cn.bc.identity.service;

import java.util.List;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.dao.ActorDao;
import cn.bc.identity.domain.Actor;
import cn.bc.security.domain.Module;

/**
 * 参与者Service接口的实现
 * 
 * @author dragon
 * 
 */
public class ActorServiceImpl extends DefaultCrudService<Actor> implements
		ActorService {
	private ActorDao actorDao;

	public void setActorDao(ActorDao actorDao) {
		this.actorDao = actorDao;
		this.setCrudDao(actorDao);
	}

	public ActorDao getActorDao() {
		return actorDao;
	}

	public Actor loadByCode(String actorCode) {
		return this.actorDao.loadByCode(actorCode);
	}

	public Actor loadBelong(Long followerId, Integer[] masterTypes) {
		return this.actorDao.loadBelong(followerId, masterTypes);
	}

	public List<Actor> findMaster(Long followerId, Integer[] relationTypes,
			Integer[] masterTypes) {
		return this.actorDao.findMaster(followerId, relationTypes, masterTypes);
	}

	public List<Actor> findFollower(Long masterId, Integer[] relationTypes,
			Integer[] followerTypes) {
		return this.actorDao.findFollower(masterId, relationTypes,
				followerTypes);
	}

	public List<Actor> findTopUnit() {
		return this.actorDao.findTopUnit();
	}

	public List<Actor> findAllUnit(Integer... statues) {
		return this.actorDao.findAllUnit(statues);
	}

	public List<Actor> findLowerOrganization(Long higherOrganizationId,
			Integer... lowerOrganizationTypes) {
		return this.actorDao.findLowerOrganization(higherOrganizationId,
				lowerOrganizationTypes);
	}

	public List<Actor> findHigherOrganization(Long lowerOrganizationId,
			Integer... higherOrganizationTypes) {
		return this.actorDao.findHigherOrganization(lowerOrganizationId,
				higherOrganizationTypes);
	}

	public List<Actor> findUser(Long organizationId) {
		return this.actorDao.findUser(organizationId);
	}

	public List<Actor> findAncestorOrganization(Long lowerOrganizationId,
			Integer... ancestorOrganizationTypes) {
		return this.actorDao.findAncestorOrganization(lowerOrganizationId,
				ancestorOrganizationTypes);
	}

	public List<Actor> findDescendantOrganization(Long higherOrganizationId,
			Integer... descendantOrganizationTypes) {
		return this.actorDao.findDescendantOrganization(higherOrganizationId,
				descendantOrganizationTypes);
	}

	public List<Actor> findDescendantUser(Long organizationId,
			Integer... descendantOrganizationTypes) {
		return this.actorDao.findDescendantUser(organizationId,
				descendantOrganizationTypes);
	}

	public List<Module> findCanUseModules(Long actorId) {
		return this.actorDao.findCanUseModules(actorId);
	}

	public Actor save4belong(Actor follower, Actor belong) {
		return this.actorDao.save4belong(follower, belong);
	}

	public List<Actor> find(Integer[] actorTypes, Integer[] actorStatues) {
		return this.actorDao.find(actorTypes, actorStatues);
	}
}
