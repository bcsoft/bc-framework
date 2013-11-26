package cn.bc.device.dao.hibernate.jpa;

import cn.bc.device.dao.DeviceDao;
import cn.bc.device.domain.Device;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 设备Dao实现
 * 
 * 
 * @author hwx
 * 
 */
public class DeviceDaoImpl extends HibernateCrudJpaDao<Device> implements
		DeviceDao {
}
