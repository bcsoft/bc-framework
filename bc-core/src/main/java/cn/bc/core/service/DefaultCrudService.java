/**
 * 
 */
package cn.bc.core.service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import cn.bc.core.SetEntityClass;
import cn.bc.core.dao.CrudDao;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.Query;


/**
 * CrudService接口
 * 
 * @author dragon
 * 
 * @param <T>
 *            对象类型
 */
public class DefaultCrudService<T extends Object> implements CrudService<T>,
		SetEntityClass<T> , InitializingBean{
	private static Log logger = LogFactory.getLog(DefaultCrudService.class);
	//protected final Log logger = LogFactory.getLog(getClass());
	private CrudDao<T> crudDao;
	protected Class<T> entityClass;

	public void setCrudDao(CrudDao<T> crudDao) {
		if (logger.isDebugEnabled())
			logger.debug("setCrudDao:" + crudDao);
		this.crudDao = crudDao;
	}

	@SuppressWarnings("unchecked")
	public DefaultCrudService() {
		// 这个需要子类中指定T为实际的类才有效(指定接口也不行的)
		Type type = this.getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			type = ((ParameterizedType) type).getActualTypeArguments()[0];
			if (type instanceof Class){
				this.entityClass = (Class<T>) type;
				if (logger.isDebugEnabled())
					logger.debug("auto judge entityClass to '" + this.entityClass + "' [" + this.getClass() + "]");
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws Exception {
		Class<T> clazz = this.getEntityClass();
//		//如果没有注入entityClass属性，则尝试自动设置
//		if(this.getEntityClass() == null){
//			//Caused by: java.lang.IllegalArgumentException: Type 'T' is not a Class, ParameterizedType, or GenericArrayType. Can't extract class.
//			clazz = new TypeReference<T>() {}.getRawType();
//			
//			if(logger.isDebugEnabled())
//				logger.debug("auto set entityClass to '" + clazz + "' [" + this.getClass() + "]");
//			this.setEntityClass(clazz);
//		}else{
//			clazz = this.getEntityClass();
//		}
		
		//如果dao没有注入entityClass属性，则尝试自动设置
		if (this.getCrudDao() == null) {
			throw new CoreException("must inject crudDao [" + this.getClass() + "]");
		}
		if (this.crudDao.getEntityClass() == null && clazz != null) {
			if (this.crudDao instanceof SetEntityClass) {
				if (logger.isDebugEnabled())
					logger.debug("auto set crudDao.entityClass to '" + clazz + "' [" + this.getClass() + "]");
				// 补充设置crudDao的entityClass
				((SetEntityClass<T>) this.crudDao).setEntityClass(clazz);
			}
		}
	}

	public DefaultCrudService(Class<T> clazz) {
		this.entityClass = clazz;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<T> clazz) {
		this.entityClass = clazz;
		if (logger.isDebugEnabled())
			logger.debug("setEntityClass:" + clazz);
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
