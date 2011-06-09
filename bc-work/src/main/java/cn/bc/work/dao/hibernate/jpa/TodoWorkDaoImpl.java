package cn.bc.work.dao.hibernate.jpa;

import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.work.dao.TodoWorkDao;
import cn.bc.work.domain.TodoWork;

/**
 * 待办事务Dao接口的实现
 * 
 * @author dragon
 * 
 */
public class TodoWorkDaoImpl extends HibernateCrudJpaDao<TodoWork> implements TodoWorkDao {
}
