package cn.bc.identity.service;

import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.core.Entity;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorDetail;
import cn.bc.identity.domain.ActorRelation;
import cn.bc.identity.service.ActorRelationService;
import cn.bc.identity.service.ActorService;
import cn.bc.security.domain.Module;
import cn.bc.security.domain.Role;
import cn.bc.security.service.ModuleService;
import cn.bc.security.service.RoleService;
import cn.bc.test.AbstractEntityCrudTest;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class ActorServiceImplTest extends AbstractEntityCrudTest<Long, Actor> {
	ActorService actorService;
	ActorRelationService actorRelationService;

	@Autowired
	public void setActorService(ActorService actorService) {
		this.actorService = actorService;
		this.crudOperations = actorService;// 赋值基类的crud操作对象
	}

	RoleService roleService;

	@Autowired
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	ModuleService moduleService;

	@Autowired
	public void setModuleService(ModuleService moduleService) {
		this.moduleService = moduleService;
	}

	@Autowired
	public void setActorRelationService(
			ActorRelationService actorRelationService) {
		this.actorRelationService = actorRelationService;
	}

	@Override
	protected Actor createInstance(String config) {
		Actor actor = new Actor();

		// 补充一些必填域的设置
		actor.setType(Actor.TYPE_USER);
		actor.setInner(false);
		actor.setStatus(Entity.STATUS_ENABLED);
		actor.setUid(UUID.randomUUID().toString());
		actor.setCode("test");
		actor.setName("测试");

		return actor;
	}

	@Test
	public void testSaveWithDetail() {
		saveOneWithDetail();
	}

	private Actor saveOneWithDetail() {
		// 先插入一条数据
		Actor entity = this.createInstance(this.getDefaultConfig());
		ActorDetail detail = new ActorDetail();
		Calendar now = Calendar.getInstance();
		now.set(Calendar.YEAR, 2010);
		now.set(Calendar.MONTH, 0);
		now.set(Calendar.DATE, 1);
		now.set(Calendar.HOUR_OF_DAY, 20);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		detail.set("createDate", now);
		entity.setDetail(detail);

		Assert.assertNull(entity.getId());
		Assert.assertNull(entity.getDetail().getId());
		this.actorService.save(entity);
		Assert.assertNotNull(entity.getId());
		Assert.assertNotNull(entity.getDetail().getId());
		Assert.assertEquals(now, entity.getDetail().getCalendar("createDate"));
		Assert.assertEquals(now,
				((ActorDetail) entity.getDetail()).getCreateDate());
		return entity;
	}

	@Test
	public void testLoadAdmin() {
		String actorCode = "admin";
		Actor _user = this.actorService.loadByCode(actorCode);
		Assert.assertNotNull(_user);
//		Set<Role> roles = _user.getRoles();
//		Assert.assertNotNull(roles);
//		for(Role role:roles){
//			System.out.println(role.getName());
//			for(Module m:role.getModules()){
//				System.out.println(m.getName());
//			}
//		}
	}

	@Test
	public void testLoadByCode() {
		String actorCode = "adminadminadmin";
		// 先插入一条数据
		Actor user = this.createActor(Actor.TYPE_USER, actorCode);
		this.actorService.save(user);
		Assert.assertNotNull(user.getId());

		Actor _user = this.actorService.loadByCode(actorCode);
		Assert.assertNotNull(_user);
		Assert.assertEquals(user, _user);
	}

	@Test
	public void testLoadWithDetail() {
		// 先插入一条数据
		Actor entity = saveOneWithDetail();
		Long id = entity.getId();
		Calendar now = entity.getDetail().getCalendar("createDate");

		// 强制重新从数据库加载，如果直接使用load，因还在同一事务内，不会重新加载
		entity = this.actorService.load(id);
		//oracle：is javax.persistence.PersistenceException: org.hibernate.HibernateException: this instance does not yet exist as a row in the database
		//entity = this.actorService.forceLoad(id);

		Assert.assertNotNull(entity);
		Assert.assertNotNull(entity.getDetail());
		Assert.assertEquals(now, entity.getDetail().getCalendar("createDate"));
		Assert.assertEquals(now,
				((ActorDetail) entity.getDetail()).getCreateDate());
	}

	private Actor saveOneWithRoles() {
		Actor actor = this.createInstance(this.getDefaultConfig());

		// 添加两个角色
		Role role = createRole();
		role.setCode("test1");
		this.roleService.save(role);
		Assert.assertNotNull(role.getId());
		Set<Role> roles = new LinkedHashSet<Role>();// 使用有序的Set
		roles.add(role);
		Long rid = role.getId();// 记录第一个角色的id
		role = createRole();
		role.setCode("test2");
		this.roleService.save(role);
		roles.add(role);
		Assert.assertNotNull(role.getId());
		actor.setRoles(roles);

		this.actorService.save(actor);
		Assert.assertNotNull(actor.getId());
		Assert.assertNotNull(actor.getRoles());
		Assert.assertEquals(2, actor.getRoles().size());
		Assert.assertEquals(rid, actor.getRoles().iterator().next().getId());
		return actor;
	}

	private Role createRole() {
		Role role = new Role();
		role.setType(Role.TYPE_DEFAULT);
		role.setStatus(Entity.STATUS_ENABLED);
		role.setInner(false);
		role.setCode("test");
		role.setName(role.getCode());
		return role;
	}

	@Test
	public void testSaveWithRoles() {
		saveOneWithRoles();
	}

	@Test
	public void testLoadWithRoles() {
		// 插入2条数据
		Actor actor = saveOneWithRoles();
		Long id = actor.getId();
		Long mid = actor.getRoles().iterator().next().getId();

		// 强制重新从数据库加载，如果直接使用load，因还在同一事务内，不会重新加载
		actor = this.actorService.load(id);// TODO .forceLoad(id)

		Assert.assertNotNull(actor);
		Assert.assertNotNull(actor.getRoles());
		Assert.assertEquals(2, actor.getRoles().size());
		Assert.assertEquals(mid, actor.getRoles().iterator().next().getId());
	}

	private Actor saveOneWithRolesAndModule() {
		Actor actor = this.createInstance(this.getDefaultConfig());

		// 添加1个角色
		Role role = createRole();
		role.setCode("code1");
		this.roleService.save(role);
		Assert.assertNotNull(role.getId());
		Set<Role> roles = new LinkedHashSet<Role>();// 使用有序的Set
		roles.add(role);
		Long rid = role.getId();// 记录角色的id
		roles.add(role);
		Assert.assertNotNull(role.getId());
		// 为角色添加1个模块
		Module module = createModule();
		this.moduleService.save(module);
		Assert.assertNotNull(module.getId());
		Set<Module> ms = new LinkedHashSet<Module>();// 使用有序的Set
		ms.add(module);
		Long mid = module.getId();
		ms.add(module);
		Assert.assertNotNull(module.getId());

		role.setModules(ms);
		actor.setRoles(roles);
		this.actorService.save(actor);

		Assert.assertNotNull(actor.getId());
		Assert.assertNotNull(actor.getRoles());
		role = actor.getRoles().iterator().next();
		Assert.assertEquals(1, actor.getRoles().size());
		Assert.assertEquals(rid, role.getId());

		Assert.assertNotNull(role.getModules());
		Assert.assertEquals(1, role.getModules().size());
		Assert.assertEquals(mid, role.getModules().iterator().next().getId());

		return actor;
	}

	private Module createModule() {
		Module module = new Module();
		module.setType(Module.TYPE_INNER_LINK);
		module.setStatus(Entity.STATUS_ENABLED);
		module.setInner(false);
		module.setCode("test");
		module.setName(module.getCode());
		return module;
	}

	@Test
	public void testSaveWithRolesAndModule() {
		saveOneWithRolesAndModule();
	}

	@Test
	//@Rollback(false)
	public void testFindFollower() {
		// 单位
		Actor unit = this.createActor(Actor.TYPE_UNIT, "unit1");
		this.actorService.save(unit);
		Assert.assertNotNull(unit.getId());

		// 单位下的子单位1
		Actor cunit = this.createActor(Actor.TYPE_UNIT, "unit1-1");
		this.actorService.save(cunit);
		Assert.assertNotNull(cunit.getId());
		ActorRelation ar0 = createActorRelation(unit, cunit,
				ActorRelation.TYPE_BELONG, null);
		actorRelationService.save(ar0);

		// 单位下的部门1
		Actor dep1 = this.createActor(Actor.TYPE_DEPARTMENT, "dep-b");
		this.actorService.save(dep1);
		Assert.assertNotNull(dep1.getId());
		ActorRelation ar1 = createActorRelation(unit, dep1,
				ActorRelation.TYPE_BELONG, null);
		actorRelationService.save(ar1);

		// 单位下的部门2
		Actor dep2 = this.createActor(Actor.TYPE_DEPARTMENT, "dep-a");
		this.actorService.save(dep2);
		Assert.assertNotNull(dep2.getId());
		ActorRelation ar2 = createActorRelation(unit, dep2,
				ActorRelation.TYPE_BELONG, null);
		actorRelationService.save(ar2);

		// 部门1下的子部门1
		Actor cdep1 = this.createActor(Actor.TYPE_DEPARTMENT, "cdep1");
		this.actorService.save(cdep1);
		Assert.assertNotNull(cdep1.getId());
		ActorRelation ar3 = createActorRelation(dep1, cdep1,
				ActorRelation.TYPE_BELONG, null);
		actorRelationService.save(ar3);

		// 反查单位1下的部门列表
		List<Actor> children = this.actorService.findFollower(unit.getId(),
				new Integer[] { ActorRelation.TYPE_BELONG },
				new Integer[] { Actor.TYPE_DEPARTMENT });
		Assert.assertNotNull(children);
		Assert.assertEquals(2, children.size());
		Assert.assertEquals(dep2, children.get(0));
		Assert.assertEquals(dep1, children.get(1));

		// 反查单位1下的子单位列表
		children = this.actorService.findFollower(unit.getId(),
				new Integer[] { ActorRelation.TYPE_BELONG },
				new Integer[] { Actor.TYPE_UNIT });
		Assert.assertNotNull(children);
		Assert.assertEquals(1, children.size());
		Assert.assertEquals(cunit, children.get(0));

		// 反查单位1下的子单位+部门列表
		children = this.actorService.findFollower(unit.getId(),
				new Integer[] { ActorRelation.TYPE_BELONG }, new Integer[] {
						Actor.TYPE_UNIT, Actor.TYPE_DEPARTMENT });
		Assert.assertNotNull(children);
		Assert.assertEquals(3, children.size());
		Assert.assertEquals(cunit, children.get(0));
		Assert.assertEquals(dep2, children.get(1));
		Assert.assertEquals(dep1, children.get(2));

		// 反查部门1下的子部门列表
		children = this.actorService.findFollower(dep1.getId(),
				new Integer[] { ActorRelation.TYPE_BELONG },
				new Integer[] { Actor.TYPE_DEPARTMENT });
		Assert.assertNotNull(children);
		Assert.assertEquals(1, children.size());
		Assert.assertEquals(cdep1, children.get(0));
	}

	@Test
	public void testFindMaster() {
		// 单位
		Actor unit = this.createActor(Actor.TYPE_UNIT, "unit1");
		this.actorService.save(unit);
		Assert.assertNotNull(unit.getId());

		// 单位下的子单位1
		Actor cunit = this.createActor(Actor.TYPE_UNIT, "unit1-1");
		this.actorService.save(cunit);
		Assert.assertNotNull(cunit.getId());
		ActorRelation ar0 = createActorRelation(unit, cunit,
				ActorRelation.TYPE_BELONG, null);
		actorRelationService.save(ar0);

		// 单位下的部门1
		Actor dep1 = this.createActor(Actor.TYPE_DEPARTMENT, "dep-b");
		this.actorService.save(dep1);
		Assert.assertNotNull(dep1.getId());
		ActorRelation ar1 = createActorRelation(unit, dep1,
				ActorRelation.TYPE_BELONG, "02");
		actorRelationService.save(ar1);

		// 单位下的部门2
		Actor dep2 = this.createActor(Actor.TYPE_DEPARTMENT, "dep-a");
		this.actorService.save(dep2);
		Assert.assertNotNull(dep2.getId());
		ActorRelation ar2 = createActorRelation(unit, dep2,
				ActorRelation.TYPE_BELONG, "01");// 隶属单位1
		actorRelationService.save(ar2);
		ActorRelation ar22 = createActorRelation(cunit, dep2,
				ActorRelation.TYPE_BELONG, null);// 同时隶属单位1下的子单位1: 矩阵式结构
		actorRelationService.save(ar22);

		// 部门1下的子部门1
		Actor cdep1 = this.createActor(Actor.TYPE_DEPARTMENT, "cdep1");
		this.actorService.save(cdep1);
		Assert.assertNotNull(cdep1.getId());
		ActorRelation ar3 = createActorRelation(dep1, cdep1,
				ActorRelation.TYPE_BELONG, "01");
		actorRelationService.save(ar3);

		// 反查子单位1的上级单位1
		List<Actor> parents = this.actorService.findMaster(cunit.getId(),
				new Integer[] { ActorRelation.TYPE_BELONG },
				new Integer[] { Actor.TYPE_UNIT });
		Assert.assertNotNull(parents);
		Assert.assertEquals(1, parents.size());
		Assert.assertEquals(unit, parents.get(0));

		// 反查部门1的上级单位1
		parents = this.actorService.findMaster(dep1.getId(),
				new Integer[] { ActorRelation.TYPE_BELONG },
				new Integer[] { Actor.TYPE_UNIT });
		Assert.assertNotNull(parents);
		Assert.assertEquals(1, parents.size());
		Assert.assertEquals(unit, parents.get(0));

		// 反查部门2的上级单位+部门
		parents = this.actorService.findMaster(dep2.getId(),
				new Integer[] { ActorRelation.TYPE_BELONG }, new Integer[] {
						Actor.TYPE_UNIT, Actor.TYPE_DEPARTMENT });
		Assert.assertNotNull(parents);
		Assert.assertEquals(2, parents.size());
		Assert.assertEquals(unit, parents.get(0));
		Assert.assertEquals(cunit, parents.get(1));

		// 反查子部门1的上级部门
		parents = this.actorService.findMaster(cdep1.getId(),
				new Integer[] { ActorRelation.TYPE_BELONG },
				new Integer[] { Actor.TYPE_DEPARTMENT });
		Assert.assertNotNull(parents);
		Assert.assertEquals(1, parents.size());
		Assert.assertEquals(dep1, parents.get(0));
	}

	@Test
	public void testFindTopUnit() {
		// 单位1
		Actor unit = this.createActor(Actor.TYPE_UNIT, "unit1");
		this.actorService.save(unit);
		Assert.assertNotNull(unit.getId());

		// 单位1下的子单位1
		Actor cunit = this.createActor(Actor.TYPE_UNIT, "unit1-1");
		this.actorService.save(cunit);
		Assert.assertNotNull(cunit.getId());
		ActorRelation ar0 = createActorRelation(unit, cunit,
				ActorRelation.TYPE_BELONG, null);
		actorRelationService.save(ar0);

		// 单位下的部门1
		Actor dep1 = this.createActor(Actor.TYPE_DEPARTMENT, "dep-b");
		this.actorService.save(dep1);
		Assert.assertNotNull(dep1.getId());
		ActorRelation ar1 = createActorRelation(unit, dep1,
				ActorRelation.TYPE_BELONG, "02");
		actorRelationService.save(ar1);

		// 反查
		List<Actor> topUnits = this.actorService.findTopUnit();
		Assert.assertNotNull(topUnits);
		Assert.assertTrue(topUnits.size() >= 1);// 可能数据库中已经有了其他单位数据
		Actor theTopUnit = null;
		for (Actor topUnit : topUnits) {
			if (topUnit == unit) {
				theTopUnit = topUnit;
				break;
			}
		}
		Assert.assertNotNull(theTopUnit);
		Assert.assertEquals(unit, theTopUnit);
	}

	@Test
	public void testFindLowerOrganization() {
		// 单位1
		Actor unit = this.createActor(Actor.TYPE_UNIT, "unit1");
		this.actorService.save(unit);
		Assert.assertNotNull(unit.getId());

		// 单位1下的子单位1
		Actor cunit = this.createActor(Actor.TYPE_UNIT, "unit1-1");
		this.actorService.save(cunit);
		Assert.assertNotNull(cunit.getId());
		ActorRelation ar0 = createActorRelation(unit, cunit,
				ActorRelation.TYPE_BELONG, null);
		actorRelationService.save(ar0);

		// 单位下的部门1
		Actor dep1 = this.createActor(Actor.TYPE_DEPARTMENT, "dep-b");
		this.actorService.save(dep1);
		Assert.assertNotNull(dep1.getId());
		ActorRelation ar1 = createActorRelation(unit, dep1,
				ActorRelation.TYPE_BELONG, null);
		actorRelationService.save(ar1);

		// 反查
		List<Actor> lowerOrganizations = this.actorService
				.findLowerOrganization(unit.getId());
		Assert.assertNotNull(lowerOrganizations);
		Assert.assertEquals(2, lowerOrganizations.size());
		Assert.assertEquals(cunit, lowerOrganizations.get(0));
		Assert.assertEquals(dep1, lowerOrganizations.get(1));
	}

	@Test
	public void testFindHigherOrganization() {
		// 单位1
		Actor unit = this.createActor(Actor.TYPE_UNIT, "unit1");
		this.actorService.save(unit);
		Assert.assertNotNull(unit.getId());

		// 单位下的部门1
		Actor dep1 = this.createActor(Actor.TYPE_DEPARTMENT, "dep-b");
		this.actorService.save(dep1);
		Assert.assertNotNull(dep1.getId());
		ActorRelation ar1 = createActorRelation(unit, dep1,
				ActorRelation.TYPE_BELONG, "02");
		actorRelationService.save(ar1);

		// 反查
		List<Actor> higherOrganizations = this.actorService
				.findHigherOrganization(dep1.getId());
		Assert.assertNotNull(higherOrganizations);
		Assert.assertEquals(1, higherOrganizations.size());
		Assert.assertEquals(unit, higherOrganizations.get(0));
	}

	@Test
	public void testFindAncestorOrganization() {
		// 单位1
		Actor unit = this.createActor(Actor.TYPE_UNIT, "unit1");
		this.actorService.save(unit);
		Assert.assertNotNull(unit.getId());

		// 单位下的部门1
		Actor dep1 = this.createActor(Actor.TYPE_DEPARTMENT, "dep-b");
		this.actorService.save(dep1);
		Assert.assertNotNull(dep1.getId());
		ActorRelation ar1 = createActorRelation(unit, dep1,
				ActorRelation.TYPE_BELONG, "02");
		actorRelationService.save(ar1);

		// 单位下的部门2
		Actor dep2 = this.createActor(Actor.TYPE_DEPARTMENT, "dep-a");
		this.actorService.save(dep2);
		Assert.assertNotNull(dep2.getId());
		ActorRelation ar2 = createActorRelation(unit, dep2,
				ActorRelation.TYPE_BELONG, "01");// 隶属单位1
		actorRelationService.save(ar2);

		// 部门1下的子部门1
		Actor cdep1 = this.createActor(Actor.TYPE_DEPARTMENT, "cdep1");
		this.actorService.save(cdep1);
		Assert.assertNotNull(cdep1.getId());
		ActorRelation ar3 = createActorRelation(dep1, cdep1,
				ActorRelation.TYPE_BELONG, "01");
		actorRelationService.save(ar3);

		// 反查部门1祖先
		List<Actor> ancestors = this.actorService.findAncestorOrganization(dep1
				.getId());
		Assert.assertNotNull(ancestors);
		Assert.assertEquals(1, ancestors.size());
		Assert.assertEquals(unit, ancestors.get(0));

		// 反查部门2祖先
		ancestors = this.actorService.findAncestorOrganization(dep2.getId());
		Assert.assertNotNull(ancestors);
		Assert.assertEquals(1, ancestors.size());
		Assert.assertEquals(unit, ancestors.get(0));

		// 反查子部门1祖先
		ancestors = this.actorService.findAncestorOrganization(cdep1.getId());
		Assert.assertNotNull(ancestors);
		Assert.assertEquals(2, ancestors.size());
		Assert.assertEquals(unit, ancestors.get(0));
		Assert.assertEquals(dep1, ancestors.get(1));
	}

	@Test
	public void testFindDescendantOrganization() {
		// 单位1
		Actor unit = this.createActor(Actor.TYPE_UNIT, "unit1");
		this.actorService.save(unit);
		Assert.assertNotNull(unit.getId());

		// 单位下的部门1
		Actor dep1 = this.createActor(Actor.TYPE_DEPARTMENT, "dep-b");
		this.actorService.save(dep1);
		Assert.assertNotNull(dep1.getId());
		ActorRelation ar1 = createActorRelation(unit, dep1,
				ActorRelation.TYPE_BELONG, "02");
		actorRelationService.save(ar1);

		// 单位下的部门2
		Actor dep2 = this.createActor(Actor.TYPE_DEPARTMENT, "dep-a");
		this.actorService.save(dep2);
		Assert.assertNotNull(dep2.getId());
		ActorRelation ar2 = createActorRelation(unit, dep2,
				ActorRelation.TYPE_BELONG, "01");// 隶属单位1
		actorRelationService.save(ar2);

		// 部门1下的子部门1
		Actor cdep1 = this.createActor(Actor.TYPE_DEPARTMENT, "cdep1");
		this.actorService.save(cdep1);
		Assert.assertNotNull(cdep1.getId());
		ActorRelation ar3 = createActorRelation(dep1, cdep1,
				ActorRelation.TYPE_BELONG, "01");
		actorRelationService.save(ar3);

		// 反查部门1后代
		List<Actor> descendants = this.actorService
				.findDescendantOrganization(dep1.getId());
		Assert.assertNotNull(descendants);
		Assert.assertEquals(1, descendants.size());
		Assert.assertEquals(cdep1, descendants.get(0));

		// 反查部门2后代
		descendants = this.actorService
				.findDescendantOrganization(dep2.getId());
		Assert.assertNotNull(descendants);
		Assert.assertEquals(0, descendants.size());

		// 反查子单位1后代
		descendants = this.actorService
				.findDescendantOrganization(unit.getId());
		Assert.assertNotNull(descendants);
		Assert.assertEquals(3, descendants.size());
		Assert.assertEquals(dep2, descendants.get(0));
		Assert.assertEquals(dep1, descendants.get(1));
		Assert.assertEquals(cdep1, descendants.get(2));
	}

	@Test
	public void testFindUser() {
		// 单位1
		Actor unit = this.createActor(Actor.TYPE_UNIT, "unit1");
		this.actorService.save(unit);
		Assert.assertNotNull(unit.getId());

		// 单位下的人员1
		Actor user1 = this.createActor(Actor.TYPE_USER, "user1");
		this.actorService.save(user1);
		Assert.assertNotNull(user1.getId());
		ActorRelation ar1 = createActorRelation(unit, user1,
				ActorRelation.TYPE_BELONG, null);
		actorRelationService.save(ar1);

		// 反查
		List<Actor> users = this.actorService.findUser(unit.getId());
		Assert.assertNotNull(users);
		Assert.assertEquals(1, users.size());
		Assert.assertEquals(user1, users.get(0));
	}

	@Test
	public void testFindDescendantUser() {
		// 单位1
		Actor unit = this.createActor(Actor.TYPE_UNIT, "unit1");
		this.actorService.save(unit);
		Assert.assertNotNull(unit.getId());

		// 单位下的部门1
		Actor dep1 = this.createActor(Actor.TYPE_DEPARTMENT, "dep-b");
		this.actorService.save(dep1);
		Assert.assertNotNull(dep1.getId());
		ActorRelation ar1 = createActorRelation(unit, dep1,
				ActorRelation.TYPE_BELONG, "02");
		actorRelationService.save(ar1);

		// 单位下的人员
		Actor user1 = this.createActor(Actor.TYPE_USER, "user1");
		this.actorService.save(user1);
		Assert.assertNotNull(user1.getId());
		ActorRelation ar2 = createActorRelation(unit, user1,
				ActorRelation.TYPE_BELONG, null);
		actorRelationService.save(ar2);

		// 部门1下的人员
		Actor user2 = this.createActor(Actor.TYPE_USER, "user2");
		this.actorService.save(user2);
		Assert.assertNotNull(user2.getId());
		ActorRelation ar3 = createActorRelation(dep1, user2,
				ActorRelation.TYPE_BELONG, null);
		actorRelationService.save(ar3);

		// 反查部门1下的人员
		List<Actor> users = this.actorService.findDescendantUser(dep1.getId());
		Assert.assertNotNull(users);
		Assert.assertEquals(1, users.size());
		Assert.assertEquals(user2, users.get(0));

		// 反查单位下的人员
		users = this.actorService.findDescendantUser(unit.getId());
		Assert.assertNotNull(users);
		Assert.assertEquals(2, users.size());
		Assert.assertEquals(user1, users.get(0));
		Assert.assertEquals(user2, users.get(1));
	}

	private Actor createActor(int type, String code) {
		Actor actor = new Actor();
		actor.setType(type);
		actor.setInner(false);
		actor.setStatus(Entity.STATUS_ENABLED);
		actor.setUid(UUID.randomUUID().toString());
		actor.setCode(code);
		actor.setName("测试" + code);

		return actor;
	}

	private ActorRelation createActorRelation(Actor master, Actor follower,
			Integer type, String order) {
		ActorRelation ar = new ActorRelation();
		ar.setMaster(master);
		ar.setFollower(follower);
		ar.setType(type);
		ar.setOrder(order);
		return ar;
	}
}
