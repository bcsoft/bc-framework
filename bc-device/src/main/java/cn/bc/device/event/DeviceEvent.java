package cn.bc.device.event;

import java.util.Calendar;

import org.springframework.context.ApplicationEvent;

import cn.bc.device.domain.Device;

/**
 * 将要被广播的设备事件
 * 
 * @author hwx
 * 
 */
public class DeviceEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;
	private String code; // 设备编码
	private String type; // 事件类型
	private Calendar triggerTime; // 数据库记录的触发时间
	private String appId; // appId
	private String data; // 事件数据

	public DeviceEvent(Object source, String code, String type,Calendar triggerTime,String appId,String data) {
		super(source);
		this.code = code;
		this.type = type;
		this.triggerTime = triggerTime;
		this.appId = appId;
		this.data = data;

	}
	
	/**
	 * 获取设备编码
	 * @return
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * 获取设备类型
	 * @return
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * 获取数据库记录的触发时间
	 * @return
	 */
	public Calendar getTriggerTime() {
		return triggerTime;
	}
	
	/**
	 * 获取事件数据
	 * @return
	 */
	public String getAppId() {
		return appId;
	}
	
	/**
	 * 获取appId
	 * @return
	 */
	public String getData() {
		return data;
	}

}
