/**
 * 
 */
package cn.bc.report.domain;

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
 * 报表任务的运行历史
 * 
 * @author dragon
 * 
 */
@Entity
@Table(name = "BC_REPORT_HISTORY")
public class ReportHistory extends EntityImpl {
	private static final long serialVersionUID = 1L;
	/** 报表运行结果存储的子路径，开头末尾不要带"/" */
	public static String DATA_SUB_PATH = "report/history";

	private String category;// 所属分类，如"营运系统/发票统计"
	private String subject;// 标题
	private String msg;// 运行结果的描述信息，如成功、异常信息
	private boolean success;// 运行是否成功
	private String path;// 报表运行结果所在的相对路径（相对于DATA_SUB_PATH下的子路径），如果有多个附件用分号连接
	private String sourceType;// 来源:有报表任务、用户生成和其它，报表任务是指通过报表任务定时生成的报表，用户生成是指用户手动执行报表模板，并通过"存为历史"操作而产生的，其它是指通过其它模块独立生成的
	private Long sourceId;// 来源id:来源为报表任务时，来源ID记录的是报表任务的ID；来源为用户生成时，来源ID为报表模板的ID；其它来源时，来源ID有调用者自行确定记录什么信息；来源信息仅用于后台的历史追索，不作任何约束关联
	private Calendar startDate; // 执行报表的开始时间
	private Calendar endDate; // 执行报表的结束时间
	private ActorHistory author;// 创建人

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Column(name = "START_DATE")
	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	@Column(name = "END_DATE")
	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "AUTHOR_ID", referencedColumnName = "ID")
	public ActorHistory getAuthor() {
		return author;
	}

	public void setAuthor(ActorHistory author) {
		this.author = author;
	}

	@Column(name = "SOURCE_TYPE")
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	@Column(name = "SOURCE_ID")
	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

}
