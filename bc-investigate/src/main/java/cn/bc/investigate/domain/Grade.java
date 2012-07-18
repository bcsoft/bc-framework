package cn.bc.investigate.domain;

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
 * 评分记录（仅适用于问答题、填空题，如果填空题有固定标砖答案也不需要评分）
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_IVG_GRADE")
public class Grade extends EntityImpl {
	private static final long serialVersionUID = 1L;
	private Answer answer; // 所评答案
	private int score; // 所评分数
	private String description; // 备注
	private Calendar fileDate;// 评分时间
	private ActorHistory author;// 评分人

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "ANSWER_ID", referencedColumnName = "ID")
	public Answer getAnswer() {
		return answer;
	}

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Column(name = "DESC_")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "FILE_DATE")
	public Calendar getFileDate() {
		return fileDate;
	}

	public void setFileDate(Calendar fileDate) {
		this.fileDate = fileDate;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "AUTHOR_ID", referencedColumnName = "ID")
	public ActorHistory getAuthor() {
		return author;
	}

	public void setAuthor(ActorHistory author) {
		this.author = author;
	}
}
