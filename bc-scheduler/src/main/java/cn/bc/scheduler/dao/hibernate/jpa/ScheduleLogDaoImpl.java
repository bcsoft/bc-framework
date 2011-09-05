package cn.bc.scheduler.dao.hibernate.jpa;

import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.scheduler.dao.ScheduleLogDao;
import cn.bc.scheduler.domain.ScheduleLog;

/**
 * 任务调度日志DAO的Hibernate JPA实现
 * 
 * @author dragon
 * @since 2011-08-30
 */
public class ScheduleLogDaoImpl extends HibernateCrudJpaDao<ScheduleLog>
		implements ScheduleLogDao {
}
