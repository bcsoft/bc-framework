/**
 * 
 */
package cn.bc.security.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import cn.bc.core.DefaultEntity;

/**
 * 角色
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_SECURITY_ROLE")
public class Role extends DefaultEntity {
	private static final long serialVersionUID = 1273753941627332660L;

	/**类型：默认*/
	public static final int TYPE_DEFAULT = 0;

	private String name;//名称
	private String code;//编码，兼排序号的作用
	private int type;//类型，保留未用的字段
	private Set<Module> modules;//可访问的模块列表
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	@ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="BC_SECURITY_ROLE_MODULE",
        joinColumns=
            @JoinColumn(name="RID", referencedColumnName="ID"),
        inverseJoinColumns=
            @JoinColumn(name="MID", referencedColumnName="ID")
        )
	@OrderBy("code asc")
	public Set<Module> getModules() {
		return modules;
	}
	public void setModules(Set<Module> modules) {
		this.modules = modules;
	}
}
