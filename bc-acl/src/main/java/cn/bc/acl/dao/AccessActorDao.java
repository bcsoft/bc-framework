package cn.bc.acl.dao;

import java.util.List;

import cn.bc.acl.domain.AccessActor;
import cn.bc.core.dao.CrudDao;
import cn.bc.identity.domain.Actor;

/**
 * 访问者Dao接口
 * 
 * @author lbj
 * 
 */
public interface AccessActorDao extends CrudDao<AccessActor> {
	
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
	List<AccessActor> find(Long pid);
	
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
	
	/**
	 * 获取访问者对象
	 * @param actor 访问人
	 * @return
	 */
	List<AccessActor> find(Actor actor);
	
	/**
	 * 获取访问者对象
	 * @param actor访问人
	 * @param docType 文档类型
	 * @return
	 */
	List<AccessActor> find(Actor actor,String docType);
}
