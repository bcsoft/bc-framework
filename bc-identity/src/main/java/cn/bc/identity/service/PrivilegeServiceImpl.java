package cn.bc.identity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import cn.bc.identity.dao.ActorDao;
import cn.bc.identity.dao.PrivilegeDao;

@Transactional
public class PrivilegeServiceImpl implements PrivilegeService {
	
	@Autowired
	private PrivilegeDao privilegeDao;
	@Autowired
	private ActorDao actorDao;
	
	@Override
	public boolean addUser(long roleId, long userId) {
		long actorId = privilegeDao.getActorbyHistoryActor(userId);
		if(privilegeDao.ActorRelationIsExist(roleId, userId)<=0) {
			privilegeDao.addUser(roleId, actorId);
		}
		return true;
		
	}

	@Override
	public boolean addGroup(long roleId, long groupId) {
		if(privilegeDao.ActorRelationIsExist(roleId, groupId)<=0) {
			privilegeDao.addActorByRole(roleId, groupId);
		}
		return true;
	}

	

	@Override
	public boolean addUnitOrDep(long roleId, long unitOrDepId) {
		if(privilegeDao.ActorRelationIsExist(roleId, unitOrDepId)<=0) {
			privilegeDao.addActorByRole(roleId, unitOrDepId);
		}
		return true;
	}

	@Override
	public boolean deleteActor(long roleId, long actorId) {
		return privilegeDao.deleteActorByRole(roleId, actorId)>0;
	}

	@Override
	public boolean deleteResource(long roleId, long resourceId) {
		return privilegeDao.deleteResourceByRole(roleId, resourceId)>0;
	}

	@Override
	public boolean addResource(long roleId, long resourceId) {
		if(privilegeDao.RoleResouceIsExist(roleId, resourceId)<=0) {
			privilegeDao.addResourceByRole(roleId, resourceId);
		}
		return true;
	}

}
