package cn.bc.orm.hibernate.jpa;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.util.StringUtils;

import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.orm.hibernate.HibernateUtils;

/**
 * 基于hibernate的查询接口实现
 * 
 * @author dragon
 * 
 * @param <T>
 */
public class HibernateJpaQuery<T extends Object> implements
		cn.bc.core.query.Query<T> {
	protected final Log logger = LogFactory.getLog(getClass());
	private JpaTemplate jpaTemplate;
	private Condition condition;
	private Class<T> entityClass;

	@SuppressWarnings("unchecked")
	private HibernateJpaQuery() {
		// 这个需要子类中指定T为实际的类才有效
		Type type = this.getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			type = ((ParameterizedType) type).getActualTypeArguments()[0];
			if (type instanceof Class)
				this.entityClass = (Class<T>) type;
		}
		// System.out.println("T.class="+this.persistentClass);
	}

	/**
	 * 构造一个基于hibernate的Query实现
	 * 
	 * @param jpaTemplate
	 *            hibernate会话
	 */
	public HibernateJpaQuery(JpaTemplate jpaTemplate) {
		this();
		this.jpaTemplate = jpaTemplate;
	}

	public HibernateJpaQuery(JpaTemplate jpaTemplate, Class<T> persistentClass) {
		this(jpaTemplate);
		this.entityClass = persistentClass;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<T> persistentClass) {
		this.entityClass = persistentClass;
	}

	// --implements Query

	public HibernateJpaQuery<T> condition(Condition condition) {
		this.condition = condition;
		return this;
	}

	public int count() {
		String queryTemp = HibernateUtils.removeOrderBy(getHql());
		if (!queryTemp.startsWith("select")) {
			queryTemp = "select count(*) " + queryTemp;
		} else {
			queryTemp = "select count(*) "
					+ HibernateUtils.removeSelect(queryTemp);
		}
		final String queryString = queryTemp;
		Long c = jpaTemplate.execute(new JpaCallback<Long>() {
			public Long doInJpa(EntityManager em) throws PersistenceException {
				Query queryObject = em.createQuery(queryString);
				jpaTemplate.prepareQuery(queryObject);
				if (condition != null && condition.getValues() != null) {
					int i = 0;
					for (Object value : condition.getValues()) {
						queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
						i++;
					}
				}
				return (Long) queryObject.getSingleResult();
			}
		});
		return c.intValue();
	}

	@SuppressWarnings("unchecked")
	public List<T> list() {
		return jpaTemplate.execute(new JpaCallback<List<T>>() {
			public List<T> doInJpa(EntityManager em)
					throws PersistenceException {
				String hql = getHql();
				Query queryObject = em.createQuery(hql);
				jpaTemplate.prepareQuery(queryObject);
				if (condition != null && condition.getValues() != null) {
					int i = 0;
					for (Object value : condition.getValues()) {
						queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
						i++;
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug("hql=" + hql);
					logger.debug("args="
							+ (condition != null ? StringUtils
									.collectionToCommaDelimitedString(condition
											.getValues()) : "null"));
				}
				return (List<T>) queryObject.getResultList();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<T> list(final int pageNo, final int pageSize) {
		final int _pageSize = pageSize < 1 ? 1 : pageSize;
		return jpaTemplate.execute(new JpaCallback<List<T>>() {
			public List<T> doInJpa(EntityManager em)
					throws PersistenceException {
				String hql = getHql();
				Query queryObject = em.createQuery(hql);
				jpaTemplate.prepareQuery(queryObject);
				if (condition != null && condition.getValues() != null) {
					int i = 0;
					for (Object value : condition.getValues()) {
						queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
						i++;
					}
				}
				queryObject.setFirstResult(Page.getFirstResult(pageNo,
						_pageSize));
				queryObject.setMaxResults(_pageSize);
				if (logger.isDebugEnabled()) {
					logger.debug("pageNo=" + pageNo);
					logger.debug("pageSize=" + _pageSize);
					logger.debug("hql=" + hql);
					logger.debug("args="
							+ (condition != null ? StringUtils
									.collectionToCommaDelimitedString(condition
											.getValues()) : "null"));
				}
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
					Query queryObject = em.createQuery(getHql());
					jpaTemplate.prepareQuery(queryObject);
					if (condition != null && condition.getValues() != null) {
						int i = 0;
						for (Object value : condition.getValues()) {
							queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
							i++;
						}
					}
					return (T) queryObject.getSingleResult();
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	// --private

	protected String getHql() {
		String hql = "from " + this.getEntityClass().getSimpleName();
		if (condition != null){
			if (condition instanceof OrderCondition){
				hql += " order by " + this.condition.getExpression();
			}else{
				hql += " where " + this.condition.getExpression();
			}
		}
		return hql;
	}

	@SuppressWarnings("unchecked")
	public List<Object> listWithSelect(final String select) {
		return jpaTemplate.execute(new JpaCallback<List<Object>>() {
			public List<Object> doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createQuery("select " + select + " "
						+ getHql());
				jpaTemplate.prepareQuery(queryObject);
				if (condition != null && condition.getValues() != null) {
					int i = 0;
					for (Object value : condition.getValues()) {
						queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
						i++;
					}
				}
				return (List<Object>) queryObject.getResultList();
			}
		});
	}
}
