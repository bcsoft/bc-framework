package cn.bc.test.mock;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;

import cn.bc.core.Entity;
import cn.bc.core.SetEntityClass;
import cn.bc.core.dao.CrudDao;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.Query;

public class CrudDaoMock<T extends Entity<Long>> implements CrudDao<T>, SetEntityClass<T> {
	protected final Log logger = LogFactory.getLog(getClass());
	private long id = 0;
	private Map<String, T> entities = new HashMap<String, T>();
	private Class<T> entityClass;

	@SuppressWarnings("unchecked")
	public CrudDaoMock() {
		// 这个需要子类中指定T为实际的类才有效
		Type type = this.getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			type = ((ParameterizedType) type).getActualTypeArguments()[0];
			if (type instanceof Class)
				this.entityClass = (Class<T>) type;
		}
		// System.out.println("T.class="+this.persistentClass);
	}

	public Class<T> getEntityClass() {
		return this.entityClass;
	}

	public void setEntityClass(Class<T> clazz) {
		this.entityClass = clazz;
	}

	public Query<T> createQuery() {
		return new QueryMock<T>(this.entities);
	}

	public void delete(Serializable id) {
		if (id == null) {
			return;
		}

		if (entities.containsKey(id.toString())) {
			entities.remove(id.toString());
		}
	}

	public void delete(Serializable[] ids) {
		if (ids == null || ids.length == 0) {
			return;
		}

		for (Serializable id : ids) {
			this.delete(id);
		}
	}

	public T create() {
		try {
			T e = (T) this.getEntityClass().newInstance();
			logger.debug("initialize entity in CrudDaoMock.");
			return e;
		} catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public T load(Serializable id) {
		if (id == null) {
			return null;
		}

		String _id = id.toString();
		if (entities.containsKey(_id)) {
			return entities.get(_id);
		}
		return null;
	}

	public T forceLoad(Serializable id) {
		return load(id);
	}

	public T save(T entity) {
		if (entity == null) {
			return entity;
		}

		if (entity.getId() == null) {
			entity.setId(new Long(++id));
			entities.put(String.valueOf(entity.getId()), entity);
		} else {
			if (entities.containsKey(String.valueOf(entity.getId()))) {
				T e = entities.get(String.valueOf(entity.getId()));
				BeanUtils.copyProperties(entity,e);
			}
		}
		return entity;
	}

	public void save(Collection<T> entities) {
		if (entities == null || entities.isEmpty()) {
			return;
		}

		for (T e : entities) {
			this.save(e);
		}
	}

	public void update(Serializable id, Map<String, Object> attributes) {
		if (id == null) {
			return;
		}

		String _id = id.toString();
		T e;
		if (entities.containsKey(_id)) {
			e = entities.get(_id);
		} else {
			throw new CoreException("no entity exists with id '" + _id + "'");
		}
		for (String key : attributes.keySet()) {
			PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(this.getEntityClass(), key);
			if(pd == null)
				throw new CoreException("对象没有属性" + key);
			Method method = pd.getWriteMethod();
			if(method == null)
				throw new CoreException("对象属性" + key + "没有setter方法");
			try {
				method.invoke(e, attributes.get(key));
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void update(Serializable[] ids, Map<String, Object> attributes) {
		if (ids == null || ids.length == 0) {
			return;
		}

		for (Serializable id : ids) {
			this.update(id, attributes);
		}
	}
}
