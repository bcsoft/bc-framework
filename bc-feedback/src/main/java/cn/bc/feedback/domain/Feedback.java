/**
 * 
 */
package cn.bc.feedback.domain;

import java.util.Calendar;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 用户反馈
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_FEEDBACK")
public class Feedback extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	/** 状态：草稿或待提交 */
	public static final int STATUS_DRAFT = 0;
	/** 状态：已提交 */
	public static final int STATUS_SUMMIT = 1;
	/** 状态：已受理 */
	public static final int STATUS_ACCEPT = 2;
	/** 状态：被驳回 */
	public static final int STATUS_REJECT = 3;
	/** 状态：已删除 */
	public static final int STATUS_DELETED = 4;// 普通用户的删除操作将只是标记删除

	private String subject;// 标题
	private String content;// 详细内容
	private Set<Reply> replies;// 回复列表
	private Calendar lastReplyDate;// 最后回复时间
	private ActorHistory lastReplier;// 最后回复人
	private int replyCount;// 回复的数量

	@OneToMany(mappedBy = "feedback", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "fileDate asc")
	public Set<Reply> getReplies() {
		return replies;
	}

	public void setReplies(Set<Reply> replies) {
		this.replies = replies;
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

	@Column(name = "LAST_REPLY_DATE")
	public Calendar getLastReplyDate() {
		return lastReplyDate;
	}

	public void setLastReplyDate(Calendar lastReplyDate) {
		this.lastReplyDate = lastReplyDate;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "LAST_REPLIER_ID", referencedColumnName = "ID")
	public ActorHistory getLastReplier() {
		return lastReplier;
	}

	public void setLastReplier(ActorHistory lastReplier) {
		this.lastReplier = lastReplier;
	}

	@Column(name = "REPLY_COUNT")
	public int getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}
}