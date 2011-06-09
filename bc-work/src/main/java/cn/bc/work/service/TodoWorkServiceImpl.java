package cn.bc.work.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.work.dao.TodoWorkDao;
import cn.bc.work.domain.TodoWork;

/**
 * 待办事务service接口的实现
 * 
 * @author dragon
 * 
 */
public class TodoWorkServiceImpl extends DefaultCrudService<TodoWork> implements
		TodoWorkService {
	private TodoWorkDao todoWorkDao;

	@Autowired
	public void setTodoWorkDao(TodoWorkDao todoWorkDao) {
		this.todoWorkDao = todoWorkDao;
		this.setCrudDao(todoWorkDao);
	}
}
