package cn.bc.scheduler.domain;

import cn.bc.core.EntityImpl;
import org.json.JSONObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Calendar;

/**
 * 调度任务配置
 *
 * @author dragon
 * @since 2011-08-29
 */
@Entity
@Table(name = "BC_SD_JOB")
public class ScheduleJob extends EntityImpl {
  private static final long serialVersionUID = 1L;
  /**
   * 状态：正在运行
   */
  public static final int STATUS_RUNNING = 3;
  /**
   * 状态：暂停
   */
  public static final int STATUS_PAUSING = 4;

  private int status; // 状态：参考RichEntity以STATUS_开头的常数
  private String name; // 任务名称
  private String groupn; // 任务分组名
  private String bean; // 要调用的SpringBean名
  private String method; // 要调用的SpringBean方法名
  private String memo; // 备注
  private String cron; // 时间表达式
  private String orderNo; // 排序号
  private Calendar nextDate; // 任务的下一运行时间
  private boolean ignoreError = false; // 发现异常是否忽略后继续调度

  @Column(name = "NEXT_DATE")
  public Calendar getNextDate() {
    return nextDate;
  }

  public void setNextDate(Calendar nextDate) {
    this.nextDate = nextDate;
  }

  @Column(name = "GROUPN")
  public String getGroupn() {
    return groupn;
  }

  public void setGroupn(String groupn) {
    this.groupn = groupn;
  }

  @Column(name = "CRON")
  public String getCron() {
    return cron;
  }

  public void setCron(String cron) {
    this.cron = cron;
  }

  @Column(name = "ORDER_")
  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  @Column(name = "STATUS_")
  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  @Column(name = "IGNORE_ERROR")
  public boolean isIgnoreError() {
    return ignoreError;
  }

  public void setIgnoreError(boolean stopInError) {
    this.ignoreError = stopInError;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "BEAN")
  public String getBean() {
    return bean;
  }

  public void setBean(String bean) {
    this.bean = bean;
  }

  @Column(name = "METHOD")
  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  @Column(name = "MEMO_")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memos) {
    this.memo = memos;
  }

  @Transient
  public String getTriggerName() {
    return this.getName() + "_Trigger";
  }

  public String toString() {
    return new JSONObject(this).toString();
  }
}