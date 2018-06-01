package cn.bc.identity.dto;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 用户所属部门的简易信息 Dto
 *
 * @author cjw
 */
@Entity
public class DepartmentByActorDto4MiniInfo implements Serializable {
  /**
   * 用户历史 Id
   */
  @Id
  public Long actorHistoryId;
  /**
   * 部门 Id
   */
  public Long departmentId;
  /**
   * 用户编号
   */
  public String departmentCode;
  /**
   * 部门名称
   */
  public String departmentName;

  public DepartmentByActorDto4MiniInfo(Long actorHistoryId, Long departmentId, String departmentCode, String departmentName) {
    this.actorHistoryId = actorHistoryId;
    this.departmentId = departmentId;
    this.departmentCode = departmentCode;
    this.departmentName = departmentName;
  }

  public Long getActorHistoryId() {
    return actorHistoryId;
  }

  public void setActorHistoryId(Long actorHistoryId) {
    this.actorHistoryId = actorHistoryId;
  }

  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
  }

  public String getDepartmentCode() {
    return departmentCode;
  }

  public void setDepartmentCode(String departmentCode) {
    this.departmentCode = departmentCode;
  }

  public String getDepartmentName() {
    return departmentName;
  }

  public void setDepartmentName(String departmentName) {
    this.departmentName = departmentName;
  }
}
