/**
 * 
 */
package cn.bc.email.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;
import cn.bc.identity.domain.Actor;

/**
 * 邮件收件人
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_EMAIL_TO")
public class EmailTo extends EntityImpl {
	private static final long serialVersionUID = 1L;
	/** 类型：主送 */
	public static final int TYPE_TO = 0;
	/** 类型：抄送 */
	public static final int TYPE_CC = 1;
	/** 类型：密送 */
	public static final int TYPE_BCC = 2;

	private Email email;// 所属邮件
	private int type;// 发送类型 : 0-主送,1-抄送,2-密送
	private int orderNo;// 排序号 : 针对同一发送类型的不同收件人之间的排序
	private boolean read;// 已阅标记
	private Actor receiver; // 收件人：只能为用户类型，如果为岗位、部门、单位类型需要使用到upper字段配合
	private Actor upper;    // 收件人所属的部门或岗位

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "ORDER_")
	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	@Column(name = "READ_")
	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "RECEIVER_ID", referencedColumnName = "ID")
	public Actor getReceiver() {
		return receiver;
	}

	public void setReceiver(Actor receiver) {
		this.receiver = receiver;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "UPPER_ID", referencedColumnName = "ID")
	public Actor getUpper() {
		return upper;
	}

	public void setUpper(Actor upper) {
		this.upper = upper;
	}
	
	

}