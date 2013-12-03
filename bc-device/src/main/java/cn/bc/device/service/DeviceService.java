package cn.bc.device.service;

import cn.bc.core.service.CrudService;
import cn.bc.device.domain.Device;

/**
 * 设备Service接口
 * 
 * @author hwx
 * 
 */
public interface DeviceService extends CrudService<Device> {

	/**
	 *  根据新建时设备编号从设备表查找id，如果id存在就重复，则设备编号就是唯一
	 * 
	 * @param code 
	 * 				新建时设备编号
	 * @return
	 */
	Long checkDeviceCodeIsExist(String code);
}
