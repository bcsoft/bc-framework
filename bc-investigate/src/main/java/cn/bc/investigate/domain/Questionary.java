package cn.bc.investigate.domain;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.*;

import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.domain.FileEntityImpl;

/**
 * 问卷
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_IVG_QUESTIONARY")
public class Questionary extends FileEntityImpl {
	private static final long serialVersionUID = 1L;

	/** 类型:网上调查 */
	public static final int TYPE_SURVEY = 0;
	/** 类型:网上考试 */
	public static final int TYPE_PAPER = 1;

	/** 状态值:草稿，值为-1 */
	public static final int STATUS_DRAFT = -1;
	/** 状态值:已发布，值为0 */
	public static final int STATUS_ISSUE = 0;
	/** 状态值:已归档，值为1 */
	public static final int STATUS_END = 1;

	private int status;// 状态
	private int type; // 类型，见 TYPE_XXXX 常数的定义
	private String subject; // 标题
	private Calendar startDate; // 开始日期
	private Calendar endDate; // 结束日期
	private boolean permitted = true; // 提交前允许查看统计
	private Set<Question> questions;// 问题集
	private Set<Respond> responds;// 作答集
	private Set<Actor> actors;// 参与人:为空代表所有人均可提交

	private ActorHistory issuer; // 发布人
	private Calendar issueDate; // 发布时间
	private ActorHistory pigeonholer; // 归档人
	private Calendar pigeonholeDate; // 归档时间

	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@OneToMany(mappedBy = "questionary", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "orderNo asc")
	public Set<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(Set<Question> questions) {
		this.questions = questions;
	}

	@OneToMany(mappedBy = "questionary", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "fileDate asc")
	public Set<Respond> getResponds() {
		return responds;
	}

	public void setResponds(Set<Respond> responds) {
		this.responds = responds;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "BC_IVG_QUESTIONARY_ACTOR", joinColumns = @JoinColumn(name = "QID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "AID", referencedColumnName = "ID"))
	@OrderBy("orderNo asc")
	public Set<Actor> getActors() {
		return actors;
	}

	public void setActors(Set<Actor> actors) {
		this.actors = actors;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public boolean isPermitted() {
		return permitted;
	}

	public void setPermitted(boolean permitted) {
		this.permitted = permitted;
	}

	@Column(name = "START_DATE")
	@Temporal(TemporalType.DATE)
	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	@Column(name = "END_DATE")
	@Temporal(TemporalType.DATE)
	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "ISSUER_ID", referencedColumnName = "ID")
	public ActorHistory getIssuer() {
		return issuer;
	}

	public void setIssuer(ActorHistory issuer) {
		this.issuer = issuer;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "PIGEONHOLER_ID", referencedColumnName = "ID")
	public ActorHistory getPigeonholer() {
		return pigeonholer;
	}

	public void setPigeonholer(ActorHistory pigeonholer) {
		this.pigeonholer = pigeonholer;
	}

	@Column(name = "ISSUE_DATE")
	public Calendar getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Calendar issueDate) {
		this.issueDate = issueDate;
	}

	@Column(name = "PIGEONHOLE_DATE")
	public Calendar getPigeonholeDate() {
		return pigeonholeDate;
	}

	public void setPigeonholeDate(Calendar pigeonholeDate) {
		this.pigeonholeDate = pigeonholeDate;
	}
}