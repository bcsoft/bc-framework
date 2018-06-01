/**
 *
 */
package cn.bc.log.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Set;

/**
 * 操作日志：包括工作日志和审计日志
 * <p>
 * 主要用于记录用户对文档的操作记录
 * </p>
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_LOG_OPERATE")
public class OperateLog extends BaseLog {
  private static final long serialVersionUID = 1L;
  public static final String ATTACH_TYPE = OperateLog.class.getSimpleName();

  /**
   * 创建方式：用户创建
   */
  public static final Integer WAY_USER = 0;
  /**
   * 创建方式：自动生成
   */
  public static final int WAY_SYSTEM = 1;

  /**
   * 日志类型：工作日志
   */
  public static final Integer TYPE_WORK = 0;
  /**
   * 日志类型：审计日志
   */
  public static final Integer TYPE_AUDIT = 1;

  /**
   * 操作分类：新建
   */
  public static final String OPERATE_CREATE = "create";
  /**
   * 操作分类：更新
   */
  public static final String OPERATE_UPDATE = "update";
  /**
   * 操作分类：删除
   */
  public static final String OPERATE_DELETE = "delete";
  /**
   * 操作分类：导出数据
   */
  public static final String OPERATE_EXPORT = "export";

  private String uid;
  private String pid;// 文档标识，通常使用文档的id、uid或批号
  private String ptype;// 所属模块：如User、Role，一般为类名
  private String operate;// 操作分类：如create、update、delete等
  private int way;// 创建方式
  private Set<AuditItem> auditItems;// 审计条目列表

  public int getWay() {
    return way;
  }

  public void setWay(int way) {
    this.way = way;
  }

  @Column(name = "UID_")
  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public String getPtype() {
    return ptype;
  }

  public void setPtype(String ptype) {
    this.ptype = ptype;
  }

  public String getOperate() {
    return operate;
  }

  public void setOperate(String operate) {
    this.operate = operate;
  }

  // @OneToMany(mappedBy = "belong", fetch = FetchType.EAGER, cascade =
  // CascadeType.ALL, orphanRemoval = true)
  // @OrderBy(value = "orderNo asc")
  @Transient
  public Set<AuditItem> getAuditItems() {
    return auditItems;
  }

  public void setAuditItems(Set<AuditItem> auditItems) {
    this.auditItems = auditItems;
  }
}