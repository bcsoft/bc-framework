/**
 * 
 */
package cn.bc.acl.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.FileEntityImpl;

/**
 * 访问控制
 * 
 * @author dragon
 * 
 */
@Entity
@Table(name = "BC_ACL_CONTROL")
public class AccessControl extends FileEntityImpl {
	private static final long serialVersionUID = 1L;
	private AccessWay accessWay;// 访问方式
	private Actor actor;// 访问者
	private int orderNo = 0;// 排序号：针对同一文档同一访问类型的不同访问者之间的排序

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "WAY_ID", referencedColumnName = "ID")
	public AccessWay getAccessWay() {
		return accessWay;
	}

	public void setAccessWay(AccessWay accessWay) {
		this.accessWay = accessWay;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "ACTOR_ID", referencedColumnName = "ID")
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
}
