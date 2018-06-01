/**
 *
 */
package cn.bc.acl.domain;

import cn.bc.core.EntityImpl;
import cn.bc.identity.domain.ActorHistory;

import javax.persistence.*;
import java.util.Calendar;

/**
 * 访问历史
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_ACL_HISTORY")
public class AccessHistory extends EntityImpl {
  private static final long serialVersionUID = 1L;
  private ActorHistory actorHistory;// 访问者
  private Calendar accessDate;// 访问时间
  private String docId;// 文档标识 : 指实际所访问信息的ID
  private String docType;// 文档类型 : 指实际所访问信息的类型
  private String docName;// 文档名称 : 指实际所访问信息的描述
  private String source;//来源 : 如流程实例监控、流程部署监控、部门监控等
  private String url;//链接
  private Long pid;//访问对象ID : 当从ACL控制中得到访问权限时才记录
  private String role;//访问时的权限


  @Column(name = "DOC_ID")
  public String getDocId() {
    return docId;
  }

  public void setDocId(String docId) {
    this.docId = docId;
  }

  @Column(name = "DOC_TYPE")
  public String getDocType() {
    return docType;
  }

  public void setDocType(String docType) {
    this.docType = docType;
  }

  @Column(name = "DOC_NAME")
  public String getDocName() {
    return docName;
  }

  public void setDocName(String docName) {
    this.docName = docName;
  }

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "AHID", referencedColumnName = "ID")
  public ActorHistory getActorHistory() {
    return actorHistory;
  }

  public void setActorHistory(ActorHistory actorHistory) {
    this.actorHistory = actorHistory;
  }

  @Column(name = "ACCESS_DATE")
  public Calendar getAccessDate() {
    return accessDate;
  }

  public void setAccessDate(Calendar accessDate) {
    this.accessDate = accessDate;
  }

  @Column(name = "SRC")
  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Long getPid() {
    return pid;
  }

  public void setPid(Long pid) {
    this.pid = pid;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

}