package cn.bc.device.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;

/**
 * 设备事件
 * 
 * @author hwx
 */
@Entity
@Table(name = "BC_DEVICE_EVENT")
public class DeviceEventDomain extends EntityImpl{
	private static final long serialVersionUID = 1L;
	private Device device; //设备ID
	private String type; //事件类型
	private Calendar triggerTime; //触发时间
	private String appId; //appId
	private String data; //时间数据
	
	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "DEVICE_ID", referencedColumnName = "ID")
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
	
	@Column(name = "TYPE_")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name = "TRIGGER_TIME")
	public Calendar getTriggerTime() {
		return triggerTime;
	}
	public void setTriggerTime(Calendar triggerTime) {
		this.triggerTime = triggerTime;
	}
	
	@Column(name = "APPID")
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	@Column(name = "DATA_")
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	
	

}
