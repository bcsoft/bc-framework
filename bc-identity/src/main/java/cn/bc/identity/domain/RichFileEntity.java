/**
 * 
 */
package cn.bc.identity.domain;

import java.io.Serializable;

import cn.bc.core.RichEntity;

/**
 * 带UID和状态的基本文档实体接口：定义id字段
 * 
 * @author dragon
 * 
 */
public interface RichFileEntity<ID extends Serializable> extends
		FileEntity<ID>, RichEntity<ID> {

}
