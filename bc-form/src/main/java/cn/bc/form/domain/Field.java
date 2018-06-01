package cn.bc.form.domain;

import cn.bc.core.EntityImpl;
import cn.bc.core.util.JsonUtils;
import cn.bc.identity.domain.ActorHistory;

import javax.persistence.*;
import java.util.Calendar;

/**
 * 表单字段
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_FORM_FIELD")
public class Field extends EntityImpl {
  private static final long serialVersionUID = 1L;
  private Form form;// 所属表单
  private String name;// 字段名成
  private String label;// 标签
  private String type;// 值类型
  private String value;// 值
  private Calendar updateTime;// 更新时间
  private ActorHistory updator;// 更新人

  @JoinColumn(name = "PID", referencedColumnName = "ID")
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  public Form getForm() {
    return form;
  }

  public void setForm(Form form) {
    this.form = form;
  }

  @Column(name = "NAME_")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "LABEL_")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Column(name = "TYPE_")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Column(name = "VALUE_")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Column(name = "UPDATE_TIME")
  public Calendar getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Calendar updateTime) {
    this.updateTime = updateTime;
  }

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "UPDATOR", referencedColumnName = "ID")
  public ActorHistory getUpdator() {
    return updator;
  }

  public void setUpdator(ActorHistory updator) {
    this.updator = updator;
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}