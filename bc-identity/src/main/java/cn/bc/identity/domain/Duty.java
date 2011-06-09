/**
 * 
 */
package cn.bc.identity.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.core.DefaultEntity;

/**
 * 职务
 * 
 * @author dragon
 * 
 */
@Entity
@Table(name = "BC_IDENTITY_DUTY")
public class Duty extends DefaultEntity {
	private static final long serialVersionUID = 1L;
	private String code;// 编码，兼排序号的作用
	private String name;// 名称

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return "id:" + getId() + ",code:" + getCode() + ",name:" + getName()
				+ ",status:" + getStatus() + ",inner:" + isInner();
	}
}
