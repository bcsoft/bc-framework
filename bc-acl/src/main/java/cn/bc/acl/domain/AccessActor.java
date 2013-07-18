/**
 * 
 */
package cn.bc.acl.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.identity.domain.Actor;

/**
 * 访问者
 * 
 * @author dragon
 * 
 */
@Entity
@Table(name = "BC_ACL_ACTOR")
@IdClass(AccessActorPK.class)
public class AccessActor implements Serializable {
	private static final long serialVersionUID = 1L;
	private AccessDoc accessDoc;// 访问对象
	private Actor actor;// 访问者
	private int orderNo = 0;// 排序号：针对同一文档同一访问类型的不同访问者之间的排序
	
	/**
	 * 访问权限  从右边起，第一位为：查阅
	 * 				第二位为： 编辑 
	 * 				1代表有权限 0代表无限权
	 * 	例如：“01” 代表有查阅，没有编辑权限
	 * 		“11” 代表既有查阅权限，又有编辑权限
	 * 
	 */
	private String role;
	public static final String ROLE_FALSE = "0";
	public static final String ROLE_TRUE = "1";

	@Id
	@ManyToOne(targetEntity = AccessDoc.class)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public AccessDoc getAccessDoc() {
		return accessDoc;
	}

	public void setAccessDoc(AccessDoc accessDoc) {
		this.accessDoc = accessDoc;
	}

	@Id
	@ManyToOne(targetEntity = Actor.class)
	@JoinColumn(name = "AID", referencedColumnName = "ID")
	public Actor getActor() {
		return actor;
	}

	public void setActor(Actor actor) {
		this.actor = actor;
	}

	@Column(name = "ORDER_")
	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}