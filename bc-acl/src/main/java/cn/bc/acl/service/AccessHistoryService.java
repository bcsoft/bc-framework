package cn.bc.acl.service;

import java.util.List;

import cn.bc.acl.domain.AccessHistory;
import cn.bc.core.service.CrudService;

/**
 * 访问历史Service接口
 * 
 * @author lbj
 * 
 */
public interface AccessHistoryService extends CrudService<AccessHistory> {
	/**
	 * 获取某一访问对象中的访问历史
	 * 
	 * @param pid 访问对象id1
	 * @return
	 */
	List<AccessHistory> findByDoc(Long pid);
	
}