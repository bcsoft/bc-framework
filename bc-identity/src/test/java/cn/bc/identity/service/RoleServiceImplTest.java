package cn.bc.identity.service;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.BCConstants;
import cn.bc.identity.domain.Resource;
import cn.bc.identity.domain.Role;
import cn.bc.test.AbstractEntityCrudTest;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class RoleServiceImplTest extends AbstractEntityCrudTest<Long,Role> {
	ResourceService resourceService;
	RoleService roleService;
	@Autowired
	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}
	@Autowired
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
		this.crudOperations = roleService;//赋值基类的crud操作对象
	}

	@Override
	protected Role createInstance(String config) {
		Role entity = super.createInstance(config);
		entity.setUid(null);
		
		//补充一些额外的设置
		entity.setCode("test");
		entity.setName(entity.getCode());
		
		return entity;
	}

	@Test
	public void testSaveWithResources() {
		saveOneWithResources();
	}
	
	private Role saveOneWithResources() {
		Role role = this.createInstance(this.getDefaultConfig());
		
		Resource module = createResource();
		module.setOrderNo("test1");
		this.resourceService.save(module);
		Assert.assertNotNull(module.getId());
		Set<Resource> ms = new LinkedHashSet<Resource>();//使用有序的Set
		ms.add(module);
		Long mid = module.getId();
		
		module = createResource();
		module.setOrderNo("test2");
		this.resourceService.save(module);
		ms.add(module);
		Assert.assertNotNull(module.getId());
		
		role.setResources(ms);

		this.roleService.save(role);
		Assert.assertNotNull(role.getId());
		Assert.assertNotNull(role.getResources());
		Assert.assertEquals(2, role.getResources().size());
		Assert.assertEquals(mid, role.getResources().iterator().next().getId());
		return role;
	}
	
	private Resource createResource() {
		Resource module = new Resource();
		module.setType(Resource.TYPE_INNER_LINK);
		module.setStatus(BCConstants.STATUS_ENABLED);
		//module.setInner(false);
		module.setOrderNo("test"); 
		module.setName(module.getOrderNo());
		return module;
	}

	@Test
	public void testLoadWithResources() {
		// 先插入一条数据
		Role role = saveOneWithResources();
		Long id = role.getId();
		Long mid = role.getResources().iterator().next().getId();

		//强制重新从数据库加载，如果直接使用load，因还在同一事务内，不会重新加载
		role = this.roleService.load(id);//TODO .forceLoad(id)
		
		Assert.assertNotNull(role);
		Assert.assertNotNull(role.getResources());
		Assert.assertEquals(2, role.getResources().size());
		Assert.assertEquals(mid, role.getResources().iterator().next().getId());
	}
}
