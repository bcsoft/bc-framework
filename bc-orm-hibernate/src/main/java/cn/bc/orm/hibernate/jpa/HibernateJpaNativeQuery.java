package cn.bc.orm.hibernate.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import cn.bc.core.Page;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.JdbcUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.orm.hibernate.HibernateUtils;

/**
 * 基于hibernate的查询接口实现
 * 
 * @author dragon
 * 
 * @param <T>
 */
public class HibernateJpaNativeQuery<T extends Object> implements
		cn.bc.core.query.Query<T> {
	protected static final Log logger = LogFactory
			.getLog("cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery");
	private JpaTemplate jpaTemplate;
	private Condition condition;
	private SqlObject<T> sqlObject;

	private HibernateJpaNativeQuery() {
	}

	/**
	 * 构造一个基于hibernate的Query实现
	 * 
	 * @param jpaTemplate
	 */
	public HibernateJpaNativeQuery(JpaTemplate jpaTemplate,
			SqlObject<T> sqlObject) {
		this();
		this.jpaTemplate = jpaTemplate;
		this.sqlObject = sqlObject;
		Assert.notNull(jpaTemplate, "jpaTemplate is required");
		Assert.notNull(sqlObject, "sqlObject is required");
		Assert.notNull(sqlObject.getSql(), "sqlObject.getSql() is required");
	}

	private String getSql() {
		String sql_ = sqlObject.getSql();
		if (condition != null) {
			String expression = this.condition.getExpression();
			if (condition instanceof OrderCondition) {
				sql_ += " order by " + expression;
			} else {
				if (expression != null && expression.startsWith("order by")) {
					sql_ += " " + expression;
				} else if (expression != null && expression.length() > 0) {
					sql_ += " where " + expression;
				}
			}
		}
		return sql_;
	}

	// --implements Query

	public HibernateJpaNativeQuery<T> condition(Condition condition) {
		this.condition = condition;
		return this;
	}

	public int count() {
		String queryTemp = HibernateUtils.removeOrderBy(getSql());
		if (!queryTemp.startsWith("select")) {
			queryTemp = "select count(*) " + queryTemp;
		} else {
			queryTemp = "select count(*) "
					+ HibernateUtils.removeSelect(queryTemp);
		}

		// 合并参数
		final List<Object> args = new ArrayList<Object>();
		if (sqlObject.getArgs() != null)
			args.addAll(sqlObject.getArgs());
		if (condition != null) {
			List<Object> values = condition.getValues();
			if (values != null)
				args.addAll(values);
		}

		final String queryString = queryTemp;
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + queryString);
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args));
		}
		Number c = jpaTemplate.execute(new JpaCallback<Number>() {
			public Number doInJpa(EntityManager em) throws PersistenceException {
				Query queryObject = em.createNativeQuery(queryString);
				// jpaTemplate.prepareQuery(queryObject);

				// 注入参数
				int i = 0;
				for (Object value : args) {
					queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
					i++;
				}

				// mysql返回java.math.BigInteger
				return (Number) queryObject.getSingleResult();
			}
		});
		return c.intValue();
	}

	public List<T> list() {
		final String hql = getSql();

		// 合并参数
		final List<Object> args = new ArrayList<Object>();
		if (sqlObject.getArgs() != null)
			args.addAll(sqlObject.getArgs());
		if (condition != null) {
			List<Object> values = condition.getValues();
			if (values != null)
				args.addAll(values);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args));
		}

		return executeNativeSql(jpaTemplate, hql, args.toArray(),
				sqlObject.getRowMapper());
	}

	public static <T> List<T> executeNativeSql(JpaTemplate jpaTemplate,
			final String hql, final Object[] args, final RowMapper<T> rowMapper) {
		return jpaTemplate.execute(new JpaCallback<List<T>>() {
			@SuppressWarnings("unchecked")
			public List<T> doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(hql);
				// jpaTemplate.prepareQuery(queryObject);

				// 注入参数
				if (args != null) {
					int i = 0;
					for (Object value : args) {
						queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
						i++;
					}
				}
				return JdbcUtils.mapRows(
						(List<Object[]>) queryObject.getResultList(), rowMapper);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<T> list(final int pageNo, final int pageSize) {
		final int _pageSize = pageSize < 1 ? 1 : pageSize;
		final String hql = getSql();

		// 合并参数
		final List<Object> args = new ArrayList<Object>();
		if (sqlObject.getArgs() != null)
			args.addAll(sqlObject.getArgs());
		if (condition != null) {
			List<Object> values = condition.getValues();
			if (values != null)
				args.addAll(values);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("pageNo=" + pageNo);
			logger.debug("pageSize=" + _pageSize);
			logger.debug("hql=" + hql);
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args));
		}
		return jpaTemplate.execute(new JpaCallback<List<T>>() {
			public List<T> doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(hql);
				// jpaTemplate.prepareQuery(queryObject);

				// 注入参数
				int i = 0;
				for (Object value : args) {
					queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
					i++;
				}
				queryObject.setFirstResult(Page.getFirstResult(pageNo,
						_pageSize));
				queryObject.setMaxResults(_pageSize);

				return JdbcUtils.mapRows(
						(List<Object[]>) queryObject.getResultList(),
						sqlObject.getRowMapper());
			}

		});
	}

	public Page<T> page(int pageNo, int pageSize) {
		return new Page<T>(pageNo, pageSize, this.count(), this.list(pageNo,
				pageSize));
	}

	@SuppressWarnings("unchecked")
	public T singleResult() {
		// 合并参数
		final List<Object> args = new ArrayList<Object>();
		if (sqlObject.getArgs() != null)
			args.addAll(sqlObject.getArgs());
		if (condition != null) {
			List<Object> values = condition.getValues();
			if (values != null)
				args.addAll(values);
		}

		try {
			return jpaTemplate.execute(new JpaCallback<T>() {
				public T doInJpa(EntityManager em) throws PersistenceException {
					Query queryObject = em.createNativeQuery(getSql());
					// jpaTemplate.prepareQuery(queryObject);
					int i = 0;
					for (Object value : args) {
						queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
						i++;
					}
					Object[] r = (Object[]) queryObject.getSingleResult();

					if (r != null) {
						return sqlObject.getRowMapper() != null ? sqlObject
								.getRowMapper().mapRow(r, 0) : (T) r;
					} else {
						return null;
					}
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<Object> listWithSelect(String select) {
		throw new CoreException("unsupport method.");
	}
}
