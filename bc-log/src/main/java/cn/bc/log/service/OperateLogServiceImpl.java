package cn.bc.log.service;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.log.dao.OperateLogDao;
import cn.bc.log.domain.AuditItem;
import cn.bc.log.domain.OperateLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * 操作日志service接口的实现
 *
 * @author dragon
 */
public class OperateLogServiceImpl extends DefaultCrudService<OperateLog>
  implements OperateLogService {
  private OperateLogDao worklogDao;
  private IdGeneratorService idGeneratorService;

  @Autowired
  public void setWorklogDao(OperateLogDao worklogDao) {
    this.worklogDao = worklogDao;
    this.setCrudDao(worklogDao);
  }

  @Autowired
  public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
    this.idGeneratorService = idGeneratorService;
  }

  public List<OperateLog> find(String ptype, String pid) {
    return this.worklogDao.find(ptype, pid);
  }

  public OperateLog saveWorkLog(String ptype, String pid, String subject,
                                String content, String operate) {
    return this.saveWorkLog(ptype, pid, subject, content, operate,
      OperateLog.WAY_SYSTEM);
  }

  public OperateLog saveWorkLog(String ptype, String pid, String subject,
                                String content, String operate, int way) {
    Assert.hasText(ptype, "ptype 不能为空！");
    Assert.hasText(pid, "pid 不能为空！");
    Assert.hasText(subject, "subject 不能为空！");

    OperateLog worklog = new OperateLog();
    worklog.setType(OperateLog.TYPE_WORK);// 工作日志
    worklog.setWay(way);
    worklog.setFileDate(Calendar.getInstance());
    worklog.setAuthor(SystemContextHolder.get().getUserHistory());
    worklog.setPtype(ptype);
    worklog.setPid(pid);
    worklog.setSubject(subject);
    worklog.setContent(content);
    worklog.setOperate(operate);
    worklog.setUid(this.idGeneratorService.next("WorkLog"));

    return this.save(worklog);
  }

  public OperateLog saveAuditLog(String ptype, String pid,
                                 Set<AuditItem> auditItems, String subject, String content,
                                 String operate) {
    Assert.hasText(ptype, "ptype 不能为空！");
    Assert.hasText(pid, "pid 不能为空！");
    Assert.notEmpty(auditItems, "auditItems 不能为空！");
    Assert.hasText(subject, "subject 不能为空！");

    OperateLog auditLog = new OperateLog();
    auditLog.setType(OperateLog.TYPE_AUDIT);// 审计日志
    auditLog.setWay(OperateLog.WAY_SYSTEM);// 自动生成
    auditLog.setFileDate(Calendar.getInstance());
    auditLog.setAuthor(SystemContextHolder.get().getUserHistory());
    auditLog.setPtype(ptype);
    auditLog.setPid(pid);
    auditLog.setSubject(subject);
    auditLog.setContent(content);
    auditLog.setOperate(operate);
    auditLog.setUid(this.idGeneratorService.next("AuditLog"));

    for (AuditItem auditItem : auditItems) {
      auditItem.setBelong(auditLog);
    }
    auditLog.setAuditItems(auditItems);

    return this.save(auditLog);
  }
}
