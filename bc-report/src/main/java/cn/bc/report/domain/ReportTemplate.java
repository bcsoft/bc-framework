/**
 * 
 */
package cn.bc.report.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.FileEntityImpl;

/**
 * 报表模板
 * 
 * @author dragon
 * 
 */
@Entity
@Table(name = "BC_REPORT_TEMPLATE")
public class ReportTemplate extends FileEntityImpl {
	private static final long serialVersionUID = 1L;

	private int status;// 状态：0-正常,1-禁用
	private String orderNo;// 排序号
	private String category;// 所属分类，如"营运系统/发票统计"
	private String name;// 名称
	private String code;// 编码，全局唯一
	private String desc;// 备注
	private String config;// 详细配置
	private Set<Actor> users;// 使用人，为空代表所有人均可使用

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "BC_REPORT_TEMPLATE_ACTOR", joinColumns = @JoinColumn(name = "TID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "AID", referencedColumnName = "ID"))
	@OrderBy("orderNo asc")
	public Set<Actor> getUsers() {
		return users;
	}

	public void setUsers(Set<Actor> users) {
		this.users = users;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

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

	public void setOrderNo(String order) {
		this.orderNo = order;
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

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}
}
