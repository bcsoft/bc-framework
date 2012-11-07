/**
 * 
 */
package cn.bc.ac.domain;

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
@Table(name = "BC_AC_WAY")
public class AccessWay extends EntityImpl {
	private static final long serialVersionUID = 1L;
	private long docId;// 文档标识
	private int docType;// 文档类型
	private int accessType;// 访问类型：如查阅、编辑、回复等

	@Column(name = "DOC_ID")
	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	@Column(name = "DOC_TYPE")
	public int getDocType() {
		return docType;
	}

	public void setDocType(int docType) {
		this.docType = docType;
	}

	@Column(name = "ACCESS_TYPE")
	public int getAccessType() {
		return accessType;
	}

	public void setAccessType(int accessType) {
		this.accessType = accessType;
	}
}
