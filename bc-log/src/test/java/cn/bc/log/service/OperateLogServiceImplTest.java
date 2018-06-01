package cn.bc.log.service;

import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.service.ActorHistoryService;
import cn.bc.log.domain.OperateLog;
import cn.bc.test.AbstractEntityCrudTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class OperateLogServiceImplTest extends
  AbstractEntityCrudTest<Long, OperateLog> {
  private ActorHistoryService actorHistoryService;
  OperateLogService worklogService;

  @Autowired
  public void setWorklogService(OperateLogService worklogService) {
    this.worklogService = worklogService;
    this.crudOperations = worklogService;// 赋值基类的crud操作对象
  }

  @Autowired
  public void setActorHistoryService(ActorHistoryService actorHistoryService) {
    this.actorHistoryService = actorHistoryService;
  }

  @Override
  protected OperateLog createInstance(String config) {
    OperateLog entity = super.createInstance(config);
    entity.setUid("uid");

    // 补充一些额外的设置
    entity.setType(OperateLog.TYPE_WORK);
    entity.setWay(OperateLog.WAY_USER);
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
    OperateLog worklog = this.createInstance(getDefaultConfig());
    worklog.setPtype("ptype");
    worklog.setPid(UUID.randomUUID().toString());
    Assert.assertNull(worklog.getId());
    worklogService.save(worklog);
    Assert.assertNotNull(worklog.getId());

    // 从数据库加载
    List<OperateLog> worklogs = this.worklogService.find(
      worklog.getPtype(), worklog.getPid());

    Assert.assertNotNull(worklogs);
    Assert.assertEquals(1, worklogs.size());
    Assert.assertEquals(worklog.getId(), worklogs.get(0).getId());
  }
}
