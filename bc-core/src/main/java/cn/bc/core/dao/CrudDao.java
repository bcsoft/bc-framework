/**
 *
 */
package cn.bc.core.dao;

import cn.bc.core.CrudOperations;

/**
 * CrudDao接口
 *
 * @param <T> 对象类型
 * @author dragon
 */
public interface CrudDao<T extends Object> extends CrudOperations<T> {
}
