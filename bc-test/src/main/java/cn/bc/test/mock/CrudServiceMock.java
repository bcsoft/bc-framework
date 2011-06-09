/**
 * 
 */
package cn.bc.test.mock;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.core.Entity;
import cn.bc.core.SetEntityClass;
import cn.bc.core.dao.CrudDao;
import cn.bc.core.query.Query;
import cn.bc.core.service.CrudService;

/**
 * CrudService的内存模拟实现
 * 
 * @author dragon
 * 
 * @param <T>
 *            对象类型
 */
public class CrudServiceMock<T extends Entity<Long>> implements CrudService<T>,
		SetEntityClass<T> {
	private static Log logger = LogFactory.getLog(CrudServiceMock.class);
	private CrudDao<T> crudDao;
	protected Class<T> entityClass;

	public void setCrudDao(CrudDao<T> crudDao) {
		this.crudDao = crudDao;
	}

	@SuppressWarnings("unchecked")
	public CrudServiceMock() {
		// 这个需要子类中指定T为实际的类才有效(指定接口也不行的)
		Type type = this.getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			type = ((ParameterizedType) type).getActualTypeArguments()[0];
			if (type instanceof Class) {
				this.entityClass = (Class<T>) type;
				if (logger.isInfoEnabled())
					logger.info("auto judge entityClass to '"
							+ this.entityClass + "' [" + this.getClass() + "]");
			}
		}

		CrudDaoMock<T> daoMock = new CrudDaoMock<T>();
		daoMock.setEntityClass(this.entityClass);
		this.setCrudDao(daoMock);
	}

	public CrudServiceMock(Class<T> clazz) {
		this.entityClass = clazz;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setEntityClass(Class<T> clazz) {
		this.entityClass = clazz;
		if (logger.isDebugEnabled())
			logger.debug("setEntityClass:" + clazz);
		if(this.crudDao instanceof SetEntityClass)
		((SetEntityClass)this.crudDao).setEntityClass(clazz);
	}

	public CrudDao<T> getCrudDao() {
		return this.crudDao;
	}

	public Query<T> createQuery() {
		return this.crudDao.createQuery();
	}

	public void delete(Serializable id) {
		this.crudDao.delete(id);
	}

	public void delete(Serializable[] ids) {
		this.crudDao.delete(ids);
	}

	public T load(Serializable id) {
		return this.crudDao.load(id);
	}

	public T forceLoad(Serializable id) {
		return this.crudDao.forceLoad(id);
	}

	public T create() {
		return this.crudDao.create();
	}

	public T save(T entity) {
		return this.crudDao.save(entity);
	}

	public void save(Collection<T> entities) {
		this.crudDao.save(entities);
	}

	public void update(Serializable id, Map<String, Object> attributes) {
		this.crudDao.update(id, attributes);
	}

	public void update(Serializable[] ids, Map<String, Object> attributes) {
		this.crudDao.update(ids, attributes);
	}
}
