package cn.bc.identity.service;

import cn.bc.core.service.CrudService;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.dto.DepartmentByActorDto4MiniInfo;

import java.util.List;

/**
 * Actor隶属信息的变动历史Service接口
 *
 * @author dragon
 */
public interface ActorHistoryService extends CrudService<ActorHistory> {
  /**
   * 获取Actor当前的隶属信息,如果没有就自动创建一个
   *
   * @param actorId
   * @return
   */
  ActorHistory loadCurrent(Long actorId);

  /**
   * 根据编码获取，如用户历史
   *
   * @param actorCode
   * @return
   */
  ActorHistory loadByCode(String actorCode);

  /**
   * 获取指定编码的姓名信息
   *
   * @param actorCode
   * @return
   */
  List<String> findNames(List<String> actorCode);

  /**
   * 批量获取指定用户当前所属部门的简易信息
   *
   * @param actorHistoryIds 用户历史 Id 数组
   * @return {@link DepartmentByActorDto4MiniInfo} 集合
   */
  List<DepartmentByActorDto4MiniInfo> findDepartmentMiniInfoByActors(Long[] actorHistoryIds);
}