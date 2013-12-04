package cn.bc.device.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.device.domain.Device;

/**
 * 设备Dao接口
 * 
 * 
 * @author hwx
 * 
 */
public interface DeviceDao  extends CrudDao<Device>{


	/**
	 *  根据新建时设备编号从设备表查找id，如果id存在就重复，则设备编号就是唯一
	 * 
	 * @param code 
	 * 				新建时设备编号
	 * @return
	 */
	Long checkDeviceCodeIsExist(String code);

	/**
	 * 根据设备id值从设备表找出相应的设备编码
	 * 
	 * @param id 
	 * 				设备id值
	 * @return
	 */
	String findDeviceCode(Long id);

}
