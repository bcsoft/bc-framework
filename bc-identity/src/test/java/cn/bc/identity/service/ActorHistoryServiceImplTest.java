package cn.bc.identity.service;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.test.AbstractEntityCrudTest;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class ActorHistoryServiceImplTest extends
		AbstractEntityCrudTest<Long, ActorHistory> {
	ActorService actorService;
	ActorHistoryService actorHistoryService;

	@Autowired
	public void setActorHistoryService(ActorHistoryService actorHistoryService) {
		this.actorHistoryService = actorHistoryService;
		this.crudOperations = actorHistoryService;// 赋值基类的crud操作对象
	}

	@Autowired
	public void setActorService(
			@Qualifier("actorService") ActorService actorService) {
		this.actorService = actorService;
	}

	@Override
	protected ActorHistory createInstance(String config) {
		ActorHistory actorHistory = new ActorHistory();

		// 补充一些必填域的设置
		actorHistory.setCreateDate(Calendar.getInstance());
		actorHistory.setActorType(Actor.TYPE_USER);
		actorHistory.setActorId(new Long(1));
		actorHistory.setName("actorName");

		return actorHistory;
	}

	@Test
	public void testLoadCurrent() {
		String actorCode = "admin";
		Actor user = this.actorService.loadByCode(actorCode);
		Assert.assertNotNull(user);

		ActorHistory actorHistory = actorHistoryService.loadCurrent(user.getId());
		Assert.assertNotNull(actorHistory);
		Assert.assertEquals(user.getId(), actorHistory.getActorId());
	}
}
