package cn.bc.identity.dao;

public interface PrivilegeDao {
  /**
   * 为角色添加用户
   *
   * @param roleId
   * @param userId
   */
  int addUser(Long roleId, Long userId);

  Long getActorbyHistoryActor(Long id);

  /**
   * 为角色添加权限，返回该权限的子部门
   *
   * @param roleId
   * @param actorId
   * @return
   */
  long addActorByRole(Long roleId, Long actorId);

  /**
   * 删除该角色的部门，单位，岗位，用户
   *
   * @param roleId
   * @param actorId
   */
  int deleteActorByRole(Long roleId, Long actorId);

  /**
   * 检查此角色是否已存在此actor
   *
   * @param roleId
   * @param actorId
   * @return
   */
  int ActorRelationIsExist(Long roleId, Long actorId);

  /**
   * 删除该角色的资源
   *
   * @param roleId
   * @param resourceId
   * @return
   */
  int deleteResourceByRole(long roleId, long resourceId);

  /**
   * 检查此角色的指定资源是否存在
   *
   * @param roleId
   * @param resourceId
   * @return
   */
  int RoleResouceIsExist(long roleId, long resourceId);

  /**
   * 为该角色添加指定资源
   *
   * @param roleId
   * @param resourceId
   * @return
   */
  long addResourceByRole(long roleId, long resourceId);

  /**
   * 根据id获取名字
   *
   * @param roleId
   * @return
   */
  String getRoleNameById(long roleId);
}
