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
import cn.bc.identity.domain.ActorHistory;

/**
 * 邮件查阅历史
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_EMAIL_HISTORY")
public class EmailHistory extends EntityImpl {
	private static final long serialVersionUID = 1L;

	private Email email;// 所属邮件
	private ActorHistory reader;// 查阅人
	private Calendar fileDate;// 查阅时间

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "READER_ID", referencedColumnName = "ID")
	public ActorHistory getReader() {
		return reader;
	}

	public void setReader(ActorHistory reader) {
		this.reader = reader;
	}

	@Column(name = "FILE_DATE")
	public Calendar getFileDate() {
		return fileDate;
	}

	public void setFileDate(Calendar fileDate) {
		this.fileDate = fileDate;
	}
}