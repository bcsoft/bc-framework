/**
 *
 */
package cn.bc.log.domain;

import cn.bc.core.EntityImpl;

import javax.persistence.*;

/**
 * 审计日志对应的审计条目
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_LOG_AUDIT_ITEM")
public class AuditItem extends EntityImpl {
  private static final long serialVersionUID = 1L;

  private OperateLog belong;// 所隶属的日志条目
  private String key;// 审计条目:如字段名、属性名
  private String lable;// 审计条目的描述:如字段名、属性名的中文描述
  private String oldValue;// 原值
  private String newValue;// 新值
  private String orderNo;// 同一belong内的排序号

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "PID", referencedColumnName = "ID")
  public OperateLog getBelong() {
    return belong;
  }

  public void setBelong(OperateLog belong) {
    this.belong = belong;
  }

  public String getLable() {
    return lable;
  }

  public void setLable(String lable) {
    this.lable = lable;
  }

  @Column(name = "ORDER_")
  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  @Column(name = "KEY_")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Column(name = "OLD_VALUE")
  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  @Column(name = "NEW_VALUE")
  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }
}