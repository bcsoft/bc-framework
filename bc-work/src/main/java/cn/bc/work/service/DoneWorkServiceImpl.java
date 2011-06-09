package cn.bc.work.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.work.dao.DoneWorkDao;
import cn.bc.work.domain.DoneWork;

/**
 * 已办事务service接口的实现
 * 
 * @author dragon
 * 
 */
public class DoneWorkServiceImpl extends DefaultCrudService<DoneWork> implements
		DoneWorkService {
	private DoneWorkDao doneWorkDao;

	@Autowired
	public void setDoneWorkDao(DoneWorkDao doneWorkDao) {
		this.doneWorkDao = doneWorkDao;
		this.setCrudDao(doneWorkDao);
	}
}
