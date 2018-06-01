/**
 *
 */
package cn.bc.subscribe.domain;

import cn.bc.identity.domain.FileEntityImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 订阅
 *
 * @author lbj
 */
@Entity
@Table(name = "BC_SUBSCRIBE")
public class Subscribe extends FileEntityImpl {
  private static final long serialVersionUID = 1L;

  /**
   * 邮件
   **/
  public static final int TYPE_EMAIL = 0;
  /**
   * 短信
   **/
  public static final int TYPE_SMS = 1;

  private int status;//状态 : 0-草稿,1-已发布,2-禁用
  private int type;// 类型 : 0-邮件,1-短信' 全局默认的类型
  private String orderNo;// 排序号
  private String subject;// 主题
  private String eventCode;// 事件编码

  @Column(name = "STATUS_")
  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  @Column(name = "TYPE_")
  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  @Column(name = "ORDER_")
  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  @Column(name = "EVENT_CODE")
  public String getEventCode() {
    return eventCode;
  }

  public void setEventCode(String eventCode) {
    this.eventCode = eventCode;
  }


}