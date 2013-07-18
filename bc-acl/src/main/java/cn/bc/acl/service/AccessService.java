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
	
	/**
	 * 获取权限字符串数据
	 * @param docId
	 * @param docType
	 * @param aid
	 * @return 返回null代表未配置此权限
	 */
	String findRolw(String docId,String docType,Long aid);
	
	/**
	 * 判断是否有没权限
	 * @param docId
	 * @param docType
	 * @param aid
	 * @param wildcard 权限字符串统配符 例如有编辑权限："1_",其中_为占位符号
	 * @return true有 false没
	 */
	boolean hasRolw(String docId,String docType,Long aid,String wildcard);
}