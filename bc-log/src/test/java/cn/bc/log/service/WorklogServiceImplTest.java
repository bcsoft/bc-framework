package cn.bc.log.service;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.service.ActorHistoryService;
import cn.bc.log.domain.Worklog;
import cn.bc.test.AbstractEntityCrudTest;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class WorklogServiceImplTest extends
		AbstractEntityCrudTest<Long, Worklog> {
	private ActorHistoryService actorHistoryService;
	WorklogService worklogService;

	@Autowired
	public void setWorklogService(WorklogService worklogService) {
		this.worklogService = worklogService;
		this.crudOperations = worklogService;// 赋值基类的crud操作对象
	}

	@Autowired
	public void setActorHistoryService(ActorHistoryService actorHistoryService) {
		this.actorHistoryService = actorHistoryService;
	}

	@Override
	protected Worklog createInstance(String config) {
		Worklog entity = super.createInstance(config);
		entity.setUid("uid");

		// 补充一些额外的设置
		entity.setType(Worklog.TYPE_SYSTEM);
		entity.setPtype("ptype");
		entity.setPid("pid");
		entity.setSubject("subject");
		entity.setAuthor(this.getAdminHistory());
		entity.setFileDate(Calendar.getInstance());

		return entity;
	}

	protected ActorHistory getAdminHistory() {
		return this.getUserHistory("admin");
	}

	protected ActorHistory getUserHistory(String code) {
		return this.actorHistoryService.loadByCode(code);
	}

	@Test
	public void testFindWithParent() {
		// 先插入一条数据
		Worklog worklog = this.createInstance(getDefaultConfig());
		worklog.setPtype("ptype");
		worklog.setPid(UUID.randomUUID().toString());
		Assert.assertNull(worklog.getId());
		worklogService.save(worklog);
		Assert.assertNotNull(worklog.getId());

		// 从数据库加载
		List<Worklog> worklogs = this.worklogService.find(worklog.getPtype(),
				worklog.getPid());

		Assert.assertNotNull(worklogs);
		Assert.assertEquals(1, worklogs.size());
		Assert.assertEquals(worklog.getId(), worklogs.get(0).getId());
	}
}
