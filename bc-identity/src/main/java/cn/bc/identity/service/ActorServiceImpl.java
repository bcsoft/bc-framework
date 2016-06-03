package cn.bc.identity.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bc.BCConstants;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.dao.ActorDao;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.domain.Resource;

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

	@Override
	public void delete(Serializable id) {
		// 仅将状态标记为已删除
		Map<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("status", new Integer(BCConstants.STATUS_DELETED));
		this.update(id, attrs);
	}

	@Override
	public void delete(Serializable[] ids) {
		// 仅将状态标记为已删除
		Map<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("status", new Integer(BCConstants.STATUS_DELETED));
		this.update(ids, attrs);
	}

	public Actor loadByCode(String actorCode) {
		return this.actorDao.loadByCode(actorCode);
	}

	public Actor loadBelong(Long followerId, Integer[] masterTypes) {
		return this.actorDao.loadBelong(followerId, masterTypes);
	}

	public List<Actor> findBelong(Long followerId, Integer[] masterTypes) {
		return this.actorDao.findBelong(followerId, masterTypes);
	}

	public List<Actor> findBelong(Long[] followerIds, Integer[] masterTypes) {
		return this.actorDao.findBelong(followerIds, masterTypes);
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

	public List<Actor> findFollowerWithName(Long masterId, String followerName,
			Integer[] relationTypes, Integer[] followerTypes,
			Integer[] followerStatuses) {
		return this.actorDao.findFollowerWithName(masterId, followerName,
				relationTypes, followerTypes, followerStatuses);
	}

	public List<Actor> findFollwerWithIds(Long[] masterIds,
		   Integer[] relationTypes, Integer[] followerTypes) {
		return this.actorDao.findFollowersByMastersId(masterIds,
				relationTypes, followerTypes);
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

	public List<Resource> findCanUseModules(Long actorId) {
		return this.actorDao.findCanUseModules(actorId);
	}

	public Actor save4belong(Actor follower, Long belongId) {
		return this.actorDao.save4belong(follower, belongId);
	}

	public Actor save4belong(Actor follower, Long[] belongIds) {
		return this.actorDao.save4belong(follower, belongIds);
	}

	public List<Actor> find(Integer[] actorTypes, Integer[] actorStatues) {
		return this.actorDao.find(actorTypes, actorStatues);
	}

	public List<ActorHistory> findHistory(Integer[] actorTypes,
			Integer[] actorStatues) {
		return this.actorDao.findHistory(actorTypes, actorStatues);
	}

	public List<Map<String, String>> find4option(Integer[] actorTypes,
			Integer[] actorStatues) {
		return this.actorDao.find4option(actorTypes, actorStatues);
	}

	public List<Map<String, String>> findHistory4option(Integer[] actorTypes,
			Integer[] actorStatues) {
		return this.actorDao.findHistory4option(actorTypes, actorStatues);
	}

	public String loadActorNameByCode(String actorCode) {
		return this.actorDao.loadActorNameByCode(actorCode);
	}
	
	public String loadActorFullNameByCode(String actorCode) {
		return this.actorDao.loadActorFullNameByCode(actorCode);
	}

	public List<Actor> findByName(String actorName, Integer[] actorTypes,
			Integer[] actorStatues) {
		return this.actorDao.findByName(actorName, actorTypes, actorStatues);
	}

	public boolean isUnique(Long id, String code, int type) {
		return this.actorDao.isUnique(id, code, type);
	}

	public String[] findMailAddressByGroup(List<String> groupCodes) {
		return this.actorDao.findMailAddressByGroup(groupCodes);
	}

	public String[] findMailAddressByUser(String[] userCodes) {
		return this.actorDao.findMailAddressByUser(userCodes);
	}

	public List<Actor> findUser(Long organizationId, Integer[] status) {
		return this.actorDao.findUser(organizationId, status);
	}

	public List<Actor> findDescendantUser(Long organizationId,
			Integer[] status, Integer... descendantOrganizationTypes) {
		return this.actorDao.findDescendantUser(organizationId, status, descendantOrganizationTypes);
	}

	@Override
	public List<String> findFollowerCode(String masterCode, Integer[] relationTypes
			, Integer[] followerTypes, Integer[] followerStatuses) {
		return this.actorDao.findFollowerCode(masterCode, relationTypes, followerTypes, followerStatuses);
	}
}
