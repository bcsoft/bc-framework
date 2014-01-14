package cn.bc.device.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.RichFileEntityImpl;

/**
 * 设备
 * 
 * @author hwx
 */
@Entity
@Table(name = "BC_DEVICE")
public class Device extends RichFileEntityImpl{
	private static final long serialVersionUID = 1L;
	private String code; //编码
	private String model; //型号
	private String name; //名称
	private String purpose; //用途
	private Calendar buyDate; //购买日期
	private String sn; //序列号
	private String desc;
	
	@Column(name = "CODE")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name = "MODEL")
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	
	@Column(name = "NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "PURPOSE")
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	
	@Column(name = "BUY_DATE")
	public Calendar getBuyDate() {
		return buyDate;
	}
	public void setBuyDate(Calendar buyDate) {
		this.buyDate = buyDate;
	}
	
	@Column(name = "SN")
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	
	@Column(name = "DESC_")
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
