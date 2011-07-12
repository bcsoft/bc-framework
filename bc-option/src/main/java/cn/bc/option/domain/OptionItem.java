/**
 * 
 */
package cn.bc.option.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;

/**
 * 选项条目
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_OPTION_ITEM")
public class OptionItem extends EntityImpl {
	private static final long serialVersionUID = 1L;

	private OptionGroup optionGroup; // 所属分组
	private String value; // 值
	private String key; // 键
	private String orderNo; // 排序号
	private String icon; // 图标样式
	private int status = cn.bc.core.RichEntity.STATUS_ENABLED;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public OptionGroup getOptionGroup() {
		return optionGroup;
	}

	public void setOptionGroup(OptionGroup optionGroup) {
		this.optionGroup = optionGroup;
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

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	@Column(name = "VALUE_")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Column(name = "KEY_")
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
}