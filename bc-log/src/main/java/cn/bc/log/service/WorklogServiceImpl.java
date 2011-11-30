package cn.bc.log.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.log.dao.WorklogDao;
import cn.bc.log.domain.Worklog;

/**
 * 工作日志service接口的实现
 * 
 * @author dragon
 * 
 */
public class WorklogServiceImpl extends DefaultCrudService<Worklog> implements
		WorklogService {
	private WorklogDao worklogDao;

	@Autowired
	public void setWorklogDao(WorklogDao worklogDao) {
		this.worklogDao = worklogDao;
		this.setCrudDao(worklogDao);
	}

	public List<Worklog> find(String ptype, String pid) {
		return this.worklogDao.find(ptype, pid);
	}
}
