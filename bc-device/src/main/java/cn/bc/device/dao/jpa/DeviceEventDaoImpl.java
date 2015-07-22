package cn.bc.device.dao.jpa;

import cn.bc.device.dao.DeviceEventDao;
import cn.bc.device.domain.DeviceEventEntity;
import cn.bc.orm.jpa.JpaCrudDao;

/**
 * 设备事件Dao实现
 *
 * @author hwx
 * 
 */
public class DeviceEventDaoImpl extends JpaCrudDao<DeviceEventEntity> implements DeviceEventDao {
}