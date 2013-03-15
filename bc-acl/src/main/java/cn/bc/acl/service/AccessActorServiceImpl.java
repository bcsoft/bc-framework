package cn.bc.acl.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.acl.dao.AccessActorDao;
import cn.bc.acl.domain.AccessActor;
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


	public List<AccessActor> find(Long pid) {
		return this.accessActorDao.find(pid);
	}


	public void delete(AccessActor accessActor) {
		this.accessActorDao.delete(accessActor);
	}


	public void delete(List<AccessActor> accessActors) {
		this.accessActorDao.delete(accessActors);
	}
}