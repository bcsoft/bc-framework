/**
 * 
 */
package cn.bc.acl.domain;

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
@Table(name = "BC_ACL_HISTORY")
public class AccessHistory extends EntityImpl {
	private static final long serialVersionUID = 1L;
	private AccessWay accessWay;// 访问方式
	private ActorHistory actorHistory;// 访问者
	private Calendar accessDate;// 访问时间

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "WAY_ID", referencedColumnName = "ID")
	public AccessWay getAccessWay() {
		return accessWay;
	}

	public void setAccessWay(AccessWay accessWay) {
		this.accessWay = accessWay;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "ACTOR_HID", referencedColumnName = "ID")
	public ActorHistory getActorHistory() {
		return actorHistory;
	}

	public void setActorHistory(ActorHistory actorHistory) {
		this.actorHistory = actorHistory;
	}

	@Column(name = "ACCESS_DATE")
	public Calendar getAccessDate() {
		return accessDate;
	}

	public void setAccessDate(Calendar accessDate) {
		this.accessDate = accessDate;
	}
}