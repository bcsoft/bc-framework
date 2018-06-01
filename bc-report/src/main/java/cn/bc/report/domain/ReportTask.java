/**
 *
 */
package cn.bc.report.domain;

import cn.bc.identity.domain.FileEntityImpl;

import javax.persistence.*;
import java.util.Calendar;

/**
 * 报表任务
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_REPORT_TASK")
public class ReportTask extends FileEntityImpl {
  private static final long serialVersionUID = 1L;
  public static final String GROUP_NAME = "REPORT_TASK";

  private int status;// 状态：0-已启用2,1-已禁用
  private String orderNo;// 排序号
  private String name;// 任务名称
  private String cron;// 定时表达式，按标准的cron表达式，参考http://rongjih.blog.163.com/blog/static/33574461201032011858793/
  private String desc;// 备注
  private String config;// 详细配置
  private ReportTemplate template;// 所用模板

  private Calendar startDate;// 开始时间
  private boolean ignoreError = false; // 发现异常是否忽略后继续调度

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "PID", referencedColumnName = "ID")
  public ReportTemplate getTemplate() {
    return template;
  }

  public void setTemplate(ReportTemplate template) {
    this.template = template;
  }

  @Column(name = "STATUS_")
  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  @Column(name = "ORDER_")
  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String order) {
    this.orderNo = order;
  }

  @Column(name = "DESC_")
  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCron() {
    return cron;
  }

  public void setCron(String cron) {
    this.cron = cron;
  }

  public String getConfig() {
    return config;
  }

  public void setConfig(String config) {
    this.config = config;
  }

  @Column(name = "START_DATE")
  public Calendar getStartDate() {
    return startDate;
  }

  public void setStartDate(Calendar startDate) {
    this.startDate = startDate;
  }

  @Column(name = "IGNORE_ERROR")
  public boolean isIgnoreError() {
    return ignoreError;
  }

  public void setIgnoreError(boolean stopInError) {
    this.ignoreError = stopInError;
  }
}
