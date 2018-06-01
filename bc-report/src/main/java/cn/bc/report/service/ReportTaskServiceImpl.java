package cn.bc.report.service;

import cn.bc.BCConstants;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.service.ActorHistoryService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.log.domain.OperateLog;
import cn.bc.log.service.OperateLogService;
import cn.bc.report.dao.ReportTaskDao;
import cn.bc.report.domain.ReportTask;
import cn.bc.report.scheduler.RunReportTemplateJobBean;
import cn.bc.scheduler.service.SchedulerManage;
import cn.bc.scheduler.service.SchedulerService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

/**
 * 报表任务Service接口的实现
 *
 * @author lbj
 * @author dragon 2016-09-20 add beanResolver
 */
public class ReportTaskServiceImpl extends DefaultCrudService<ReportTask> implements ReportTaskService, InitializingBean, BeanFactoryAware {
  private static Logger logger = LoggerFactory.getLogger(ReportTaskServiceImpl.class);
  private OperateLogService operateLogService;
  private ReportTaskDao reportTaskDao;
  private Scheduler scheduler;
  private SchedulerService schedulerService;
  private ActorHistoryService actorHistoryService;
  private ReportService reportService;
  private IdGeneratorService idGeneratorService;
  @Autowired
  private SchedulerManage schedulerManage;
  private BeanResolver beanResolver;

  @Autowired
  public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
    this.idGeneratorService = idGeneratorService;
  }

  @Autowired
  public void setReportService(ReportService reportService) {
    this.reportService = reportService;
  }

  @Autowired
  public void setScheduler(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Autowired
  public void setActorHistoryService(ActorHistoryService actorHistoryService) {
    this.actorHistoryService = actorHistoryService;
  }

  @Autowired
  public void setSchedulerService(SchedulerService schedulerService) {
    this.schedulerService = schedulerService;
  }

  @Autowired
  public void setOperateLogService(OperateLogService operateLogService) {
    this.operateLogService = operateLogService;
  }

  @Autowired
  public void setReportTaskDao(ReportTaskDao reportTaskDao) {
    this.setCrudDao(reportTaskDao);
    this.reportTaskDao = reportTaskDao;
  }

  @Autowired
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanResolver = new BeanFactoryResolver(beanFactory);
  }

  public void afterPropertiesSet() throws Exception {
    // 禁用就不再处理
    if (schedulerManage.isDisabled()) {
      logger.warn("ReportTask was config to disabled");
      return;
    }

    // 立即计划所有可用的调度任务
    List<ReportTask> tasks = this.reportTaskDao.findAllEnabled();
    logger.warn("scheduling {} reportTasks", tasks.size());
    for (ReportTask task : tasks) {
      this.doStart(task.getId());
    }
  }

  public Date doStart(Long taskId) throws Exception {
    ReportTask reportTask = this.reportTaskDao.load(taskId);
    if (reportTask == null) {
      logger.warn("ignore unknown reportTaskId: {}", taskId);
      return null;
    }
    logger.warn("scheduling reportTask:{}/{}", reportTask.getTemplate().getCategory(), reportTask.getName());

    Date nextDate;
    String triggerName = "REPORT_TASK_TRIGGER" + reportTask.getId();
    // 检测是否已经调度
    CronTrigger trigger = (CronTrigger) this.scheduler.getTrigger(
      triggerKey(triggerName, ReportTask.GROUP_NAME));
    if (null == trigger) {// Trigger不存在，就创建一个新的
      // 记录状态数据：要注入到RunReportTemplateJobBean的属性
      JobDataMap jobDataMap = new JobDataMap();
      jobDataMap.put("schedulerService", this.schedulerService);
      jobDataMap.put("reportService", this.reportService);
      jobDataMap.put("beanResolver", this.beanResolver);
      jobDataMap.put("executor", this.actorHistoryService.loadByCode("admin"));
      jobDataMap.put("reportTask", reportTask);
      JobDetail jobDetail = newJob(RunReportTemplateJobBean.class)
        .withIdentity("REPORT_TASK_JOB" + reportTask.getId(), ReportTask.GROUP_NAME)
        .usingJobData(jobDataMap)
        .build();

      trigger = newTrigger()
        .withIdentity(triggerName, ReportTask.GROUP_NAME)
        .withSchedule(cronSchedule(reportTask.getCron()))
        .build();
      nextDate = this.scheduler.scheduleJob(jobDetail, trigger);

      // 记录操作日志
      OperateLog worklog = new OperateLog();
      worklog.setType(OperateLog.TYPE_WORK);// 工作日志
      worklog.setWay(OperateLog.WAY_SYSTEM);
      worklog.setFileDate(Calendar.getInstance());
      worklog.setAuthor(this.actorHistoryService.loadByCode("admin"));// 超级管理员作为执行者
      worklog.setPtype(ReportTask.class.getSimpleName());
      worklog.setPid(String.valueOf(reportTask.getId()));
      worklog.setSubject("启动报表任务：" + reportTask.getName());
      worklog.setContent(null);
      worklog.setOperate(OperateLog.OPERATE_UPDATE);
      worklog.setUid(this.idGeneratorService.next("WorkLog"));
      this.operateLogService.save(worklog);
    } else {// Trigger已存在，更新相应的调度设置
      //trigger.setCronExpression(reportTask.getCron());
      TriggerKey key = trigger.getKey();
      trigger = newTrigger()
        .withIdentity(key)
        .withSchedule(cronSchedule(reportTask.getCron()))
        .build();
      nextDate = this.scheduler.rescheduleJob(key, trigger);

      // 记录操作日志
      this.operateLogService.saveWorkLog(ReportTask.class.getSimpleName(), String.valueOf(reportTask.getId()),
        "重置报表任务：" + reportTask.getName(), null, OperateLog.OPERATE_UPDATE);
    }

    // 将任务的状态设置为正常
    if (reportTask.getStatus() != BCConstants.STATUS_ENABLED) {
      SystemContext context = SystemContextHolder.get();
      reportTask.setStatus(BCConstants.STATUS_ENABLED);
      reportTask.setModifiedDate(Calendar.getInstance());
      reportTask.setModifier(context.getUserHistory());
      this.reportTaskDao.save(reportTask);
    }

    return nextDate;
  }

  public void doStop(Long taskId) throws Exception {
    ReportTask reportTask = this.reportTaskDao.load(taskId);
    if (reportTask == null) {
      logger.warn("ignore unknown reportTaskId: {}", taskId);
      return;
    }

    // 删除调度
    String triggerName = "REPORT_TASK_TRIGGER" + reportTask.getId();
    CronTrigger trigger = (CronTrigger) this.scheduler.getTrigger(triggerKey(triggerName, ReportTask.GROUP_NAME));
    if (null != trigger) {
      this.scheduler.deleteJob(jobKey("REPORT_TASK_JOB" + reportTask.getId(), ReportTask.GROUP_NAME));

      // 记录操作日志
      this.operateLogService.saveWorkLog(ReportTask.class.getSimpleName(), String.valueOf(reportTask.getId()),
        "停止报表任务：" + reportTask.getName(), null, OperateLog.OPERATE_UPDATE);
    }

    // 将任务的状态设置为禁用
    if (reportTask.getStatus() != BCConstants.STATUS_DISABLED) {
      SystemContext context = SystemContextHolder.get();
      reportTask.setStatus(BCConstants.STATUS_DISABLED);
      reportTask.setModifiedDate(Calendar.getInstance());
      reportTask.setModifier(context.getUserHistory());
      this.reportTaskDao.save(reportTask);
    }
  }
}