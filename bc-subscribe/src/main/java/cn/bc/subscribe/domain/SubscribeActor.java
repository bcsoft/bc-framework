/**
 * 
 */
package cn.bc.subscribe.domain;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.identity.domain.Actor;

/**
 * 订阅者
 * 
 * @author lbj
 * 
 */
@Entity
@Table(name = "BC_SUBSCRIBE_ACTOR")
@IdClass(SubscribeActorPK.class)
public class SubscribeActor implements Serializable {
	private static final long serialVersionUID = 1L;
	private Subscribe  subscribe;// 订阅
	private Actor actor;// 订阅者
	private int type = 0;//类型: 0-用户订阅，1-系统推送
	private Calendar fileDate;//订阅日期

	/** 类型: 0-用户订阅  **/
	public static final int TYPE_ACTIVE = 0;
	/** 类型: 1-系统推送  **/
	public static final int TYPE_PASSIVE = 1;

	@Id
	@ManyToOne(targetEntity = Subscribe.class)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public Subscribe getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(Subscribe subscribe) {
		this.subscribe = subscribe;
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

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "FILE_DATE")
	public Calendar getFileDate() {
		return fileDate;
	}

	public void setFileDate(Calendar fileDate) {
		this.fileDate = fileDate;
	}


	
}