/**
 * 
 */
package cn.bc.identity.domain;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 基本文档实体接口：定义id字段
 * 
 * @author dragon
 * 
 */
public interface FileEntity<ID extends Serializable> extends
		cn.bc.core.Entity<ID> {
	/**
	 * 获取最后修改人的ID
	 * 
	 * @return
	 */
	Long getModifierId();

	/**
	 * 设置最后修改人的ID
	 * 
	 * @param modifierId
	 */
	void setModifierId(Long modifierId);

	/**
	 * 获取最后修改人的名称
	 * 
	 * @return
	 */
	String getModifierName();

	/**
	 * 设置最后修改人的名称
	 * 
	 * @param modifierName
	 */
	void setModifierName(String modifierName);

	/**
	 * 获取最后修改时间
	 * 
	 * @return
	 */
	Calendar getModifiedDate();

	/**
	 * 设置最后修改时间
	 * 
	 * @param fileDate
	 */
	void setModifiedDate(Calendar modifiedDate);

	/**
	 * 获取创建时间
	 * 
	 * @return
	 */
	Calendar getFileDate();

	/**
	 * 设置创建时间
	 * 
	 * @param fileDate
	 */
	void setFileDate(Calendar fileDate);

	/**
	 * 获取文档的创建人
	 * 
	 * @return
	 */
	Actor getAuthor();

	/**
	 * 设置文档的创建人
	 * 
	 * @param author
	 */
	void setAuthor(Actor author);

	/**
	 * 获取文档创建人的名称
	 * 
	 * @return
	 */
	String getAuthorName();

	/**
	 * 设置文档创建人的名称
	 * 
	 * @param authorName
	 */
	void setAuthorName(String authorName);

	/**
	 * 获取创建人所在的部门的ID
	 * 
	 * @return
	 */
	Long getAuthorDepartId();

	/**
	 * 设置创建人所在的部门的ID
	 * 
	 * @param departId
	 */
	void setAuthorDepartId(Long departId);

	/**
	 * 获取创建人所在的部门的名称
	 * 
	 * @return
	 */
	String getAuthorDepartName();

	/**
	 * 设置创建人所在的部门的名称
	 * 
	 * @param departName
	 */
	void setAuthorDepartName(String departName);

	/**
	 * 获取创建人所在的单位的ID
	 * 
	 * @return
	 */
	Long getAuthorUnitId();

	/**
	 * 设置创建人所在的单位的ID
	 * 
	 * @param unitId
	 */
	void setAuthorUnitId(Long unitId);

	/**
	 * 获取创建人所在的单位的名称
	 * 
	 * @return
	 */
	String getAuthorUnitName();

	/**
	 * 设置创建人所在的单位的名称
	 * 
	 * @param unitName
	 */
	void setAuthorUnitName(String unitName);
}
