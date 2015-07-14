package cn.bc.report.dao.hibernate.jpa;

import cn.bc.db.jdbc.RowMapper;
import cn.bc.orm.jpa.JpaCrudDao;
import cn.bc.report.dao.ReportHistoryDao;
import cn.bc.report.domain.ReportHistory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 历史报表Dao接口的实现
 *
 * @author lbj
 */
public class ReportHistoryDaoImpl extends JpaCrudDao<ReportHistory> implements ReportHistoryDao {
	public List<Map<String, String>> findCategoryOption() {
		String hql = "SELECT a.category,1";
		hql += " FROM bc_report_history a";
		hql += " GROUP BY a.category";
		return executeNativeQuery(hql, (Object[]) null, new RowMapper<Map<String, String>>() {
			public Map<String, String> mapRow(Object[] rs, int rowNum) {
				Map<String, String> oi = new HashMap<>();
				oi.put("value", rs[0].toString());
				return oi;
			}
		});
	}

	public List<Map<String, String>> findSourceOption() {
		String hql = "SELECT a.source_type,1";
		hql += " FROM bc_report_history a";
		hql += " GROUP BY a.source_type";
		return executeNativeQuery(hql, (Object[]) null, new RowMapper<Map<String, String>>() {
			public Map<String, String> mapRow(Object[] rs, int rowNum) {
				Map<String, String> oi = new HashMap<>();
				oi.put("value", rs[0].toString());
				return oi;
			}
		});
	}
}