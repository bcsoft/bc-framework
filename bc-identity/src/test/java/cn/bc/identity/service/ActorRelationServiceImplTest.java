package cn.bc.identity.service;

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
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorRelation;
import cn.bc.identity.domain.ActorRelationPK;
import cn.bc.identity.service.ActorRelationService;
import cn.bc.identity.service.ActorService;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class ActorRelationServiceImplTest {
	ActorRelationService actorRelationService;
	ActorService actorService;

	@Autowired
	public void setActorRelationService(
			ActorRelationService actorRelationService) {
		this.actorRelationService = actorRelationService;
	}

	@Autowired
	public void setActorService(ActorService actorService) {
		this.actorService = actorService;
	}

	@Test
	public void testLoad() {
		ActorRelation ar1 = saveOne();
		ActorRelation ar2 = actorRelationService.load(ar1.getType(), ar1
				.getMaster().getId(), ar1.getFollower().getId());
		Assert.assertNotNull(ar2);
		Assert.assertEquals(ar1, ar2);
	}

	@Test
	public void testLoadByPK() {
		ActorRelation ar = saveOne();
		Long mid = ar.getMaster().getId();
		Long fid = ar.getFollower().getId();
		Integer type = ar.getType();
		String order = ar.getOrder();

		ActorRelationPK id = new ActorRelationPK(ar.getMaster(),
				ar.getFollower(), ar.getType());
		ar = actorRelationService.load(id);
		Assert.assertNotNull(ar);
		Assert.assertEquals(mid, ar.getMaster().getId());
		Assert.assertEquals(fid, ar.getFollower().getId());
		Assert.assertEquals(type, ar.getType());
		Assert.assertEquals(order, ar.getOrder());
	}

	@Test(expected = NullPointerException.class)
	// 主键为空无法保存
	public void testSaveError() {
		Actor master = createActor(Actor.TYPE_UNIT, "mtest1");
		// actorService.save(master);

		Actor follower = createActor(Actor.TYPE_DEPARTMENT, "ftest1");
		// actorService.save(follower);

		ActorRelation ar = createInstance(master, follower,
				ActorRelation.TYPE_BELONG, "01");
		actorRelationService.save(ar);
	}

	@Test(expected = org.springframework.orm.jpa.JpaSystemException.class)
	// 游离实体无法保存
	// org.hibernate.PersistentObjectException: detached entity passed to
	// persist: cn.bc.identity.domain.Actor
	public void testSaveErrorByDetachedEntity() {
		Actor master = createActor(Actor.TYPE_UNIT, "mtest1");
		actorService.save(master);
		Long id = master.getId();
		master = createActor(Actor.TYPE_UNIT, "mtest1");// 使master处于游离状态
		master.setId(id);

		Actor follower = createActor(Actor.TYPE_DEPARTMENT, "ftest1");
		actorService.save(follower);
		id = follower.getId();
		follower = createActor(Actor.TYPE_DEPARTMENT, "ftest1");// 使follower处于游离状态
		follower.setId(id);

		ActorRelation ar = createInstance(master, follower,
				ActorRelation.TYPE_BELONG, "01");
		actorRelationService.save(ar);
	}

	@Test
	//@Rollback(false)
	public void testSave() {
		saveOne();
	}

	private ActorRelation saveOne() {
		Actor master = createActor(Actor.TYPE_UNIT, "mtest1");
		actorService.save(master);
		Assert.assertNotNull(master.getId());

		Actor follower = createActor(Actor.TYPE_DEPARTMENT, "ftest1");
		actorService.save(follower);
		Assert.assertNotNull(follower.getId());

		ActorRelation ar = createInstance(master, follower,
				ActorRelation.TYPE_BELONG, "01");
		actorRelationService.save(ar);
		return ar;
	}

	private ActorRelation createInstance(Actor master, Actor follower,
			Integer type, String order) {
		ActorRelation ar = new ActorRelation();
		ar.setMaster(master);
		ar.setFollower(follower);
		ar.setType(type);
		ar.setOrder(order);
		return ar;
	}

	protected Actor createActor(int type, String code) {
		Actor actor = new Actor();
		actor.setType(type);
		actor.setInner(false);
		actor.setStatus(Entity.STATUS_ENABLED);
		actor.setUid(UUID.randomUUID().toString());
		actor.setCode(code);
		actor.setName("测试" + code);

		return actor;
	}

	@Test
	public void testFindByMaster() {
		// 保存一个ActorRelation
		ActorRelation ar1 = saveOne();

		// 保存另一个ActorRelation：follower重新创建，其他相同
		Actor follower2 = createActor(Actor.TYPE_DEPARTMENT, "ftest2");
		actorService.save(follower2);
		Assert.assertNotNull(follower2.getId());
		ActorRelation ar2 = createInstance(ar1.getMaster(), follower2,
				ar1.getType(), "02");
		actorRelationService.save(ar2);

		// 执行查询
		List<ActorRelation> ars = actorRelationService.findByMaster(
				ar1.getType(), ar1.getMaster().getId());

		// 验证
		Assert.assertNotNull(ars);
		Assert.assertEquals(2, ars.size());
		Assert.assertEquals(ar1, ars.get(0));
		Assert.assertEquals(ar2, ars.get(1));
	}

	@Test
	public void testFindByFollower() {
		// 保存一个ActorRelation
		ActorRelation ar1 = saveOne();

		// 保存另一个ActorRelation：master重新创建，其他相同
		Actor master2 = createActor(Actor.TYPE_DEPARTMENT, "mtest2");
		actorService.save(master2);
		Assert.assertNotNull(master2.getId());
		ActorRelation ar2 = createInstance(master2, ar1.getFollower(),
				ar1.getType(), "02");
		actorRelationService.save(ar2);

		// 执行查询
		List<ActorRelation> ars = actorRelationService.findByFollower(
				ar1.getType(), ar1.getFollower().getId());

		// 验证
		Assert.assertNotNull(ars);
		Assert.assertEquals(2, ars.size());
		Assert.assertEquals(ar1, ars.get(0));
		Assert.assertEquals(ar2, ars.get(1));
	}
}
