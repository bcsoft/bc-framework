package cn.bc.identity.service;

import java.util.List;
import java.util.Map;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.dao.RoleDao;
import cn.bc.identity.domain.Role;

/**
 * 角色Service接口的实现
 * 
 * @author dragon
 * 
 */
public class RoleServiceImpl extends DefaultCrudService<Role> implements
		RoleService {
	private RoleDao roleDao;

	public RoleDao getRoleDao() {
		return roleDao;
	}

	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
		this.setCrudDao(roleDao);
	}

	public List<Map<String, String>> find4option(Integer[] types,
			Integer[] statues) {
		return roleDao.find4option(types, statues);
	}

}
