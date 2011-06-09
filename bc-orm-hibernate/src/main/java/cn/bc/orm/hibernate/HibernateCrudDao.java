package cn.bc.orm.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.util.StringUtils;

import cn.bc.core.SetEntityClass;
import cn.bc.core.dao.CrudDao;


/**
 * CrudDao的SpringHibernate实现
 * @author dragon
 *
 * @param <T>
 * @param <PK>
 */
public class HibernateCrudDao<T extends Object> implements CrudDao<T>,SetEntityClass<T> {
	protected final Log logger = LogFactory.getLog(getClass());
	protected Class<T> entityClass;
	protected String pkName = "id";// 主键名称
	private HibernateTemplate hibernateTemplate;

	/**
	 * 通过构造函数注入实体对象的类型, 因为Java的泛型无法使用T.class的缘故
	 * 
	 * @param clazz
	 */
	public HibernateCrudDao(Class<T> clazz) {
		this.entityClass = clazz;
	}

	/**
	 * 如果子类将泛型参数具体化了，这个构造函数用于自动侦测实体对象的类型
	 */
	@SuppressWarnings("unchecked")
	public HibernateCrudDao() {
		// 这个需要子类中指定T为实际的类才有效
		Type type = this.getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			type = ((ParameterizedType) type).getActualTypeArguments()[0];
			if (type instanceof Class)
				this.entityClass = (Class<T>) type;
		}
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

	protected HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	public void setPkName(String pkName) {
		this.pkName = pkName;
	}

	public void setEntityClass(Class<T> persistentClass) {
		this.entityClass = persistentClass;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	private String getEntityName() {
		return this.entityClass.getSimpleName();
	}

	/**
	 * 返回默认的基于Hibernate实现的查询器
	 * 
	 * @see cn.bc.core.dao.CrudDao#createQuery()
	 */
	public cn.bc.core.query.Query<T> createQuery() {
		return defaultQuery(this.entityClass);
	}

	protected cn.bc.core.query.Query<T> defaultQuery(Class<T> persistentClass) {
		Session session = null;
		try {
			session = this.getSession();
		} catch (java.lang.NullPointerException e) {
			// e.printStackTrace();
		}
		if (persistentClass == null)
			return new HibernateQuery<T>(session);
		else
			return new HibernateQuery<T>(session, persistentClass);
	}

	private Session getSession() {
		//从HibernateDaoSupport抄过来的方法
		return (!this.hibernateTemplate.isAllowCreate() ?
			    SessionFactoryUtils.getSession(this.hibernateTemplate.getSessionFactory(), false) :
					SessionFactoryUtils.getSession(
							this.hibernateTemplate.getSessionFactory(),
							this.hibernateTemplate.getEntityInterceptor(),
							this.hibernateTemplate.getJdbcExceptionTranslator()));
	}

	public void delete(Serializable pk) {
		this.createQuery(
				"delete " + this.getEntityName() + " _alias where _alias."
						+ pkName + "=?", new Object[] { pk }).executeUpdate();
	}

	public void delete(Serializable[] ids) {
		if (ids == null || ids.length == 0)
			return;

		List<Object> args = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("delete " + this.getEntityName() + " _alias");
		if (ids.length == 1) {
			hql.append(" where _alias." + pkName + "=?");
			args.add(ids[0]);
		} else {
			int i = 0;
			hql.append(" where _alias." + pkName + " in (");
			for (Serializable pk : ids) {
				hql.append(i == 0 ? "?" : ",?");
				args.add(pk);
				i++;
			}
			hql.append(")");
		}
		this.createQuery(hql.toString(), args.toArray()).executeUpdate();
	}

	public T load(Serializable pk) {
		return this.getHibernateTemplate().get(this.entityClass, pk);
	}

	public T forceLoad(Serializable pk) {
		T e = this.getHibernateTemplate().get(this.entityClass, pk);
		this.getHibernateTemplate().refresh(e);
		return e;
	}

	public T save(T entity) {
		if (null != entity)
			this.getHibernateTemplate().saveOrUpdate(entity);
		return entity;
	}

	public void save(Collection<T> entities) {
		if (null != entities && !entities.isEmpty())
			this.getHibernateTemplate().saveOrUpdateAll(entities);
	}

	public void update(Serializable pk, Map<String, Object> attributes) {
		if (pk == null || attributes == null || attributes.isEmpty())
			return;
		this.update(new Serializable[]{pk}, attributes);
	}

	public void update(Serializable[] pks, Map<String, Object> attributes) {
		if (pks == null || pks.length == 0 || attributes == null
				|| attributes.isEmpty())
			return;

		List<Object> args = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("update " + this.getEntityName() + " _alias");

		// set
		int i = 0;
		for (String key : attributes.keySet()) {
			if (i > 0)
				hql.append(",_alias." + key + "=?");
			else
				hql.append(" set _alias." + key + "=?");
			args.add(attributes.get(key));
			i++;
		}

		// pks
		if (pks.length == 1) {
			hql.append(" where _alias." + pkName + "=?");
			args.add(pks[0]);
		} else {
			i = 0;
			hql.append(" where _alias." + pkName + " in (");
			for (Serializable pk : pks) {
				hql.append(i == 0 ? "?" : ",?");
				args.add(pk);
				i++;
			}
			hql.append(")");
		}

		this.createQuery(hql.toString(), args.toArray()).executeUpdate();
	}

	/**
	 * 创建查询对象
	 * 
	 * @param hql
	 *            查询语句
	 * @param args
	 *            查询语句中的参数
	 * @return 构建好的查询对象
	 */
	protected Query createQuery(String hql, Object[] args) {
		if(logger.isDebugEnabled()){
			logger.debug("hql=" + hql);
			logger.debug("args=" + StringUtils.arrayToCommaDelimitedString(args));
		}
		Query queryObj = this.getSession().createQuery(hql);
		if (null != args && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				queryObj.setParameter(i, args[i]);
			}
		}
		return queryObj;
	}

	public T create() {
		try {
			T e = (T) this.getEntityClass().newInstance();
			logger.debug("initialize entity in HibernateCrudDao.");
			return e;
		} catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
}
