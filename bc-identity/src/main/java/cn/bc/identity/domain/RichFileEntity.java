/**
 * 
 */
package cn.bc.identity.domain;

import java.io.Serializable;

/**
 * 基本文档实体接口：定义id字段
 * 
 * @author dragon
 * 
 */
public interface RichFileEntity<ID extends Serializable> extends FileEntity<ID> {
	/**
	 * @return 全局标识号
	 */
	String getUid();

	/**
	 * @param uid
	 *            设置全局标识号
	 */
	void setUid(String uid);

	/**
	 * @return 状态：参考STATUS_*常数的定义
	 */
	int getStatus();

	/**
	 * @param status
	 *            设置状态：值参考STATUS_*常数的定义
	 */
	void setStatus(int status);
}
