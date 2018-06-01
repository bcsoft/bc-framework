package cn.bc.identity.dao;

import cn.bc.core.dao.CrudDao;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.dto.DepartmentByActorDto4MiniInfo;

import java.util.List;

/**
 * Actor隶属信息的变动历史Dao接口
 *
 * @author dragon
 */
public interface ActorHistoryDao extends CrudDao<ActorHistory> {
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
   * @return 用户当前所属部门的简易信息 {@link DepartmentByActorDto4MiniInfo} 集合
   */
  List<DepartmentByActorDto4MiniInfo> findDepartmentMiniInfoByActors(Long[] actorHistoryIds);
}
