/**
 * 
 */
package cn.bc.template.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.FileEntityImpl;

/**
 * 模板参数
 * 
 * @author lbj
 */
@Entity
@Table(name = "BC_TEMPLATE_Param")
public class TemplateParam extends FileEntityImpl {
	private static final long serialVersionUID = 1L;

	private int status;// 状态：0-正常,1-禁用
	private String orderNo;// 排序号
	private String name;// 模板类型名称
	private String config;//模板参数配置信息
	private String desc;// 备注
/*	private Set<Template> templates;// 
	
	@OneToMany(mappedBy = "templateType", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	public Set<Template> getTemplates() {
		return templates;
	}

	public void setTemplates(Set<Template> templates) {
		this.templates = templates;
	}*/

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
