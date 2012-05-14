/**
 * 
 */
package cn.bc.template.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.FileEntityImpl;

/**
 * 模板类型
 * 
 * @author lbj
 */
@Entity
@Table(name = "BC_TEMPLATE_TYPE")
public class TemplateType extends FileEntityImpl {
	private static final long serialVersionUID = 1L;


	private int status;// 状态：0-正常,1-禁用
	private String orderNo;// 排序号
	private String code;// 编码
	private String name;// 模板类型名称
	private boolean isPath=true;// 是否关联附件
	private boolean isPureText=false;// 是否纯文本
	private String extension;// 备注
	private String desc;// 备注

	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "ORDER_")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}


	@Column(name = "DESC_")
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "IS_PATH")
	public boolean isPath() {
		return isPath;
	}

	public void setPath(boolean isPath) {
		this.isPath = isPath;
	}

	@Column(name = "IS_PURE_TEXT")
	public boolean isPureText() {
		return isPureText;
	}

	public void setPureText(boolean isPureText) {
		this.isPureText = isPureText;
	}

	@Column(name = "EXT")
	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	

}
