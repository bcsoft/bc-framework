/**
 * 
 */
package cn.bc.ac.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;
import cn.bc.identity.domain.Actor;

/**
 * 访问控制列表
 * 
 * @author dragon
 * 
 */
@Entity
@Table(name = "BC_AC_LIST")
public class AccessList extends EntityImpl {
	private static final long serialVersionUID = 1L;
	private AccessWay accessWay;// 访问方式
	private Actor visitor;// 访问者
	private int orderNo = 0;// 排序号：针对同一文档同一访问类型的不同访问者之间的排序

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public AccessWay getAccessWay() {
		return accessWay;
	}

	public void setAccessWay(AccessWay accessWay) {
		this.accessWay = accessWay;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "VISITOR_ID", referencedColumnName = "ID")
	public Actor getVisitor() {
		return visitor;
	}

	public void setVisitor(Actor visitor) {
		this.visitor = visitor;
	}

	@Column(name = "ORDER_")
	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}
}
