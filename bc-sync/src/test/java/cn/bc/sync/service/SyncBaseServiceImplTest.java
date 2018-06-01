package cn.bc.sync.service;

import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.service.ActorHistoryService;
import cn.bc.sync.domain.SyncBase;
import cn.bc.test.AbstractEntityCrudTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class SyncBaseServiceImplTest extends
  AbstractEntityCrudTest<Long, SyncBase> {
  SyncBaseService syncBaseService;
  ActorHistoryService actorHistoryService;

  @Autowired
  public void setSyncBaseService(SyncBaseService syncBaseService) {
    this.syncBaseService = syncBaseService;
    this.crudOperations = syncBaseService;// 赋值基类的crud操作对象
  }

  @Autowired
  public void setActorHistoryService(ActorHistoryService actorHistoryService) {
    this.actorHistoryService = actorHistoryService;
  }

  @Override
  protected SyncBase createInstance(String config) {
    SyncBase syncBase = new SyncBase();
    syncBase.setAuthor(getAdminHistory());
    syncBase.setSyncDate(Calendar.getInstance());
    syncBase.setSyncType("syncType");
    syncBase.setSyncCode(UUID.randomUUID().toString());
    syncBase.setSyncFrom("syncFrom");
    syncBase.setStatus(SyncBase.STATUS_NEW);
    return syncBase;
  }

  private ActorHistory getAdminHistory() {
    return this.actorHistoryService.loadByCode("admin");
  }

  @Test
  public void testLoadBySyncTypeAndId() {
    SyncBase syncBase = createInstance(null);
    String syncId = UUID.randomUUID().toString();
    syncBase.setSyncCode(syncId);
    Assert.assertNull(syncBase.getId());
    this.syncBaseService.save(syncBase);
    Assert.assertNotNull(syncBase.getId());

    SyncBase syncBase2 = this.syncBaseService.load("syncType", syncId);
    Assert.assertNotNull(syncBase2);
    Assert.assertEquals(syncBase.getId(), syncBase2.getId());
  }

  @Test
  public void testHadSync() {
    Assert.assertFalse(this.syncBaseService.hadSync("syncType", UUID.randomUUID().toString()));

    SyncBase syncBase = createInstance(null);
    String syncId = UUID.randomUUID().toString();
    syncBase.setSyncCode(syncId);
    Assert.assertNull(syncBase.getId());
    this.syncBaseService.save(syncBase);
    Assert.assertNotNull(syncBase.getId());
    Assert.assertTrue(this.syncBaseService.hadSync("syncType", syncId));
  }
}
