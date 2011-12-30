/**
 * 
 */
package cn.bc.feedback.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 用户反馈的回复
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_FEEDBACK_REPLY")
public class Reply extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	// 使用标准的状态定义，详见BCConstants.STATUS_XXX常数的定义

	private Feedback parent;// 所属反馈
	private String subject;// 标题
	private String content;// 详细内容

	public Feedback getParent() {
		return parent;
	}

	public void setParent(Feedback parent) {
		this.parent = parent;
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