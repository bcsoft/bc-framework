package cn.bc.work.dao.hibernate.jpa;

import cn.bc.orm.jpa.JpaCrudDao;
import cn.bc.work.dao.WorkDao;
import cn.bc.work.domain.Work;

/**
 * 工作事务Dao接口的实现
 *
 * @author dragon
 */
public class WorkDaoImpl extends JpaCrudDao<Work> implements WorkDao {
}
