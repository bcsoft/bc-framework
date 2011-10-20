package cn.bc.identity.dao;

import java.util.List;
import java.util.Map;

import cn.bc.core.dao.CrudDao;
import cn.bc.identity.domain.Role;

/**
 * 角色Dao接口
 * 
 * @author dragon
 * 
 */
public interface RoleDao extends CrudDao<Role> {
	/**
	 * 获取指定类型和状态的Role信息
	 * 
	 * @param types
	 *            类型列表
	 * @param statues
	 *            状态列表
	 * @return 返回结果中的元素Map格式为：
	 *         <ul>
	 *         <li>id -- Role的id</li>
	 *         <li>type -- Role的type</li>
	 *         <li>code -- Role的code</li>
	 *         <li>name -- Role的name</li>
	 *         </ul>
	 */
	List<Map<String, String>> find4option(Integer[] types, Integer[] statues);
}
