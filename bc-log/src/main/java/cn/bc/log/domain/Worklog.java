/**
 * 
 */
package cn.bc.log.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 操作日志
 * <p>
 * 主要用于记录用户对文档的操作记录
 * </p>
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_LOG_WORK")
public class Worklog extends BaseLog {
	private static final long serialVersionUID = 1L;
	public static final String ATTACH_TYPE = Worklog.class.getSimpleName();
	
	/** 类型：系统创建 */
	public static final Integer TYPE_SYSTEM = 0;
	/** 类型：用户创建 */
	public static final Integer TYPE_USER = 1;

	private String uid;
	private String pid;// 所关联文档的标识，通常使用文档的id、uid或批号
	private String ptype;// 所关联文档的分类

	@Column(name = "UID_")
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPtype() {
		return ptype;
	}

	public void setPtype(String ptype) {
		this.ptype = ptype;
	}
}