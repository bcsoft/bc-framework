package cn.bc.form.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.BCConstants;
import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 表单
 * 
 * @author dragon
 * 
 */
@Entity
@Table(name = "BC_FORM")
public class Form extends RichFileEntityImpl {
	private static final long serialVersionUID = 1L;
	/** 状态：草稿 */
	public static final int STATUS_DRAFT = BCConstants.STATUS_DRAFT;
	/** 状态：正常 */
	public static final int STATUS_ENABLED = BCConstants.STATUS_ENABLED;
	private int pid;
	private String type;// 类别
	private String code;// 编码
	private String subject;// 标题
	private String tpl;// 模板编码
	public static final String ATTACH_TYPE = Form.class.getSimpleName();

	// 获取pid
	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	// 获取类别
	@Column(name = "TYPE_")
	public String getType() {
		return type;
	}

	// 设置类别
	public void setType(String type) {
		this.type = type;
	}

	// 获取编码
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	// 获取主题
	public String getSubject() {
		return subject;
	}

	// 设置主题
	public void setSubject(String subject) {
		this.subject = subject;
	}

	// 获取模板编码
	@Column(name = "TPL_")
	public String getTpl() {
		return tpl;
	}

	// 设置模板编码
	public void setTpl(String tpl) {
		this.tpl = tpl;
	}
}
