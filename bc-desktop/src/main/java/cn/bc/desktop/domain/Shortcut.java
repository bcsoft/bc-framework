/**
 *
 */
package cn.bc.desktop.domain;

import cn.bc.core.RichEntityImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 桌面快捷方式
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_DESKTOP_SHORTCUT")
public class Shortcut extends RichEntityImpl {
  private static final long serialVersionUID = 1L;

  private boolean standalone;// 是否在独立的浏览器窗口中打开
  private String order;// 排序号
  private String name;// 名称,为空则使用模块的名称
  private String url;// 地址,为空则使用模块的地址
  private Long resourceId;// 对应的资源Id
  private Long actorId;// 所属的参与者Id(如果为上级参与者,如单位部门,则其下的所有参与者都拥有该快捷方式)
  private String iconClass;// 图标样式
  private boolean inner = false;// 是否为内置对象，内置对象不允许删除
  private String cfg;// 扩展配置

  @Column(name = "CFG")
  public String getCfg() {
    return cfg;
  }

  public void setCfg(String cfg) {
    this.cfg = cfg;
  }

  @Column(name = "INNER_")
  public boolean isInner() {
    return inner;
  }

  public void setInner(boolean inner) {
    this.inner = inner;
  }

  public String getIconClass() {
    return iconClass;
  }

  public void setIconClass(String iconClass) {
    this.iconClass = iconClass;
  }

  public boolean isStandalone() {
    return standalone;
  }

  public void setStandalone(boolean standalone) {
    this.standalone = standalone;
  }

  @Column(name = "ORDER_")
  public String getOrder() {
    return order;
  }

  public void setOrder(String order) {
    this.order = order;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Column(name = "SID")
  public Long getResourceId() {
    return resourceId;
  }

  public void setResourceId(Long resourceId) {
    this.resourceId = resourceId;
  }

  @Column(name = "AID")
  public Long getActorId() {
    return actorId;
  }

  public void setActorId(Long actorId) {
    this.actorId = actorId;
  }
}
