package cn.bc.subscribe.service;

import cn.bc.core.service.CrudService;
import cn.bc.identity.domain.Actor;
import cn.bc.subscribe.domain.Subscribe;

import java.util.List;

/**
 * 订阅Service接口
 *
 * @author lbj
 */
public interface SubscribeService extends CrudService<Subscribe> {

  /**
   * 根据时间编码查找 订阅
   *
   * @param eventCode
   * @return
   */
  Subscribe loadByEventCode(String eventCode);

  /**
   * 唯一性检测
   *
   * @param eventCode
   * @param id
   * @return
   */
  boolean isUnique(String eventCode, Long id);

  /**
   * 用户添加订阅
   *
   * @param subscribe
   */
  void doAddActor4Personal(Subscribe subscribe);

  /**
   * 用户添加多个订阅
   *
   * @param subscribes
   */
  void doAddActor4Personal(List<Subscribe> subscribes);

  /**
   * 用户删除订阅
   *
   * @param subscribe
   */
  void doDeleteActor4Personal(Subscribe subscribe);

  /**
   * 用户删除多个订阅
   *
   * @param subscribes
   */
  void doDeleteActor4Personal(List<Subscribe> subscribes);

  /**
   * 管理员 为 用户 设置一个系统推送的订阅
   *
   * @param subscribe
   * @param actor     用户
   */
  void doAddActor4Manager(Subscribe subscribe, Actor actor);


  /**
   * 管理员 为 多个用户 设置一个系统推送的订阅
   *
   * @param subscribe
   * @param actors
   */
  void doAddActor4Manager(Subscribe subscribe, List<Actor> actors);

  /**
   * 管理员 为 用户 取消一个系统推送的订阅
   *
   * @param subscribe
   * @param actor     用户
   */
  void doDeleteActor4Manager(Subscribe subscribe, Actor actor);


  /**
   * 管理员 为 多个用户 取消一个系统推送的订阅
   *
   * @param subscribe
   * @param actors
   */
  void doDeleteActor4Manager(Subscribe subscribe, List<Actor> actors);

}