package cn.bc.identity.service;

import java.util.List;
import java.util.Map;

/**
 * 专为登录设置的Service接口，目的是不使用事务直接加载相关信息
 * 
 * @author dragon
 * 
 */
public interface LoginService {
	/**
	 * 根据编码获取，如用户的相关信息
	 * 
	 * @param actorCode
	 *            actor的编码
	 * @return 返回的对象包括了Actor、AuthData、ActorHistory
	 */
	Map<String, Object> loadActorByCode(String actorCode);

	/**
	 * 获取指定用户的直属上级
	 * 
	 * @param actorId
	 * @return 返回的列表中的元素的key为：
	 *         <ul>
	 *         <li>fid follower的id</li>
	 *         <li>id actor的id</li>
	 *         <li>type actor的type</li>
	 *         <li>code actor的code</li>
	 *         <li>name actor的name</li>
	 *         <li>pcode actor的pcode</li>
	 *         <li>pname actor的pname</li>
	 *         </ul>
	 */
	List<Map<String, String>> findActorAncestors(Long actorId);

	/**
	 * 获取指定actor可访问角色的编码列表
	 * 
	 * @param actorIds
	 * @return
	 */
	List<String> findActorRoles(List<Long> actorIds);
}
