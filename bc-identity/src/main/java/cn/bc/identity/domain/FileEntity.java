/**
 * 
 */
package cn.bc.identity.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import cn.bc.core.EntityImpl;

/**
 * 默认的带文档创建信息的实体基类
 * 
 * @author dragon
 */
@MappedSuperclass
public abstract class FileEntity extends EntityImpl {
	private static final long serialVersionUID = 1L;
	private Calendar fileDate;// 文档创建时间
	private Actor author;// 创建人
	private String subject;// 标题

	// 所属组织的冗余信息，用于提高统计效率用
	private Long departId;// 所属部门id
	private String departName;
	private Long unitId;// 所属单位id
	private String unitName;

	@Column(name = "FILE_DATE")
	public Calendar getFileDate() {
		return fileDate;
	}

	public void setFileDate(Calendar fileDate) {
		this.fileDate = fileDate;
	}

	/**
	 * 获取用于计算文档年月日的日期指
	 * 
	 * @return
	 */
	@Transient
	protected Calendar getSpecalDate() {
		return fileDate;
	}

	/** 统计用：文档时间的年度 */
	@Column(name = "FILE_YEAR")
	public int getFileYear() {
		return getSpecalDate() != null ? getSpecalDate().get(Calendar.YEAR) : 0;
	}

	public void setFileYear(int n) {
		// do nothing
	}

	/** 统计用：文档时间的月份(1-12) */
	@Column(name = "FILE_MONTH")
	public int getFileMonth() {
		return getSpecalDate() != null ? getSpecalDate().get(Calendar.MONTH) + 1
				: 0;
	}

	public void setFileMonth(int n) {
		// do nothing
	}

	/** 统计用：文档时间的日(1-31) */
	@Column(name = "FILE_DAY")
	public int getFileDay() {
		return getSpecalDate() != null ? getSpecalDate().get(
				Calendar.DAY_OF_MONTH) : 0;
	}

	public void setFileDay(int n) {
		// do nothing
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false, targetEntity = Actor.class)
	@JoinColumn(name = "AUTHOR_ID", referencedColumnName = "ID")
	public Actor getAuthor() {
		return author;
	}

	public void setAuthor(Actor author) {
		this.author = author;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(name = "AUTHOR_NAME")
	public String getAuthorName() {
		if (this.author != null) {
			return this.author.getName();
		} else {
			return null;
		}
	}

	public void setAuthorName(String authorName) {
		// do nothing
	}

	@Column(name = "DEPART_ID")
	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	@Column(name = "DEPART_NAME")
	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	@Column(name = "UNIT_ID")
	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	@Column(name = "UNIT_NAME")
	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
}
