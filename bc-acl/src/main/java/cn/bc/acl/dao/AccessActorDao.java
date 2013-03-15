package cn.bc.acl.dao;

import java.util.List;

import cn.bc.acl.domain.AccessActor;
import cn.bc.core.dao.CrudDao;

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
	 * @param id 访问对象id1
	 * @return
	 */
	List<AccessActor> findByPid(Long id);
	
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
	 * @param actor 访问人id
	 * @return
	 */
	List<AccessActor> findByAid(Long id);
	
	/**
	 * 获取访问者对象
	 * @param actor访问人id
	 * @param docType 文档类型
	 * @return
	 */
	List<AccessActor> findByDocType(Long id,String docType);
	
	/**
	 * 获取访问者对象
	 * @param actor访问人id
	 * @param docId 文档Id
	 * @return
	 */
	List<AccessActor> findByDocId(Long id,String docId);
	
	/**
	 * 获取访问者对象
	 * @param actor访问人id
	 * @param docId 文档Id
	 * @param docType 文档类型
	 * @return
	 */
	List<AccessActor> findByDocIdType(Long id,String docId,String docType);
}
