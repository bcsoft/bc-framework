package cn.bc.subscribe.service;

import cn.bc.core.service.CrudService;
import cn.bc.identity.domain.Actor;
import cn.bc.subscribe.domain.Subscribe;
import cn.bc.subscribe.domain.SubscribeActor;

import java.util.List;

/**
 * 订阅者Service接口
 *
 * @author lbj
 */
public interface SubscribeActorService extends CrudService<SubscribeActor> {

  /**
   * 查找订阅者
   *
   * @param subscribe
   * @return
   */
  List<SubscribeActor> findList(Subscribe subscribe);

  /**
   * 查找订阅者用户(包含：用户，岗位，单位，部门)
   *
   * @param subscribe
   * @return
   */
  List<Actor> findList2Actor(Subscribe subscribe);

  /**
   * 订阅人添加订阅
   *
   * @param aid  用户/岗位/单位/部门  id
   * @param pid  订阅Id
   * @param type 类型 0-用户订阅， 1-系统推送
   */
  void save(Long aid, Long pid, int type);


  /**
   * 删除订阅
   *
   * @param aid 用户/岗位/单位/部门  id
   * @param pid 订阅Id
   */
  void delete(Long aid, Long pid);


  /**
   * 查找订阅者
   *
   * @param aid
   * @param pid
   * @return
   */
  SubscribeActor find4aidpid(Long aid, Long pid);

}