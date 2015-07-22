package cn.bc.scheduler.dao.jpa;

import cn.bc.orm.jpa.JpaCrudDao;
import cn.bc.scheduler.dao.ScheduleLogDao;
import cn.bc.scheduler.domain.ScheduleLog;

/**
 * 任务调度日志DAO的Hibernate JPA实现
 *
 * @author dragon
 * @since 2011-08-30
 */
public class ScheduleLogDaoImpl extends JpaCrudDao<ScheduleLog> implements ScheduleLogDao {
}