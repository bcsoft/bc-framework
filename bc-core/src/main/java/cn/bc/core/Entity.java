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
	/**状态：启用中*/
	public static final int STATUS_ENABLED = 0;
	/**状态：已禁用*/
	public static final int STATUS_DISABLED = 1;
	/**状态：已删除*/
	public static final int STATUS_DELETED = 2;
	
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
