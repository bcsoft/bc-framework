/**
 * 
 */
package cn.bc.work.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import cn.bc.core.DefaultEntity;
import cn.bc.identity.domain.Actor;

/**
 * 待办\已办事项的基类
 * 
 * @author dragon
 */
@MappedSuperclass
public class Base extends DefaultEntity {
	private static final long serialVersionUID = 1L;

	private Work work;// 工作
	private Calendar sendDate;// 发送时间
	private Actor sender;// 发送人
	private Actor worker;// 办理人
	private String info;// 附加说明

	@Column(name = "SEND_DATE")
	public Calendar getSendDate() {
		return sendDate;
	}

	public void setSendDate(Calendar sendDate) {
		this.sendDate = sendDate;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false, targetEntity = Actor.class)
	@JoinColumn(name = "SENDER_ID", referencedColumnName = "ID")
	public Actor getSender() {
		return sender;
	}

	public void setSender(Actor sender) {
		this.sender = sender;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "WORK_ID", referencedColumnName = "ID")
	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false, targetEntity = Actor.class)
	@JoinColumn(name = "WORKER_ID", referencedColumnName = "ID")
	public Actor getWorker() {
		return worker;
	}

	public void setWorker(Actor worker) {
		this.worker = worker;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
