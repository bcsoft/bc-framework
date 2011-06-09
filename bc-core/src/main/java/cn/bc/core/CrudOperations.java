/**
 * 
 */
package cn.bc.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import cn.bc.core.query.Query;



/**
 * CRUD(增删改查)常用操作。
 * CRUD ---- create增加、read查找、update修改、delete删除
 * @author dragon
 *
 * @param <T> 对象类型
 * @param <PK> 主键
 * @param <Q> 查询器
 */
public interface CrudOperations<T extends Object> {
	/**
	 * @return 获取操作对象的类型
	 */
	Class<T> getEntityClass();
	
	/**
	 * CRUD'D:删除对象
	 * @param id 对象主键
	 */
	void delete(Serializable id);
	
	/**
	 * CRUD'D:删除对象集
	 * @param ids 对象主键集
	 */
	void delete(Serializable[] ids);
	
	/**
	 * CRUD'CU:保存对象,
	 * 如果对象未持久化则为C，否则就是U了
	 * @param entity 要保存的对象
	 */
	T save(T entity);
	
	/**
	 * CRUD'CU:保存对象集,
	 * 如果对象未持久化则为C，否则就是U了
	 * @param entities 对象集
	 */
	void save(Collection<T> entities);
	
	/**
	 * CRUD'U:更新对象属性值
	 * @param id 对象主键
	 * @param attributes 新属性集
	 */
	void update(Serializable id, Map<String,Object> attributes);
	
	/**
	 * CRUD'U:更新对象属性值
	 * @param ids 对象主键集
	 * @param attributes 新属性集
	 */
	void update(Serializable[] ids, Map<String,Object> attributes);
	
	/**
	 * CRUD'R:查询指定主键的对象
	 * @param id 对象主键
	 */
	T load(Serializable id);
	
	/**
	 * CRUD'R:查询指定主键的对象,并且不管对象的状态如何，强制重新加载
	 * @param id 对象主键
	 */
	T forceLoad(Serializable id);
	
	/**
	 * CRUD'C:创建新的对象
	 */
	T create();
	
	/**
	 * CRUD'R:复杂查询的封装
	 * @return 实例化对象查询器
	 */
	Query<T> createQuery();
}
