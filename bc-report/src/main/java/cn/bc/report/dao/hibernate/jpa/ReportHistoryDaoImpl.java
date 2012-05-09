package cn.bc.report.dao.hibernate.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bc.db.jdbc.RowMapper;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;
import cn.bc.report.dao.ReportHistoryDao;
import cn.bc.report.domain.ReportHistory;

/**
 * 历史报表Dao接口的实现
 * 
 * @author lbj
 * 
 */
public class ReportHistoryDaoImpl extends HibernateCrudJpaDao<ReportHistory>
		implements ReportHistoryDao {

	public List<Map<String, String>> findCategoryOption() {
		String hql="SELECT a.category,1";
		   hql+=" FROM bc_report_history a";
		   hql+=" GROUP BY a.category"; 
		 return	HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), hql,null
		 	,new RowMapper<Map<String, String>>() {
				public Map<String, String> mapRow(Object[] rs, int rowNum) {
					Map<String, String> oi = new HashMap<String, String>();
					int i = 0;
					oi.put("value", rs[i++].toString());
					return oi;
				}
		});
	}

	
}
