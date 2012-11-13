/**
 * 
 */
package cn.bc.identity.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import cn.bc.BCConstants;

/**
 * 带状态和UID的文档实体基类
 * 
 * @author dragon
 */
@MappedSuperclass
public abstract class RichFileEntityImpl extends FileEntityImpl implements
		RichFileEntity<Long> {
	private static final long serialVersionUID = 1L;
	private String uid;
	private int status = BCConstants.STATUS_ENABLED;

	@Column(name = "UID_")
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
