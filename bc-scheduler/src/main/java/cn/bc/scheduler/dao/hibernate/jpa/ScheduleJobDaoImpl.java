package cn.bc.scheduler.dao.hibernate.jpa;

import java.util.List;

import cn.bc.BCConstants;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.scheduler.dao.ScheduleJobDao;
import cn.bc.scheduler.domain.ScheduleJob;

/**
 * 调度任务DAO的Hibernate JPA实现
 * 
 * @author dragon
 * @since 2011-08-30
 */
public class ScheduleJobDaoImpl extends HibernateCrudJpaDao<ScheduleJob>
		implements ScheduleJobDao {
	@SuppressWarnings("unchecked")
	public List<ScheduleJob> findAllEnabled() {
		String hql = "from ScheduleJob t where t.status=? order by t.orderNo";
		return (List<ScheduleJob>) this.getJpaTemplate().find(hql,
				new Integer(BCConstants.STATUS_ENABLED));
	}

	@SuppressWarnings("unchecked")
	public List<ScheduleJob> findAll() {
		String hql = "from ScheduleJob t order by t.orderNo";
		return (List<ScheduleJob>) this.getJpaTemplate().find(hql);
	}
}
