package cn.bc.desktop.service;

import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.core.Entity;
import cn.bc.desktop.domain.Shortcut;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorRelation;
import cn.bc.identity.service.ActorRelationService;
import cn.bc.identity.service.ActorService;
import cn.bc.security.domain.Module;
import cn.bc.security.service.ModuleService;
import cn.bc.test.AbstractEntityCrudTest;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class ShortcutServiceImplTest extends
		AbstractEntityCrudTest<Long, Shortcut> {
	ShortcutService shortcutService;
	ActorService actorService;
	ModuleService moduleService;
	ActorRelationService actorRelationService;

	@Autowired
	public void setShortcutService(ShortcutService moduleService) {
		this.shortcutService = moduleService;
		this.crudOperations = moduleService;// 赋值基类的crud操作对象
	}

	@Autowired
	public void setActorRelationService(
			ActorRelationService actorRelationService) {
		this.actorRelationService = actorRelationService;
	}

	@Autowired
	public void setModuleService(ModuleService moduleService) {
		this.moduleService = moduleService;
	}

	@Autowired
	public void setActorService(ActorService actorService) {
		this.actorService = actorService;
	}

	@Override
	protected Shortcut createInstance(String config) {
		Shortcut shortcut = super.createInstance(config);
		shortcut.setOrder("01");
		shortcut.setName("name");
		return shortcut;
	}

	protected Shortcut createShortcut(Actor actor, Module module, String order) {
		Shortcut shortcut = super.createInstance(this.getDefaultConfig());
		shortcut.setOrder(order);
		shortcut.setActor((Actor)actor);
		shortcut.setModule(module);
		return shortcut;
	}

	@Test
	public void testFindByActorStandalone() {
		// 删除数据库中Shortcut，清空测试现场

		// user
		Actor user = this.createActor(Actor.TYPE_USER, "user1", null);
		this.actorService.save(user);
		Assert.assertNotNull(user.getId());

		// 仅属于user的Shortcut
		Shortcut shortcut4user = this.createInstance(this.getDefaultConfig());
		shortcut4user.setActor((Actor)user);
		this.shortcutService.save(shortcut4user);
		Assert.assertNotNull(shortcut4user.getId());

		// 反查
		List<Shortcut> shortcuts = this.shortcutService.findByActor(
				user.getId(), false, false);
		Assert.assertNotNull(shortcuts);
		Assert.assertEquals(1, shortcuts.size());
		Assert.assertEquals(shortcut4user, shortcuts.get(0));
	}

	@Test
	public void testFindByActorIncludeCommon() {
		// 删除数据库中Shortcut，清空测试现场
		deleteAll();

		// user
		Actor user = this.createActor(Actor.TYPE_USER, "user1", null);
		this.actorService.save(user);
		Assert.assertNotNull(user.getId());

		// 仅属于user的Shortcut
		Shortcut shortcut4user = this.createShortcut(user, null, "01");
		shortcut4user.setActor((Actor)user);
		this.shortcutService.save(shortcut4user);
		Assert.assertNotNull(shortcut4user.getId());

		// 通用的Shortcut
		Shortcut shortcut4common = this.createShortcut(null, null, "00");
		this.shortcutService.save(shortcut4common);
		Assert.assertNotNull(shortcut4common.getId());

		// 反查
		List<Shortcut> shortcuts = this.shortcutService.findByActor(
				user.getId(), false, true);
		Assert.assertNotNull(shortcuts);
		Assert.assertEquals(2, shortcuts.size());
		Assert.assertEquals(shortcut4common, shortcuts.get(0));
		Assert.assertEquals(shortcut4user, shortcuts.get(1));
	}

	@Test
	public void testFindCommon() {
		// 删除数据库中Shortcut，清空测试现场
		deleteAll();

		// 通用的Shortcut
		Shortcut shortcut4common = this.createInstance(this.getDefaultConfig());
		shortcut4common.setActor(null);
		this.shortcutService.save(shortcut4common);
		Assert.assertNotNull(shortcut4common.getId());

		// 反查
		List<Shortcut> shortcuts = this.shortcutService.findCommon();
		Assert.assertNotNull(shortcuts);
		Assert.assertEquals(1, shortcuts.size());
		Assert.assertEquals(shortcut4common, shortcuts.get(0));
	}

	@Test
	public void testFindByNullActor() {
		// 删除数据库中Shortcut，清空测试现场
		deleteAll();

		// 通用的Shortcut
		Shortcut shortcut4common = this.createInstance(this.getDefaultConfig());
		shortcut4common.setActor(null);
		this.shortcutService.save(shortcut4common);
		Assert.assertNotNull(shortcut4common.getId());

		// 反查
		List<Shortcut> shortcuts = this.shortcutService.findByActor(null,
				false, false);
		Assert.assertNotNull(shortcuts);
		Assert.assertEquals(1, shortcuts.size());
		Assert.assertEquals(shortcut4common, shortcuts.get(0));

		// 反查
		shortcuts = this.shortcutService.findByActor(null, true, false);
		Assert.assertNotNull(shortcuts);
		Assert.assertEquals(1, shortcuts.size());
		Assert.assertEquals(shortcut4common, shortcuts.get(0));

		// 反查
		shortcuts = this.shortcutService.findByActor(null, true, true);
		Assert.assertNotNull(shortcuts);
		Assert.assertEquals(1, shortcuts.size());
		Assert.assertEquals(shortcut4common, shortcuts.get(0));
	}

	@Test
	public void testFindByActorIncludeAncestor() {
		// 删除数据库中Shortcut，清空测试现场
		deleteAll();

		// 单位1
		Actor unit = this.createActor(Actor.TYPE_UNIT, "unit1", "00");
		this.actorService.save(unit);
		Assert.assertNotNull(unit.getId());

		// user，隶属于单位1
		Actor user = this.createActor(Actor.TYPE_USER, "user1", "01");
		this.actorService.save(user);
		Assert.assertNotNull(user.getId());
		ActorRelation ar = createActorRelation(unit, user,
				ActorRelation.TYPE_BELONG, null);
		actorRelationService.save(ar);

		// 仅属于user的Shortcut
		Shortcut shortcut4user = this.createShortcut(user, null, "01");
		this.shortcutService.save(shortcut4user);
		Assert.assertNotNull(shortcut4user.getId());

		// 仅属于unit的Shortcut
		Shortcut shortcut4unit = this.createShortcut(unit, null, "01");
		this.shortcutService.save(shortcut4unit);
		Assert.assertNotNull(shortcut4unit.getId());

		// 通用的Shortcut
		Shortcut shortcut4common = this.createShortcut(null, null, "01");
		this.shortcutService.save(shortcut4common);
		Assert.assertNotNull(shortcut4common.getId());

		// 反查
		List<Shortcut> shortcuts = this.shortcutService.findByActor(
				user.getId(), true, false);
		Assert.assertNotNull(shortcuts);
		Assert.assertEquals(2, shortcuts.size());
		Assert.assertEquals(shortcut4unit, shortcuts.get(0));
		Assert.assertEquals(shortcut4user, shortcuts.get(1));
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


	private Actor createActor(int type, String code, String order) {
		Actor actor = new Actor();
		actor.setType(type);
		actor.setInner(false);
		actor.setStatus(Entity.STATUS_ENABLED);
		actor.setUid(UUID.randomUUID().toString());
		actor.setCode(code);
		actor.setOrder(order);
		actor.setName("测试" + code);

		return actor;
	}
}
