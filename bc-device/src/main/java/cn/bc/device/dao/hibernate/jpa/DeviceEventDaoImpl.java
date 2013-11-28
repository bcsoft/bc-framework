package cn.bc.device.dao.hibernate.jpa;

import cn.bc.device.dao.DeviceEventDao;
import cn.bc.device.domain.DeviceEventDomain;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 设备事件Dao实现
 * 
 * 
 * @author hwx
 * 
 */
public class DeviceEventDaoImpl extends HibernateCrudJpaDao<DeviceEventDomain>
		implements DeviceEventDao {

}
