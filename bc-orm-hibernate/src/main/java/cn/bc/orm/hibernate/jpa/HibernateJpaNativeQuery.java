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
	protected final Log logger = LogFactory.getLog(getClass());
	private JpaTemplate jpaTemplate;
	private Condition condition;
	private String sql;
	private List<Object> sqlArgs = new ArrayList<Object>();

	public void setSqlArgs(List<Object> sqlArgs) {
		this.sqlArgs = sqlArgs;
	}

	public HibernateJpaNativeQuery<T> setSql(String sql) {
		this.sql = sql;
		return this;
	}

	private String getSql() {
		String sql_ = sql;
		if (condition != null)
			sql_ += " where " + this.condition.getExpression();
		return sql_;
	}

	private HibernateJpaNativeQuery() {
	}

	/**
	 * 构造一个基于hibernate的Query实现
	 * 
	 * @param jpaTemplate
	 */
	public HibernateJpaNativeQuery(JpaTemplate jpaTemplate) {
		this();
		this.jpaTemplate = jpaTemplate;
		Assert.notNull(jpaTemplate, "jpaTemplate is required");
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
		final String queryString = queryTemp;
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + queryString);
			logger.debug("args="
					+ (condition != null ? StringUtils
							.collectionToCommaDelimitedString(condition
									.getValues()) : "null"));
		}
		Number c = jpaTemplate.execute(new JpaCallback<Number>() {
			public Number doInJpa(EntityManager em) throws PersistenceException {
				Query queryObject = em.createNativeQuery(queryString);
				//jpaTemplate.prepareQuery(queryObject);
				int i = 0;
				for (Object value : sqlArgs) {
					queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
					i++;
				}
				// mysql返回java.math.BigInteger
				return (Number) queryObject.getSingleResult();
			}
		});
		return c.intValue();
	}

	@SuppressWarnings("unchecked")
	public List<T> list() {
		final String hql = getSql();
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("args="
					+ (condition != null ? StringUtils
							.collectionToCommaDelimitedString(condition
									.getValues()) : "null"));
		}
		return jpaTemplate.execute(new JpaCallback<List<T>>() {
			public List<T> doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(hql);
				//jpaTemplate.prepareQuery(queryObject);
				int i = 0;
				for (Object value : sqlArgs) {
					queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
					i++;
				}
				return (List<T>) queryObject.getResultList();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<T> list(final int pageNo, final int pageSize) {
		final int _pageSize = pageSize < 1 ? 1 : pageSize;
		final String hql = getSql();
		if (logger.isDebugEnabled()) {
			logger.debug("pageNo=" + pageNo);
			logger.debug("pageSize=" + _pageSize);
			logger.debug("hql=" + hql);
			logger.debug("args="
					+ (condition != null ? StringUtils
							.collectionToCommaDelimitedString(condition
									.getValues()) : "null"));
		}
		return jpaTemplate.execute(new JpaCallback<List<T>>() {
			public List<T> doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(hql);
				//jpaTemplate.prepareQuery(queryObject);
				int i = 0;
				for (Object value : sqlArgs) {
					queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
					i++;
				}
				queryObject.setFirstResult(Page.getFirstResult(pageNo,
						_pageSize));
				queryObject.setMaxResults(_pageSize);
				return (List<T>) queryObject.getResultList();
			}
		});
	}

	public Page<T> page(int pageNo, int pageSize) {
		return new Page<T>(pageNo, pageSize, this.count(), this.list(pageNo,
				pageSize));
	}

	@SuppressWarnings("unchecked")
	public T singleResult() {
		try {
			return jpaTemplate.execute(new JpaCallback<T>() {
				public T doInJpa(EntityManager em) throws PersistenceException {
					Query queryObject = em.createNativeQuery(getSql());
					//jpaTemplate.prepareQuery(queryObject);
					int i = 0;
					for (Object value : sqlArgs) {
						queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
						i++;
					}
					return (T) queryObject.getSingleResult();
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
