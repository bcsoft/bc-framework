package cn.bc.report.dao.jpa;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.orm.jpa.JpaCrudDao;
import cn.bc.report.dao.ReportTaskDao;
import cn.bc.report.domain.ReportTask;

import java.util.List;

/**
 * 报表任务Dao接口的实现
 *
 * @author lbj
 */
public class ReportTaskDaoImpl extends JpaCrudDao<ReportTask> implements ReportTaskDao {
	public List<ReportTask> findAllEnabled() {
		return this
				.createQuery()
				.condition(new AndCondition()
						.add(new EqualsCondition("status", BCConstants.STATUS_ENABLED))
						.add(new OrderCondition("fileDate", Direction.Asc)))
				.list();
	}
}