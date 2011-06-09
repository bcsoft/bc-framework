/**
 * 
 */
package cn.bc.core;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * 用户
 * 
 * @author dragon
 */
@MappedSuperclass
public abstract class DefaultEntity implements cn.bc.core.Entity<Long> {
	private static final long serialVersionUID = 7826313222480961654L;
	private Long id;
	private String uid;
	private int status = cn.bc.core.Entity.STATUS_DISABLED;
	private boolean inner = false;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Transient
	public boolean isNew() {
		return getId() == null || getId() <= 0;
	}

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
