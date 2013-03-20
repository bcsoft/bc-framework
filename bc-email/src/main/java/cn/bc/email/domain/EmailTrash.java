/**
 * 
 */
package cn.bc.email.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;
import cn.bc.identity.domain.Actor;

/**
 * 邮件垃圾箱
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_EMAIL_TRASH")
public class EmailTrash extends EntityImpl {
	private static final long serialVersionUID = 1L;
	/** 状态：可恢复 */
	public static final int STATUS_RESUMABLE = 0;
	/** 状态：已删除 */
	public static final int STATUS_DELETED = 1;

	private int status;// 状态
	private Email email;// 邮件
	private Actor owner;// 所有者
	private Calendar handleDate;// 操作时间

	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "OWNER_ID", referencedColumnName = "ID")
	public Actor getOwner() {
		return owner;
	}

	public void setOwner(Actor reader) {
		this.owner = reader;
	}

	@Column(name = "HANDLE_DATE")
	public Calendar getHandleDate() {
		return handleDate;
	}

	public void setHandleDate(Calendar handleDate) {
		this.handleDate = handleDate;
	}
}