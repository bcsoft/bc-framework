package cn.bc.photo.service;


import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.IsNullCondition;
import cn.bc.core.query.condition.impl.OrCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.CrudService;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.photo.dao.IpCameraDao;
import cn.bc.photo.domain.IpCamera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 网络摄像头Service接口
 * 
 * @author dragon
 * 
 */
public class IpCameraServiceImpl extends DefaultCrudService<IpCamera> implements IpCameraService{
	private IpCameraDao ipCameraDao;

	public void setIpCameraDao(IpCameraDao ipCameraDao) {
		this.ipCameraDao = ipCameraDao;
		this.setCrudDao(ipCameraDao);
	}

	public List<IpCamera> findByOwner(Long ownerId) {
		return this.ipCameraDao.findByOwner(ownerId);
	}
}
