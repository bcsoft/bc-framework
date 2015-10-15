package cn.bc.orm.jpa;

import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.OrderCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 基于jpa的查询接口实现
 *
 * @param <T> Domain类
 * @author dragon
 */
public class JpaQuery<T> implements cn.bc.core.query.Query<T> {
	private static Logger logger = LoggerFactory.getLogger("cn.bc.orm.jpa.JpaQuery");
	private EntityManager entityManager;
	private Class<T> entityClass;
	private Condition condition;

	@SuppressWarnings("unchecked")
	private JpaQuery() {
		// 这个需要子类中指定T为实际的类才有效
		Type type = this.getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			type = ((ParameterizedType) type).getActualTypeArguments()[0];
			if (type instanceof Class) this.entityClass = (Class<T>) type;
		}
	}

	/**
	 * 构造一个基于hibernate的Query实现
	 *
	 * @param entityManager em
	 */
	public JpaQuery(EntityManager entityManager) {
		this();
		this.entityManager = entityManager;
	}

	public JpaQuery(EntityManager entityManager, Class<T> persistentClass) {
		this(entityManager);
		this.entityClass = persistentClass;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<T> persistentClass) {
		this.entityClass = persistentClass;
	}

	public JpaQuery<T> condition(Condition condition) {
		this.condition = condition;
		return this;
	}

	public int count() {
		String jpql = JpaUtils.removeOrderBy(getJpql());
		if (!jpql.startsWith("select")) {
			jpql = "select count(*) " + jpql;
		} else {
			jpql = "select count(*) " + JpaUtils.removeSelect(jpql);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("args={}, jpql={}"
					, (condition != null ? StringUtils.collectionToCommaDelimitedString(condition.getValues()) : "null"), jpql);
		}
		TypedQuery<Number> query = this.entityManager.createQuery(jpql, Number.class);
		if (condition != null && condition.getValues() != null) {
			int i = 0;
			for (Object value : condition.getValues()) {
				query.setParameter(++i, value);// jpa的索引号从1开始
			}
		}

		Number c = JpaUtils.getSingleResult(query);
		return c == null ? 0 : c.intValue();
	}

	@SuppressWarnings("unchecked")
	public List<T> list() {
		return JpaUtils.executeQuery(entityManager, getJpql(), condition != null ? condition.getValues() : null);
	}

	@SuppressWarnings("unchecked")
	public List<T> list(int pageNo, int pageSize) {
		int _pageSize = pageSize < 1 ? 1 : pageSize;
		logger.debug("pageNo={}, pageSize={}", pageNo, _pageSize);
		Query query = JpaUtils.createQuery(entityManager, getJpql(), condition != null ? condition.getValues() : null);
		query.setFirstResult(Page.getFirstResult(pageNo, _pageSize));
		query.setMaxResults(_pageSize);
		return (List<T>) query.getResultList();
	}

	public Page<T> page(int pageNo, int pageSize) {
		return new Page<>(pageNo, pageSize, this.count(), this.list(pageNo, pageSize));
	}

	@SuppressWarnings("unchecked")
	public T singleResult() {
		return JpaUtils.getSingleResult(
				JpaUtils.createQuery(entityManager, getJpql(), condition != null ? condition.getValues() : null));
	}

	private static String ALIAS = "alias_";

	protected String getJpql() {
		String jpql = "from " + this.getEntityClass().getSimpleName() + " " + ALIAS;
		if (condition != null) {
			String expression = this.condition.getExpression(ALIAS);
			if (condition instanceof OrderCondition) {
				jpql += " order by " + expression;
			} else {
				if (expression != null && expression.startsWith("order by")) {
					jpql += " " + expression;
				} else if (expression != null && expression.length() > 0) {
					jpql += " where " + expression;
				}
			}
		}
		return jpql;
	}

	public List<Object> listWithSelect(String select) {
		String jpql = "select " + select + " " + getJpql();
		return JpaUtils.executeQuery(entityManager, jpql, condition != null ? condition.getValues() : null);
	}
}