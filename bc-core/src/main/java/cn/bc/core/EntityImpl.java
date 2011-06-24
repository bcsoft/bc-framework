/**
 * 
 */
package cn.bc.core;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * 默认的实体接口实现
 * 
 * @author dragon
 */
@MappedSuperclass
public abstract class EntityImpl extends MiniEntityImpl implements cn.bc.core.Entity<Long> {
	private static final long serialVersionUID = 7826313222480961654L;
	private String uid;
	private int status = cn.bc.core.Entity.STATUS_DISABLED;
	private boolean inner = false;

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

	@Column(name = "INNER_")
	public boolean isInner() {
		return inner;
	}

	public void setInner(boolean inner) {
		this.inner = inner;
	}
}
