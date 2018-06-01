package cn.bc.remoting.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 通用的服务接口
 *
 * @author dragon
 */
public interface CommonService extends Remote {
  /**
   * 用于测试接口是否正常的方法
   *
   * @param token 连接令牌
   * @return
   * @throws RemoteException
   */
  boolean test(String token) throws RemoteException;
}
