package cn.bc.photo.service;


import cn.bc.core.service.CrudService;
import cn.bc.photo.domain.IpCamera;

import java.util.List;

/**
 * 网络摄像头Service接口
 *
 * @author dragon
 */
public interface IpCameraService extends CrudService<IpCamera> {
  /**
   * 获取指定用户的可用IP摄像头
   *
   * @param ownerId 拥有者的ID
   * @return 包含公共摄像头和拥有者特定摄像头
   */
  List<IpCamera> findByOwner(Long ownerId);
}
