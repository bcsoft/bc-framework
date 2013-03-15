package cn.bc.acl.service;

import java.util.List;

import cn.bc.acl.domain.AccessActor;
import cn.bc.acl.domain.AccessDoc;
import cn.bc.core.service.CrudService;

/**
 * 访问对象Service接口
 * 
 * @author lbj
 * 
 */
public interface AccessDocService extends CrudService<AccessDoc> {
	
	/**
	 *保存访问对象与访问者
	 * 
	 * @param entity
	 * @param accessActors
	 */
	AccessDoc save4AccessActors(AccessDoc entity,List<AccessActor> accessActors);
	
	/**
	 * 获取访问对象
	 * 
	 * @param docId 文档标识
	 * @param docType 文档类型
	 * @return
	 */
	AccessDoc load(String docId,String docType);
}