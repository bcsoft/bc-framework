package cn.bc.identity.service;

public interface PrivilegeService {
	/**
	 * 为角色添加用户
	 * @param roleId
	 * @param userId
	 */
	boolean addUser(long roleId, long userId);
	
	/**
	 * 为角色添加岗位
	 * @param roleId
	 * @param groupId
	 */
	boolean addGroup(long roleId, long groupId);
	
	/**
	 * 为该角色添加部门或单位
	 * @param roleId
	 * @param unitOrDepId
	 * @return
	 */
	boolean addUnitOrDep(long roleId, long unitOrDepId);
	
	/**
	 * 删除该角色的部门，单位，岗位，用户
	 * @param roleId
	 * @param actorId
	 */
	boolean deleteActor(long roleId, long actorId);
	
	/**
	 * 删除该角色的资源
	 * @param roleId
	 * @param resourId
	 * @return
	 */
	boolean deleteResource(long roleId, long resourceId);
	
	/**
	 * 为该角色添加资源
	 * @param roleId
	 * @param resourceId
	 * @return
	 */
	boolean addResource(long roleId, long resourceId);
	
	/**
	 * 根据id获取名字
	 * @param roleId
	 * @return
	 */
	String getRoleNameById(long roleId);
}
