/**
 * 
 */
package cn.bc.core;

import java.io.Serializable;

/**
 * 基本实体接口：定义id字段
 * @author dragon
 * 
 */
public interface Entity<ID extends Serializable> extends Serializable {
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
