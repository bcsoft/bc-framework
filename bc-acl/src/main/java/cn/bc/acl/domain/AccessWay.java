/**
 * 
 */
package cn.bc.acl.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;

/**
 * 访问方式
 * 
 * @author dragon
 * 
 */
@Entity
@Table(name = "BC_ACL_WAY")
public class AccessWay extends EntityImpl {
	private static final long serialVersionUID = 1L;
	/** 访问方式：查阅 */
	public static final int WAY_READ = 1;
	/** 访问方式：编辑 */
	public static final int WAY_EDIT = 2;

	private Long docId;// 文档标识
	private String docType;// 文档类型
	private int way;// 访问方式：如查阅、编辑等，见常数WAY_XXX定义

	@Column(name = "DOC_ID")
	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	@Column(name = "DOC_TYPE")
	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	@Column(name = "WAY")
	public int getWay() {
		return way;
	}

	public void setWay(int way) {
		this.way = way;
	}
}
