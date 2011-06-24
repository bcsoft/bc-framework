/**
 * 
 */
package cn.bc.log.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import cn.bc.core.EntityImpl;
import cn.bc.identity.domain.Actor;

/**
 * 日志信息基类
 * 
 * @author dragon
 */
@MappedSuperclass
public class BaseLog extends EntityImpl {
	private static final long serialVersionUID = 1L;

	private Calendar createDate;// 创建时间
	private Actor creater;// 创建人
	private Integer type;// 类别
	private String subject;// 摘要
	private String content;// 详细内容

	@Column(name = "CREATE_DATE")
	public Calendar getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Calendar createDate) {
		this.createDate = createDate;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false, targetEntity = Actor.class)
	@JoinColumn(name = "CREATER_ID", referencedColumnName = "ID")
	public Actor getCreater() {
		return creater;
	}

	public void setCreater(Actor creater) {
		this.creater = creater;
	}

	@Column(name="TYPE_")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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
}
