/**
 *
 */
package cn.bc.identity.web;

import cn.bc.Context;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;

/**
 * 系统上下文
 *
 * @author dragon
 */
public interface SystemContext extends Context {
	/**
	 * 系统上下文内保存用户信息的键值
	 */
	public static final String KEY_USER = "user";
	/**
	 * 系统上下文内保存用户隶属的最新历史信息的键值
	 */
	public static final String KEY_USER_HISTORY = "userHistory";
	/**
	 * 系统上下文内保存用户的直接上级信息的键值
	 */
	public static final String KEY_BELONG = "belong";
	/**
	 * 系统上下文内保存用户的直接上级信息的键值
	 */
	public static final String KEY_BELONGS = "belongs";
	/**
	 * 系统上下文内保存用户所在单位信息的键值
	 */
	public static final String KEY_UNIT = "unit";
	/**
	 * 系统上下文内保存用户所在单位信息的键值
	 */
	public static final String KEY_UNITS = "units";
	/**
	 * 系统上下文内保存用户所拥有的角色信息的键值
	 */
	public static final String KEY_ROLES = "roles";
	public static final String KEY_ROLEIDS = "roleIds";
	/**
	 * 系统上下文内保存用户所拥有的岗位信息的键值
	 */
	public static final String KEY_GROUPS = "groups";
	/**
	 * 系统上下文内保存用户的祖先信息的键值
	 */
	public static final String KEY_ANCESTORS = "ancestors";
	/**
	 * 系统上下文路径信息的键值
	 */
	public static final String KEY_HTMLPAGENAMESPACE = "htmlPageNamespace";
	/**
	 * 系统完整的URl访问路径
	 */
	public static final String KEY_SYSURL = "sysurl";
	public static final String KEY_SYSURL_OUTERNET = "sysurl_outernnet";
	/**
	 * 系统上下文路径KEY
	 */
	public static final String KEY_SYSCONTEXTPATH = "sysContextPath";
	/**
	 * 时间戳
	 */
	public static final String KEY_APPTS = "appTs";

	/**
	 * 获取当前登录的用户
	 *
	 * @return
	 */
	Actor getUser();

	/**
	 * 获取当前登录的用户
	 *
	 * @return
	 */
	ActorHistory getUserHistory();

	/**
	 * 获取登录用户所属的部门或单位信息
	 *
	 * @return
	 */
	Actor getBelong();

	/**
	 * 获取登录用户所属的单位信息
	 *
	 * @return
	 */
	Actor getUnit();

	/**
	 * 判断用户是否拥有指定角色中的任一个
	 *
	 * @param roles 角色的标识符，通常为角色的编码
	 * @return
	 */
	boolean hasAnyRole(String... roles);

	/**
	 * 判断用户是否拥有指定角色中的任一个
	 *
	 * @param roles 角色的标识符，通常为角色的编码,多个逗号链接
	 * @return
	 */
	boolean hasAnyOneRole(String roles);

	/**
	 * 判断用户是否拥有指定岗位中的任一个
	 *
	 * @param groups 岗位的标识符，通常为岗位的编码
	 * @return
	 */
	boolean hasAnyGroup(String... groups);

	/**
	 * 判断用户是否拥有指定岗位中的任一个
	 *
	 * @param groups 岗位的标识符，通常为岗位的编码,多个逗号链接
	 * @return
	 */
	boolean hasAnyOneGroup(String groups);

	/**
	 * 获取访问系统的上下文路径，如 "/bs"
	 *
	 * @return 系统的上下文路径，如 "/bs"
	 */
	String getContextPath();
}