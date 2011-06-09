package cn.bc.work.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.work.dao.WorkDao;
import cn.bc.work.domain.Work;

/**
 * 工作事务service接口的实现
 * 
 * @author dragon
 * 
 */
public class WorkServiceImpl extends DefaultCrudService<Work> implements
		WorkService {
	private WorkDao workDao;

	@Autowired
	public void setWorkDao(WorkDao workDao) {
		this.workDao = workDao;
		this.setCrudDao(workDao);
	}
}
