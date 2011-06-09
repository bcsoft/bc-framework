/**
 * 
 */
package cn.bc.core.query;

import java.util.List;

import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;




/**
 * 通用查询接口
 * @author dragon
 *
 */
public interface Query<T extends Object> {
	/**
	 * @return 为查询附加条件
	 */
	Query<T> condition(Condition condition);

	/**
	 * @return 符合条件的数据量
	 */
	int count();
	
	/**
	 * @return 查询的唯一结果或null
	 * @throws RuntimeException 查询返回多于1条数据的情况下
	 */
	T singleResult();
	
	/**
	 * @return 查询到的数据集
	 */
	List<T> list();
	
	/**
	 * 查询指定页的数据
	 * @param pageNo 页码，从1开始
	 * @param pageSize 查询的最大条目数
	 * @return 查询到的分页数据集
	 */
	List<T> list(int pageNo, int pageSize);
	
	/**
	 * 查询一页数据
	 * @param pageNo 页码，从1开始
	 * @param pageSize 每页的最大条目数
	 * @return 分页数据的封装
	 */
	Page<T> page(int pageNo, int pageSize);
	
	/**
	 * @param select 要选择的实体属性，如"id,name"，不要附带select字符串
	 * @return 查询到的数据集
	 */
	List<Object> listWithSelect(String select);
}
