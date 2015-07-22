package cn.bc.scheduler.dao.jpa;

import cn.bc.BCConstants;
import cn.bc.orm.jpa.JpaCrudDao;
import cn.bc.scheduler.dao.ScheduleJobDao;
import cn.bc.scheduler.domain.ScheduleJob;

import java.util.List;

/**
 * 调度任务DAO的JPA实现
 *
 * @author dragon
 * @since 2011-08-30
 */
public class ScheduleJobDaoImpl extends JpaCrudDao<ScheduleJob> implements ScheduleJobDao {
	public List<ScheduleJob> findAllEnabled() {
		String hql = "from ScheduleJob t where t.status=? order by t.orderNo";
		return executeQuery(hql, new Object[]{BCConstants.STATUS_ENABLED});
	}

	public List<ScheduleJob> findAll() {
		return executeQuery("from ScheduleJob t order by t.orderNo", (Object[]) null);
	}
}