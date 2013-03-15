package cn.bc.acl.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.acl.dao.AccessDocDao;
import cn.bc.acl.domain.AccessActor;
import cn.bc.acl.domain.AccessDoc;
import cn.bc.core.exception.CoreException;
import cn.bc.core.service.DefaultCrudService;

/**
 * 访问对象Service接口的默认实现
 * 
 * @author lbj
 * 
 */
public class AccessDocServiceImpl extends DefaultCrudService<AccessDoc> implements AccessDocService {
	private AccessDocDao accessDocDao;
	private AccessActorService accessActorService;

	@Autowired
	public void setAccessDocDao(AccessDocDao accessDocDao) {
		this.setCrudDao(accessDocDao);
		this.accessDocDao = accessDocDao;
	}
	
	@Autowired
	public void setAccessActorService(AccessActorService accessActorService) {
		this.accessActorService = accessActorService;
	}

	public AccessDoc save4AccessActors(AccessDoc entity,List<AccessActor> accessActors) {
		if(entity==null)throw new CoreException("entity is null!");
		if(accessActors==null)throw new CoreException("accessActors is null!");
		
		if(!entity.isNew())
			//先清空已保存的访问者
			this.accessActorService.delete(this.accessActorService.findByPid(entity.getId()));
		
		//保存对象
		AccessDoc e=this.accessDocDao.save(entity);
		
		//循环保存访问者
		for(AccessActor aa:accessActors){
			aa.setAccessDoc(e);
			this.accessActorService.save(aa);
		}
		
		return e;
	}

	@Override
	public void delete(Serializable id) {
		AccessDoc e=this.accessDocDao.load(id);
		//先清空已保存的访问者
		this.accessActorService.delete(this.accessActorService.findByPid(e.getId()));
		//删除对象
		this.accessDocDao.delete(id);
	}

	public AccessDoc load(String docId, String docType) {
		return this.accessDocDao.load(docId, docType);
	}
	
}