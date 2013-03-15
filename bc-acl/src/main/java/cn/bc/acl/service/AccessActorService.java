package cn.bc.acl.service;

import java.util.List;

import cn.bc.acl.domain.AccessActor;
import cn.bc.core.service.CrudService;

/**
 * 访问者Service接口
 * 
 * @author lbj
 * 
 */
public interface AccessActorService extends CrudService<AccessActor> {
	
	
	/**
	 * 获取某一访问对象中的某一访问者
	 * 
	 * @param pid 访问对象id
	 * @param aid 访问者id
	 * @return
	 */
	AccessActor load(Long pid,Long aid);
	
	/**
	 * 获取某一访问对象中的全部访问者
	 * 
	 * @param pid 访问对象id1
	 * @return
	 */
	List<AccessActor> findByPid(Long pid);
	
	
	/**
	 * CRUD'D:删除对象
	 * @param accessActor 对象
	 */
	void delete(AccessActor accessActor);
	
	/**
	 * CRUD'D:删除对象
	 * @param accessActors 对象
	 */
	void delete(List<AccessActor> accessActors);
}