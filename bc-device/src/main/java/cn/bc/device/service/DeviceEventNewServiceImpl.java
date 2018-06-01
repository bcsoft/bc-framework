package cn.bc.device.service;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.device.dao.DeviceEventNewDao;
import cn.bc.device.domain.DeviceEventNew;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 设备新事件service实现
 *
 * @author hwx
 */
public class DeviceEventNewServiceImpl extends DefaultCrudService<DeviceEventNew> implements DeviceEventNewService {
  private DeviceEventNewDao deviceEventNewDao;

  @Autowired
  public void setDeviceEventNewDao(DeviceEventNewDao deviceEventNewDao) {
    this.setCrudDao(deviceEventNewDao);
    this.deviceEventNewDao = deviceEventNewDao;
  }
}