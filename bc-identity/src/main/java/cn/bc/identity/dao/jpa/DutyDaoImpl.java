package cn.bc.identity.dao.jpa;

import cn.bc.core.Page;
import cn.bc.core.query.condition.AdvanceCondition;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.identity.dao.DutyDao;
import cn.bc.identity.domain.Duty;
import cn.bc.orm.jpa.JpaCrudDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 职务 Dao 实现
 *
 * @author dragon 2016-07-19
 */
@Named
@Singleton
public class DutyDaoImpl extends JpaCrudDao<Duty> implements DutyDao {
	private static Logger logger = LoggerFactory.getLogger(DutyDaoImpl.class);
	@Inject
	private JdbcTemplate jdbcTemplate;
	@PersistenceContext
	private EntityManager em;

	@Override
	public Page<Duty> page(Integer pageNo, Integer pageSize, List<AdvanceCondition> condition) {
		if (pageNo == null || pageNo < 1) pageNo = 1;
		if (pageSize == null || pageSize < 1) pageSize = Page.DEFAULT_PAGE_SIZE;

		// TODO condition

		// 查一页数据
		String ql4list = "select id, code, name from Duty";
		List<Duty> list = em.createQuery(ql4list, Duty.class)
				.setFirstResult((pageNo - 1) * pageSize)
				.setMaxResults(pageSize)
				.getResultList();

		// 查总数
		String ql4count = "select count(*) from Duty";
		int totalCount = em.createQuery(ql4list, Integer.class)
				.getSingleResult();

		return new Page<>(pageNo, pageSize, totalCount, list);
	}

	@Override
	public Map<String, Object> dataByJpaQuery(int pageNo, int pageSize, String search) {
		pageNo = Math.max(1, pageNo);
		pageSize = Math.max(25, pageSize);
		Map<String, Object> o = new HashMap<>();
		o.put("pageNo", pageNo);
		o.put("pageSize", pageSize);

		// 构建查询
		String countSql = "select count(*) from Duty";
		String rowsSql = "select d from Duty d";
		String orderSql = " order by code";
		boolean hasSearch = search != null && !search.isEmpty();
		Condition c = null;
		TypedQuery<Long> countQuery;
		TypedQuery<Duty> rowsQuery;
		if (hasSearch) {
			String[] strings = new String[]{"code", "name"};            // 模糊查询的字段
			c = ConditionUtils.toFuzzyCondition(search, strings, false);     // jpa 不支持 ilike
			countSql += " where " + c.getExpression();
			rowsSql += " where " + c.getExpression() + orderSql;
		} else {
			rowsSql += orderSql;
		}
		countQuery = em.createQuery(countSql, Long.class);
		rowsQuery = em.createQuery(rowsSql, Duty.class)
				.setFirstResult((pageNo - 1) * pageSize)
				.setMaxResults(pageSize);

		// 注入查询参数
		if (c != null) {
			int i = 0;
			for (Object value : c.getValues()) {
				i++;
				countQuery.setParameter(i, value);
				rowsQuery.setParameter(i, value);
			}
		}

		// log
		if (logger.isDebugEnabled()) {
			logger.debug("args={}, countSql={}, rowsSql={}",
					StringUtils.collectionToCommaDelimitedString(c == null ? null : c.getValues()),
					countSql, rowsSql
			);
		}

		// 执行查询
		o.put("count", countQuery.getSingleResult());   // 总数
		o.put("rows", rowsQuery.getResultList());       // 页数据

		return o;
	}

	public Map<String, Object> dataByNativeQuery(int pageNo, int pageSize, String search) {
		pageNo = Math.max(1, pageNo);
		pageSize = Math.max(25, pageSize);
		Map<String, Object> o = new HashMap<>();
		o.put("pageNo", pageNo);
		o.put("pageSize", pageSize);

		// 构建查询
		String countSql = "select count(*) from bc_identity_duty";
		String rowsSql = "select id, code, name from bc_identity_duty";
		String orderSql = " order by code";
		boolean hasSearch = search != null && !search.isEmpty();
		Condition c = null;
		Query countQuery;
		Query rowsQuery;
		if (hasSearch) {
			String[] strings = new String[]{"code", "name"};            // 模糊查询的字段
			c = ConditionUtils.toFuzzyCondition(search, strings, true);     // jdbc 支持 ilike
			countSql += " where " + c.getExpression();
			rowsSql += " where " + c.getExpression() + orderSql;
		} else {
			rowsSql += orderSql;
		}
		countQuery = em.createNativeQuery(countSql);
		rowsQuery = em.createNativeQuery(rowsSql)
				.setFirstResult((pageNo - 1) * pageSize)
				.setMaxResults(pageSize);

		// 注入查询参数
		if (c != null) {
			int i = 0;
			for (Object value : c.getValues()) {
				i++;
				countQuery.setParameter(i, value);
				rowsQuery.setParameter(i, value);
			}
		}

		// log
		if (logger.isDebugEnabled()) {
			logger.debug("args={}, countSql={}, rowsSql={}",
					StringUtils.collectionToCommaDelimitedString(c == null ? null : c.getValues()),
					countSql, rowsSql
			);
		}

		// 执行查询
		o.put("count", countQuery.getSingleResult());                   // 总数
		o.put("rows", arrayListToMapList(rowsQuery.getResultList()));   // 页数据

		return o;
	}

	private List<Map<String, Object>> arrayListToMapList(List<Object[]> resultList) {
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> m;
		for (Object[] o : resultList) {
			m = new HashMap<>();
			list.add(m);
			m.put("id", o[0]);
			m.put("code", o[1]);
			m.put("name", o[2]);
		}
		return list;
	}
}