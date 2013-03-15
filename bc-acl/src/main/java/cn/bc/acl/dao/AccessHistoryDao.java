package cn.bc.acl.dao;

import java.util.List;

import cn.bc.acl.domain.AccessHistory;
import cn.bc.core.dao.CrudDao;

/**
 * 访问历史Dao接口
 * 
 * @author lbj
 * 
 */
public interface AccessHistoryDao extends CrudDao<AccessHistory> {
	
	/**
	 * 获取某一访问对象中的访问历史
	 * 
	 * @param pid 访问对象id1
	 * @return
	 */
	List<AccessHistory> findByDoc(Long pid);
	
}
