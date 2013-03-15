package cn.bc.acl.service;

import java.util.List;

import cn.bc.acl.domain.AccessActor;

/**
 * 访问控制Service接口
 * 
 * @author dragon
 * 
 */
public interface AccessService{
	
	
	/**
	 * 获取访问者对象
	 * @param id 访问人id
	 * @return
	 */
	List<AccessActor> findByAid(Long id);
	
	/**
	 * 获取访问者对象
	 * @param id 访问人id
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