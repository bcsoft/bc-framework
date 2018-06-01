package cn.bc.photo.service;


import cn.bc.core.service.DefaultCrudService;
import cn.bc.photo.dao.IpCameraDao;
import cn.bc.photo.domain.IpCamera;

import java.util.List;

/**
 * 网络摄像头Service接口
 *
 * @author dragon
 */
public class IpCameraServiceImpl extends DefaultCrudService<IpCamera> implements IpCameraService {
  private IpCameraDao ipCameraDao;

  public void setIpCameraDao(IpCameraDao ipCameraDao) {
    this.ipCameraDao = ipCameraDao;
    this.setCrudDao(ipCameraDao);
  }

  public List<IpCamera> findByOwner(Long ownerId) {
    return this.ipCameraDao.findByOwner(ownerId);
  }
}
