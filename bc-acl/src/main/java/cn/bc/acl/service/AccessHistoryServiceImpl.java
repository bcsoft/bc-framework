package cn.bc.acl.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.acl.dao.AccessHistoryDao;
import cn.bc.acl.domain.AccessHistory;
import cn.bc.core.service.DefaultCrudService;

/**
 * 访问历史Service接口的默认实现
 * 
 * @author lbj
 * 
 */
public class AccessHistoryServiceImpl extends DefaultCrudService<AccessHistory> implements AccessHistoryService {
	private AccessHistoryDao accessHistoryDao;

	@Autowired
	public void setAccessHistoryDao(AccessHistoryDao accessHistoryDao) {
		this.setCrudDao(accessHistoryDao);
		this.accessHistoryDao = accessHistoryDao;
	}
	
	
	public List<AccessHistory> findByDoc(Long pid) {
		return this.accessHistoryDao.findByDoc(pid);
	}
	
}