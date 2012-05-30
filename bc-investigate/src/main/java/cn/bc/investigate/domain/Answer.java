package cn.bc.investigate.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;

/**
 * 问题的答案
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_IVG_ANSWER")
public class Answer extends EntityImpl {
	private static final long serialVersionUID = 1L;
	private Respond respond; // 对应的作答
	private QuestionItem item; // 作答的问题项
	private String content; // 问答题填写的内容

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "RID", referencedColumnName = "ID")
	public Respond getRespond() {
		return respond;
	}

	public void setRespond(Respond respond) {
		this.respond = respond;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "QID", referencedColumnName = "ID")
	public QuestionItem getItem() {
		return item;
	}

	public void setItem(QuestionItem question) {
		this.item = question;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
