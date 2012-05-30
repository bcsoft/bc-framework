/**
 * 
 */
package cn.bc.investigate.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 电子公告
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_BULLETIN")
public class Bulletin extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	/**状态：草稿或待发布*/
	public static final int STATUS_DRAFT = 0;
	/**状态：已发布*/
	public static final int STATUS_ISSUED = 1;
	/**状态：已过期*/
	public static final int STATUS_OVERDUE = 2;
	/**发布范围：本单位*/
	public static final int SCOPE_LOCALUNIT = 0;
	/**发布范围：全系统*/
	public static final int SCOPE_SYSTEM = 1;

	private Actor unit;// 所属单位
	private Actor issuer;// 发布者
	private Calendar issueDate;// 发布时间
	private Calendar overdueDate;// 过期日期：为空代表永不过期
	private int scope;// 发布范围：0-本单位,1-全系统
	private String subject;//标题
	private String content;// 详细内容

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "UNIT_ID", referencedColumnName = "ID")
	public Actor getUnit() {
		return unit;
	}

	public void setUnit(Actor unit) {
		this.unit = unit;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "ISSUER_ID", referencedColumnName = "ID")
	public Actor getIssuer() {
		return issuer;
	}

	public void setIssuer(Actor issuer) {
		this.issuer = issuer;
	}

	@Column(name = "ISSUE_DATE")
	public Calendar getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Calendar issueDate) {
		this.issueDate = issueDate;
	}

	@Column(name = "OVERDUE_DATE")
	public Calendar getOverdueDate() {
		return overdueDate;
	}

	public void setOverdueDate(Calendar overdueDate) {
		this.overdueDate = overdueDate;
	}

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}