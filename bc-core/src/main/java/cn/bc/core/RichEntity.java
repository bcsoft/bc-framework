/**
 *
 */
package cn.bc.core;

import java.io.Serializable;

/**
 * 带UID和状态的基本实体接口
 *
 * @author dragon
 */
public interface RichEntity<ID extends Serializable> extends Entity<ID> {
  /**
   * @return 全局标识号
   */
  String getUid();

  /**
   * @param uid 设置全局标识号
   */
  void setUid(String uid);

  /**
   * @return 状态：参考STATUS_*常数的定义
   */
  int getStatus();

  /**
   * @param status 设置状态：值参考STATUS_*常数的定义
   */
  void setStatus(int status);
//	
//	/**
//	 * @return 是否为内置对象,内置对象不允许删除、禁用等操作
//	 */
//	boolean isInner();
//	
//	/**
//	 * @param inner 设置是否为内置对象
//	 */
//	void setInner(boolean inner);
}
