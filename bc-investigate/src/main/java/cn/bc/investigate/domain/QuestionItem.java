package cn.bc.investigate.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;

/**
 * 问题项
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_IVG_QUESTION_ITEM")
public class QuestionItem extends EntityImpl {
	private static final long serialVersionUID = 1L;

	private Question question; // 所属问题
	private int orderNo; // 排序号
	private String subject; // 单选多选题显示的选项文字,如果为问答题则为默认填写的内容

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	@Column(name = "ORDER_")
	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
}
