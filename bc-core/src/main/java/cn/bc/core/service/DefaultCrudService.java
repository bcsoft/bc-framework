/**
 *
 */
package cn.bc.core.service;

import cn.bc.core.SetEntityClass;
import cn.bc.core.dao.CrudDao;
import cn.bc.core.exception.CoreException;
import cn.bc.core.exception.PermissionDeniedException;
import cn.bc.core.exception.UniqueConstraintException;
import cn.bc.core.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;


/**
 * CrudService接口
 *
 * @param <T> 对象类型
 * @author dragon
 */
public class DefaultCrudService<T> implements CrudService<T>, SetEntityClass<T>, InitializingBean {
	private static Logger logger = LoggerFactory.getLogger(DefaultCrudService.class);
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
			if (type instanceof Class) {
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

	/**
	 * 验证当前用户的权限
	 *
	 * @param readonly false-验证管理权限、true-验证查阅权限
	 * @throws PermissionDeniedException 当前用户没有指定的权限时
	 */
	protected void validatePrivilege(boolean readonly) throws PermissionDeniedException {
		// do nothing: 基类中复写此方法自定义权限验证;
	}

	/**
	 * 实体对象唯一性检测
	 *
	 * @param entity 要检测的实体对象
	 * @throws UniqueConstraintException
	 */
	protected void validateUniqueConstraint(T entity) throws UniqueConstraintException {
		// do nothing: 基类中复写此方法自定义验证;
	}

	public CrudDao<T> getCrudDao() {
		return this.crudDao;
	}

	public Query<T> createQuery() {
		return this.crudDao.createQuery();
	}

	public void delete(Serializable id) {
		validatePrivilege(false);
		this.crudDao.delete(id);
	}

	public void delete(Serializable[] ids) {
		validatePrivilege(false);
		this.crudDao.delete(ids);
	}

	public T load(Serializable id) {
		validatePrivilege(true);
		return this.crudDao.load(id);
	}

	public T forceLoad(Serializable id) {
		validatePrivilege(true);
		return this.crudDao.forceLoad(id);
	}

	public T create() {
		validatePrivilege(false);
		return this.crudDao.create();
	}

	public T save(T entity) {
		validatePrivilege(false);
		validateUniqueConstraint(entity);
		return this.crudDao.save(entity);
	}

	public void save(Collection<T> entities) {
		validatePrivilege(false);
		this.crudDao.save(entities);
	}

	public void update(Serializable id, Map<String, Object> attributes) {
		validatePrivilege(false);
		this.crudDao.update(id, attributes);
	}

	public void update(Serializable[] ids, Map<String, Object> attributes) {
		validatePrivilege(false);
		this.crudDao.update(ids, attributes);
	}
}
