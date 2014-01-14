package cn.bc.device.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.bc.core.EntityImpl;


/**
 * 设备新事件
 * 
 * @author hwx
 */
@Entity
@Table(name = "BC_DEVICE_EVENT_NEW")
public class DeviceEventNew extends EntityImpl {
	
}
