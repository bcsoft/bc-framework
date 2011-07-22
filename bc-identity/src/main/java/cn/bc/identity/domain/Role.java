/**
 * 
 */
package cn.bc.identity.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import cn.bc.core.RichEntityImpl;

/**
 * 角色
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_IDENTITY_ROLE")
public class Role extends RichEntityImpl {
	private static final long serialVersionUID = 1273753941627332660L;

	/** 类型：默认 */
	public static final int TYPE_DEFAULT = 0;

	private String name;// 名称
	private String code;// 编码
	private String orderNo;// 排序号
	private int type;// 类型，保留未用的字段
	private Set<Resource> resources;// 可访问的模块列表
	private boolean inner = false;//是否为内置对象，内置对象不允许删除

	@Column(name = "INNER_")
	public boolean isInner() {
		return inner;
	}

	public void setInner(boolean inner) {
		this.inner = inner;
	}

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

	@Column(name = "ORDER_")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "BC_IDENTITY_ROLE_RESOURCE", joinColumns = @JoinColumn(name = "RID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "SID", referencedColumnName = "ID"))
	@OrderBy("orderNo asc")
	public Set<Resource> getResources() {
		return resources;
	}

	public void setResources(Set<Resource> resources) {
		this.resources = resources;
	}
}
