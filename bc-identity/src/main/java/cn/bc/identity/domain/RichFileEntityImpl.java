/**
 * 
 */
package cn.bc.identity.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import cn.bc.BCConstants;

/**
 * 带状态和UID的文档实体基类
 * 
 * @author dragon
 */
@MappedSuperclass
public abstract class RichFileEntityImpl extends FileEntityImpl implements
		RichFileEntity<Long> {
	private static final long serialVersionUID = 1L;
	private String uid;
	private int status = BCConstants.STATUS_ENABLED;

	@Column(name = "UID_")
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

//	/**
//	 * 获取用于计算文档年月日的日期指
//	 * 
//	 * @return
//	 */
//	@Transient
//	protected Calendar getSpecalDate() {
//		return this.getFileDate();
//	}
//
//	/** 统计用：文档时间的年度 */
//	@Column(name = "FILE_YEAR")
//	public int getFileYear() {
//		return getSpecalDate() != null ? getSpecalDate().get(Calendar.YEAR) : 0;
//	}
//
//	public void setFileYear(int n) {
//		// do nothing
//	}
//
//	/** 统计用：文档时间的月份(1-12) */
//	@Column(name = "FILE_MONTH")
//	public int getFileMonth() {
//		return getSpecalDate() != null ? getSpecalDate().get(Calendar.MONTH) + 1
//				: 0;
//	}
//
//	public void setFileMonth(int n) {
//		// do nothing
//	}
//
//	/** 统计用：文档时间的日(1-31) */
//	@Column(name = "FILE_DAY")
//	public int getFileDay() {
//		return getSpecalDate() != null ? getSpecalDate().get(
//				Calendar.DAY_OF_MONTH) : 0;
//	}
//
//	public void setFileDay(int n) {
//		// do nothing
//	}
}
