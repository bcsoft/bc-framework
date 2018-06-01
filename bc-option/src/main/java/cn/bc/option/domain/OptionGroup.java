/**
 *
 */
package cn.bc.option.domain;

import cn.bc.core.EntityImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 选项分组
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_OPTION_GROUP")
public class OptionGroup extends EntityImpl {
  private static final long serialVersionUID = 1L;

  private String value; // 值
  private String key; // 键
  private String orderNo; // 排序号
  private String icon; // 图标样式

  @Column(name = "ORDER_")
  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  @Column(name = "VALUE_")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Column(name = "KEY_")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }
}