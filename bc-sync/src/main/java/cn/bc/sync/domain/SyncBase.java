/**
 * 
 */
package cn.bc.sync.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;
import cn.bc.identity.domain.ActorHistory;

/**
 * 同步信息的基类
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_SYNC_BASE")
@Inheritance(strategy = InheritanceType.JOINED)
public class SyncBase extends EntityImpl {
	private static final long serialVersionUID = 1L;
	public static final String KEY_UID = SyncBase.class.getSimpleName();
	/** 状态：同步后待处理 */
	public static final int STATUS_NEW = 0;
	/** 状态：已处理 */
	public static final int STATUS_DONE = 1;
	/** 状态：已生成，即已根据该同步信息生成相应的内部处理单 */
	public static final int STATUS_GEN = 2;

	private int status = STATUS_NEW;// 处理状态，见本类STATUS_XXX的定义
	private String syncType;// 同步信息的类型，syncType与syncId一起应该是唯一的
	private String syncCode;// 同步信息的标识符,该数据应来源于源数据,用于表示数据曾经的同步记录,与syncType一起用于避免重复记录的出现
	private String syncFrom;// 同步信息的来源：如WebService的连接地址和方法
	private Calendar syncDate;// 同步时间
	private ActorHistory author;// 执行同步操作的人

	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "SYNC_TYPE")
	public String getSyncType() {
		return syncType;
	}

	public void setSyncType(String syncType) {
		this.syncType = syncType;
	}

	@Column(name = "SYNC_FROM")
	public String getSyncFrom() {
		return syncFrom;
	}

	public void setSyncFrom(String syncFrom) {
		this.syncFrom = syncFrom;
	}

	@Column(name = "SYNC_DATE")
	public Calendar getSyncDate() {
		return syncDate;
	}

	public void setSyncDate(Calendar syncDate) {
		this.syncDate = syncDate;
	}

	@Column(name = "SYNC_CODE")
	public String getSyncCode() {
		return syncCode;
	}

	public void setSyncCode(String syncCode) {
		this.syncCode = syncCode;
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