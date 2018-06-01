package cn.bc.photo.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.photo.domain.IpCamera;

import java.util.List;

/**
 * IP摄像头Dao接口
 *
 * @author dragon
 */
public interface IpCameraDao extends CrudDao<IpCamera> {
  /**
   * 获取指定用户的可用IP摄像头
   *
   * @param ownerId 拥有者的ID
   * @return 包含公共摄像头和拥有者特定摄像头
   */
  List<IpCamera> findByOwner(Long ownerId);
}
