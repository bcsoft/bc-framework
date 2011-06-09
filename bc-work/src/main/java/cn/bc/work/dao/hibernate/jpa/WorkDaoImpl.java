package cn.bc.work.dao.hibernate.jpa;

import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.work.dao.WorkDao;
import cn.bc.work.domain.Work;

/**
 * 工作事务Dao接口的实现
 * 
 * @author dragon
 * 
 */
public class WorkDaoImpl extends HibernateCrudJpaDao<Work> implements WorkDao {
}
