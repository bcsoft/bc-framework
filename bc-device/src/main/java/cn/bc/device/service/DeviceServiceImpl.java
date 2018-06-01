package cn.bc.device.service;

import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.device.dao.DeviceDao;
import cn.bc.device.domain.Device;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 设备Service的实现
 *
 * @author hwx
 */
public class DeviceServiceImpl extends DefaultCrudService<Device> implements
  DeviceService {
  private DeviceDao deviceDao;

  @Autowired
  public void setDeviceDao(DeviceDao deviceDao) {
    this.deviceDao = deviceDao;
    this.setCrudDao(deviceDao);
  }

  public Long checkDeviceCodeIsExist(String code) {
    return this.deviceDao.checkDeviceCodeIsExist(code);
  }

  public String findDeviceCode(Long id) {
    return this.deviceDao.findDeviceCode(id);
  }

  public Device loadBySn(String sn) {
    AndCondition ac = new AndCondition();
    ac.add(new EqualsCondition("sn", sn));
    if (this.createQuery().condition(ac).count() == 0) {
      return null;
    } else {
      return this.createQuery().condition(ac).list().get(0);
    }
  }

  public Device loadByCode(String code) {
    AndCondition ac = new AndCondition();
    ac.add(new EqualsCondition("code", code));
    if (this.createQuery().condition(ac).count() == 0) {
      return null;
    } else {
      return this.createQuery().condition(ac).list().get(0);
    }
  }

}
