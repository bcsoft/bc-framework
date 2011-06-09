package cn.bc.desktop.service;

import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.core.Entity;
import cn.bc.desktop.domain.Personal;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
import cn.bc.test.AbstractEntityCrudTest;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class PersionServiceImplTest extends
		AbstractEntityCrudTest<Long, Personal> {
	PersonalService personalService;
	ActorService actorService;

	@Autowired
	public void setPersonalService(
			PersonalService personalService) {
		this.personalService = personalService;
		this.crudOperations = personalService;// ��ֵ�����crud��������
	}

	@Autowired
	public void setActorService(ActorService actorService) {
		this.actorService = actorService;
	}

	@Override
	protected Personal createInstance(String config) {
		Personal c = super.createInstance(config);
		c.setFont("14");
		c.setTheme("base");
		return c;
	}

	protected Personal createPersonal(Actor actor) {
		Personal c = this.createInstance(this.getDefaultConfig());
		c.setActor((Actor)actor);
		return c;
	}

	@Test
	public void testLoadGlobal() {
		// ��ղ����ֳ�
		deleteAll();

		// ͨ�õ�
		Personal personal4common = this.createPersonal(null);
		this.personalService.save(personal4common);
		Assert.assertNotNull(personal4common.getId());

		// ����
		Personal c = this.personalService.loadGlobalConfig();
		Assert.assertEquals(personal4common, c);
	}

	@Test
	public void testLoadByActor() {
		// ��ղ����ֳ�
		deleteAll();

		// user
		Actor user = this.createActor(Actor.TYPE_USER, "user1", "01");
		this.actorService.save(user);
		Assert.assertNotNull(user.getId());

		// ͨ�õ�
		Personal personal4common = this.createPersonal(null);
		this.personalService.save(personal4common);
		Assert.assertNotNull(personal4common.getId());

		// ����
		Personal c = this.personalService.loadByActor(user.getId(),true);
		Assert.assertEquals(personal4common, c);
		c = this.personalService.loadByActor(user.getId());
		Assert.assertNull(c);

		// ר�õ�
		Personal personal4user = this.createPersonal(user);
		this.personalService.save(personal4user);
		Assert.assertNotNull(personal4user.getId());

		// ����
		c = this.personalService.loadByActor(user.getId());
		Assert.assertEquals(personal4user, c);
		c = this.personalService.loadByActor(user.getId(),true);
		Assert.assertEquals(personal4user, c);
	}

	@Test
	public void testLoadByActorError() {
		// ��ղ����ֳ�
		deleteAll();

		Assert.assertNull(this.personalService.loadByActor(new Long(1)));
	}

	private Actor createActor(int type, String code, String order) {
		Actor actor = new Actor();
		actor.setType(type);
		actor.setInner(false);
		actor.setStatus(Entity.STATUS_ENABLED);
		actor.setUid(UUID.randomUUID().toString());
		actor.setCode(code);
		actor.setOrder(order);
		actor.setName("����" + code);

		return actor;
	}
}
