/**
 * 
 */
package cn.bc.core;

import java.io.Serializable;

/**
 * 迷你实体接口
 * @author dragon
 * 
 */
public interface MiniEntity<ID extends Serializable> extends Serializable {
	/**
	 * @return 主键
	 */
	ID getId();
	
	/**
	 * @param id 设置主键
	 */
	void setId(ID id);
	
	/**
	 * @return 主键是否已被设置
	 */
	boolean isNew();
}
