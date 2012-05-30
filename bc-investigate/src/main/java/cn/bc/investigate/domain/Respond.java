package cn.bc.investigate.domain;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;
import cn.bc.identity.domain.ActorHistory;

/**
 * 用户对调查进行的作答
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_IVG_RESPOND")
public class Respond extends EntityImpl {
	private static final long serialVersionUID = 1L;
	private Questionary questionary; // 所属调查
	private Calendar fileDate;// 作答时间
	private ActorHistory author;// 作答人
	private Set<Answer> answers;// 答案集

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public Questionary getQuestionary() {
		return questionary;
	}

	public void setQuestionary(Questionary questionary) {
		this.questionary = questionary;
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

	@OneToMany(mappedBy = "respond", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	public Set<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(Set<Answer> answers) {
		this.answers = answers;
	}
}
