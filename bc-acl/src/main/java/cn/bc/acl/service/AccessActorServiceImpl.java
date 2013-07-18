package cn.bc.acl.service;

import java.util.List;

import org.commontemplate.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.acl.dao.AccessActorDao;
import cn.bc.acl.domain.AccessActor;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.LikeRightCondition;
import cn.bc.core.service.DefaultCrudService;

/**
 * 访问者Service接口的默认实现
 * 
 * @author lbj
 * 
 */
public class AccessActorServiceImpl extends DefaultCrudService<AccessActor> implements AccessActorService {
	private AccessActorDao accessActorDao;

	@Autowired
	public void setAccessActorDao(AccessActorDao accessActorDao) {
		this.setCrudDao(accessActorDao);
		this.accessActorDao = accessActorDao;
	}


	public AccessActor load(Long pid, Long aid) {
		return this.accessActorDao.load(pid, aid);
	}


	public List<AccessActor> findByPid(Long pid) {
		return this.accessActorDao.findByPid(pid);
	}


	public void delete(AccessActor accessActor) {
		this.accessActorDao.delete(accessActor);
	}


	public void delete(List<AccessActor> accessActors) {
		this.accessActorDao.delete(accessActors);
	}
	
	public AccessActor load(String docId,String docType,Long aid,String wildcard){
		Assert.assertNotNull(docId);
		Assert.assertNotNull(docType);
		Assert.assertNotNull(aid);
		AndCondition ac = new AndCondition();
		ac.add(new EqualsCondition("accessDoc.docId", docId));
		ac.add(new EqualsCondition("accessDoc.docType", docType));
		ac.add(new EqualsCondition("actor.id", aid));
		if(wildcard !=null && wildcard.length()>0){
			ac.add(new LikeRightCondition("role", wildcard));
		}
		
		return this.createQuery().condition(ac).singleResult();
	}
}