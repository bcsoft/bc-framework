package cn.bc.acl.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.acl.dao.AccessActorDao;
import cn.bc.acl.domain.AccessActor;
import cn.bc.acl.domain.AccessHistory;
import cn.bc.core.exception.CoreException;
import cn.bc.identity.domain.Actor;

/**
 * 访问控制Service接口的默认实现
 * 
 * @author dragon
 * 
 */
public class AccessServiceImpl implements AccessService {
	
	private AccessActorDao accessActorDao;
	
	@Autowired
	public void setAccessActorDao(AccessActorDao accessActorDao) {
		this.accessActorDao = accessActorDao;
	}

	public void doAccessControl(Long docId, int docType, int accessType,
			Long[] visitorIDs) {
		throw new CoreException("unimplement method");
	}

	public void saveAccessHistory(Long docId, int docType, int accessType,
			Long visitorHID) {
		// throw new CoreException("unimplement method");
	}

	public List<Actor> findVisitor(Long docId, int docType, int accessType) {
		throw new CoreException("unimplement method");
	}

	public List<AccessHistory> findVisitHistory(Long docId, int docType,
			int accessType, Long visitorHID) {
		throw new CoreException("unimplement method");
	}

	public boolean canVisit(Long docId, int docType, int accessType,
			Long visitorID) {
		throw new CoreException("unimplement method");
	}

	public boolean hadVisit(Long docId, int docType, int accessType,
			Long visitorID) {
		throw new CoreException("unimplement method");
	}

	public int[] visitedCount(Long docId, int docType, int accessType) {
		// TODO Auto-generated method stub
		return new int[] { 0, 0 };
	}
	
	
	public List<AccessActor> find(Actor actor){
		return this.accessActorDao.find(actor);
	}
	
	public List<AccessActor> find(Actor actor,String docType){
		return this.accessActorDao.find(actor,docType);
	}
	
}