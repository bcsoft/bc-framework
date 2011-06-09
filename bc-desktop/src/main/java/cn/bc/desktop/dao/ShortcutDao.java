package cn.bc.desktop.dao;

import java.util.List;
import java.util.Set;

import cn.bc.core.dao.CrudDao;
import cn.bc.desktop.domain.Shortcut;
import cn.bc.identity.domain.Actor;
import cn.bc.security.domain.Module;
import cn.bc.security.domain.Role;

/**
 * ShortcutDao接口
 * 
 * @author dragon
 * 
 */
public interface ShortcutDao extends CrudDao<Shortcut> {
	/**
	 * 获取actor可以使用的快捷方式
	 * 
	 * @param actorId
	 *            参与者的id,为空代表获取通用的快捷方式
	 * @param includeAncestor
	 *            是否包含上级组织可使用快捷方式（包括上级的上级）
	 * @param includeCommon
	 *            是否包含通用快捷方式（actor为null的）
	 * @param ancestorOrganizations
	 *            返回actor所隶属的组织
	 * @param roles
	 *            返回actor可使用的角色
	 * @param modules
	 *            返回actor可使用的模块
	 * @return
	 */
	List<Shortcut> findByActor(Long actorId, boolean includeAncestor,
			boolean includeCommon, Set<Actor> ancestorOrganizations,
			Set<Role> roles, Set<Module> modules);

	List<Shortcut> findByActor(Long actorId, boolean includeAncestor,
			boolean includeCommon);
}
