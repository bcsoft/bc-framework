package cn.bc.form.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;

/**
 * 表单字段
 * 
 * @author dragon
 * 
 */
@Entity
@Table(name = "BC_FORM_FIELD")
public class Field extends EntityImpl {
	private static final long serialVersionUID = 1L;
	private Form form;// 所属表单
	private String name;// 字段名成
	private String label;// 标签
	private String type;// 值类型
	private String value;// 值

	@JoinColumn(name = "PID", referencedColumnName = "ID")
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public String getName() {
		return name;
	}

	@Column(name = "NAME_")
	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	@Column(name = "LABEL_")
	public void setLabel(String label) {
		this.label = label;
	}

	@Column(name = "TYPE_")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "VALUE_")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.getValue();
	}
}