package cn.bc.device.service;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.device.dao.DeviceEventDao;
import cn.bc.device.domain.DeviceEventEntity;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 设备事件Service的实现
 *
 * @author hwx
 */
public class DeviceEventServiceImpl extends DefaultCrudService<DeviceEventEntity> implements DeviceEventService {
	private DeviceEventDao deviceEventDao;

	@Autowired
	public void setDeviceEventDao(DeviceEventDao deviceEventDao) {
		this.setCrudDao(deviceEventDao);
		this.deviceEventDao = deviceEventDao;
	}
}
