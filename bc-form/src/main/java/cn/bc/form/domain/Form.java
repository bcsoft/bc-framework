package cn.bc.form.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
	private String type;// 类别
	private String subject;// 标题
	private String templCode;// 模板编码
	public static final String ATTACH_TYPE = Form.class.getSimpleName();
	
	@Column(name = "TYPE_")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(name = "TPL_")
	public String getTemplate() {
		return templCode;
	}

	public void setTemplate(String template) {
		this.templCode = template;
	}
	
	
}
