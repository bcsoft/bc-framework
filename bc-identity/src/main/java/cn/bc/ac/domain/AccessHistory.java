/**
 * 
 */
package cn.bc.ac.domain;

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
 * 访问历史
 * 
 * @author dragon
 * 
 */
@Entity
@Table(name = "BC_AC_HISTORY")
public class AccessHistory extends EntityImpl {
	private static final long serialVersionUID = 1L;
	private AccessWay accessWay;// 访问方式
	private ActorHistory visitor;// 访问者
	private Calendar visitDate;// 访问时间

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public AccessWay getAccessWay() {
		return accessWay;
	}

	public void setAccessWay(AccessWay accessWay) {
		this.accessWay = accessWay;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "VISITOR_HID", referencedColumnName = "ID")
	public ActorHistory getVisitor() {
		return visitor;
	}

	public void setVisitor(ActorHistory visitor) {
		this.visitor = visitor;
	}

	@Column(name = "VISIT_DATE")
	public Calendar getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(Calendar visitDate) {
		this.visitDate = visitDate;
	}
}
