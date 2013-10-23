/**
 * 
 */
package cn.bc.email.domain;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import cn.bc.core.RichEntityImpl;
import cn.bc.identity.domain.Actor;

/**
 * 邮件
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_EMAIL")
public class Email extends RichEntityImpl {
	private static final long serialVersionUID = 1L;
	/** 状态：草稿 */
	public static final int STATUS_DRAFT = 0;
	/** 状态：已发送 */
	public static final int STATUS_SENDED = 1;
	/** 类型：新邮件 */
	public static final int TYPE_NEW = 0;
	/** 类型：回复 */
	public static final int TYPE_REPLY = 1;
	/** 类型：转发 */
	public static final int TYPE_FORWARD = 2;
	
	public static final String ATTACH_TYPE = Email.class.getSimpleName()+".main";

	private int type;// 类型 : 0-新邮件,1-回复,2-转发
	private Email email;// 所属邮件

	private String subject;// 标题
	private String content;// 内容
	private Set<EmailTo> tos;// 收件人列表
	private Calendar fileDate;// 创建时间
	private Actor sender;// 发件人
	private Calendar sendDate;//发送日期

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

	@OneToMany(mappedBy = "email", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "type asc, orderNo asc")
	public Set<EmailTo> getTos() {
		return tos;
	}

	public void setTos(Set<EmailTo> tos) {
		this.tos = tos;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Column(name = "FILE_DATE")
	public Calendar getFileDate() {
		return fileDate;
	}

	public void setFileDate(Calendar fileDate) {
		this.fileDate = fileDate;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "SENDER_ID", referencedColumnName = "ID")
	public Actor getSender() {
		return sender;
	}

	public void setSender(Actor sender) {
		this.sender = sender;
	}
	
	@Column(name = "SEND_DATE")
	public Calendar getSendDate() {
		return sendDate;
	}

	public void setSendDate(Calendar sendDate) {
		this.sendDate = sendDate;
	}
}