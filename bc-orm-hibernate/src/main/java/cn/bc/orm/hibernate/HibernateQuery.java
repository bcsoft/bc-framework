package cn.bc.orm.hibernate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;


/**
 * 基于hibernate的查询接口实现
 * 
 * @author dragon
 * 
 * @param <T>
 */
public class HibernateQuery<T extends Object> implements cn.bc.core.query.Query<T> {
	private Session session;
	private Condition condition;
	private Class<T> entityClass;

	@SuppressWarnings("unchecked")
	private HibernateQuery() {
		// 这个需要子类中指定T为实际的类才有效
		Type type = this.getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			type = ((ParameterizedType) type).getActualTypeArguments()[0];
			if (type instanceof Class)
				this.entityClass = (Class<T>) type;
		}
		//System.out.println("T.class="+this.persistentClass);
	}

	/**
	 * 构造一个基于hibernate的Query实现
	 * 
	 * @param session
	 *            hibernate会话
	 */
	public HibernateQuery(Session session) {
		this();
		// if (session == null)
		// throw new QCException("session can not be null.");
		this.session = session;
	}

	public HibernateQuery(Session session, Class<T> persistentClass) {
		this(session);
		this.entityClass = persistentClass;
		//System.out.println("persistentClass="+this.persistentClass);
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public HibernateQuery<T> condition(Condition condition) {
		this.condition = condition;
		return this;
	}

	// --implements Query

	public int count() {
		String queryTemp = HibernateUtils.removeOrderBy(getHql());
		if (!queryTemp.startsWith("select")) {
			queryTemp = "select count(*) " + queryTemp;
		} else {
			queryTemp = "select count(*) " + HibernateUtils.removeSelect(queryTemp);
		}
		Query query = this.createHibernateQuery(queryTemp);
		return ((Long) query.uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<T> list() {
		return new ArrayList<T>(createHibernateQuery().list());
	}

	@SuppressWarnings("unchecked")
	public List<Object> listWithSelect(String select) {
		return createHibernateQuery("select " + select + " " + getHql()).list();
	}

	@SuppressWarnings("unchecked")
	public List<T> list(int pageNo, int pageSize) {
		org.hibernate.Query queryObject = createHibernateQuery();
		queryObject.setFirstResult(Page.getFirstResult(pageNo, pageSize));
		queryObject.setMaxResults(pageSize);
		return new ArrayList<T>(queryObject.list());
	}

	public Page<T> page(int pageNo, int pageSize) {
		return new Page<T>(pageNo, pageSize, this.count(), this.list(
				pageNo, pageSize));
	}

	@SuppressWarnings("unchecked")
	public T singleResult() {
		return (T) createHibernateQuery().uniqueResult();
	}

	// --private

	protected String getHql() {
		String hql = "from " + this.getEntityClass().getSimpleName();
		if (condition != null) hql += " where " + this.condition.getExpression();
		return hql;
	}
	
	private org.hibernate.Query createHibernateQuery() {
		return createHibernateQuery(getHql());
	}

	/**
	 * @return 创建hibernate查询对象
	 */
	private org.hibernate.Query createHibernateQuery(String hql) {
		org.hibernate.Query queryObject = session.createQuery(hql);
		if (this.condition != null){
			List<Object> args = this.condition.getValues();
			if (args != null) {
				for (int i = 0; i < args.size(); i++) {
					queryObject.setParameter(i, args.get(i));
				}
			}
		}
		return queryObject;
	}
}
