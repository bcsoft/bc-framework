/**
 *
 */
package cn.bc.message.domain;

import cn.bc.core.RichEntityImpl;
import cn.bc.identity.domain.ActorHistory;

import javax.persistence.*;
import java.util.Calendar;

/**
 * 消息
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_MESSAGE")
public class Message extends RichEntityImpl {
  private static final long serialVersionUID = 1L;

  private String subject;// 标题
  private String content;// 内容
  private Integer type;// 类型
  private Calendar sendDate;// 发送时间
  private ActorHistory sender;// 发送人
  private ActorHistory receiver;// 接收人
  private boolean read; // 阅读标志
  private Long fromId; // 来源标识
  private Integer fromType; // 来源类型

  @Column(name = "SEND_DATE")
  public Calendar getSendDate() {
    return sendDate;
  }

  public void setSendDate(Calendar sendDate) {
    this.sendDate = sendDate;
  }

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "SENDER_ID", referencedColumnName = "ID")
  public ActorHistory getSender() {
    return sender;
  }

  public void setSender(ActorHistory sender) {
    this.sender = sender;
  }

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "RECEIVER_ID", referencedColumnName = "ID")
  public ActorHistory getReceiver() {
    return receiver;
  }

  public void setReceiver(ActorHistory receiver) {
    this.receiver = receiver;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Column(name = "TYPE_")
  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  @Column(name = "READ_")
  public boolean isRead() {
    return read;
  }

  public void setRead(boolean read) {
    this.read = read;
  }

  @Column(name = "FROM_ID")
  public Long getFromId() {
    return fromId;
  }

  public void setFromId(Long fromId) {
    this.fromId = fromId;
  }

  @Column(name = "FROM_TYPE")
  public Integer getFromType() {
    return fromType;
  }

  public void setFromType(Integer fromType) {
    this.fromType = fromType;
  }
}
