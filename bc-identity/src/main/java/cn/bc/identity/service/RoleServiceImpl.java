package cn.bc.identity.service;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.dao.RoleDao;
import cn.bc.identity.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 角色Service接口的实现
 *
 * @author dragon
 */
public class RoleServiceImpl extends DefaultCrudService<Role> implements
		RoleService {
	private RoleDao roleDao;

	@Autowired
	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
		this.setCrudDao(roleDao);
	}

	public List<Map<String, String>> find4option(Integer[] types, Integer[] statues) {
		return roleDao.find4option(types, statues);
	}

	@Override
	public String getRoleNameById(Long roleId) {
		return this.roleDao.getRoleNameById(roleId);
	}

	@Override
	@Transactional
	public int addResource(Long roleId, Long[] resourceIds) {
		return this.roleDao.addResource(roleId, resourceIds);
	}

	@Override
	@Transactional
	public int deleteResource(Long roleId, Long[] resourceIds) {
		return this.roleDao.deleteResource(roleId, resourceIds);
	}
}
