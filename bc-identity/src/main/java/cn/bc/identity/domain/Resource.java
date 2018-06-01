/**
 *
 */
package cn.bc.identity.domain;

import cn.bc.core.RichEntityImpl;

import javax.persistence.*;

/**
 * 资源
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_IDENTITY_RESOURCE")
public class Resource extends RichEntityImpl {
  private static final long serialVersionUID = 4623393538293916992L;

  /**
   * 资源类型为分类文件夹
   */
  public static final int TYPE_FOLDER = 1;
  /**
   * 资源类型为内部链接
   */
  public static final int TYPE_INNER_LINK = 2;
  /**
   * 资源类型为外部链接
   */
  public static final int TYPE_OUTER_LINK = 3;
  /**
   * 资源类型为HTML
   */
  public static final int TYPE_HTML = 4;

  private String name;// 名称
  private String orderNo;// 排序号
  private int type;// 资源类型，TYPE_*定义的相关常数
  private String url;// 资源地址
  private Resource belong;// 所隶属的模块
  private String iconClass;// 图标样式
  private String option;// 额外配置
  private boolean inner = false;// 是否为内置对象，内置对象不允许删除
  private String pname;// 所隶属模块的全名:如'系统维护/组织架构/单位配置'

  @Transient
  public static Integer[] getAllTypes() {
    Integer[] types = new Integer[4];
    types[0] = TYPE_FOLDER;
    types[1] = TYPE_INNER_LINK;
    types[2] = TYPE_OUTER_LINK;
    types[3] = TYPE_HTML;
    return types;
  }

  public String getPname() {
    return pname;
  }

  public void setPname(String pname) {
    this.pname = pname;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "OPTION_")
  public String getOption() {
    return option;
  }

  public void setOption(String option) {
    this.option = option;
  }

  @Column(name = "ORDER_")
  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String order) {
    this.orderNo = order;
  }

  @Column(name = "TYPE_")
  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @ManyToOne(fetch = FetchType.EAGER, optional = true)
  @JoinColumn(name = "BELONG", referencedColumnName = "ID")
  public Resource getBelong() {
    return belong;
  }

  public void setBelong(Resource belong) {
    this.belong = belong;
  }

  public String toString() {
    return "{id:" + getId() + ",type:" + type + ",code:" + orderNo
      + ",name:" + name + ",url:" + url + "}";
  }

  /**
   * 全路径分隔符
   */
  public static final String PS = "/";

  @Transient
  public String getFullName() {
    if (this.getPname() != null && this.getPname().length() > 0) {
      return this.getPname() + PS + this.getName();
    } else {
      return this.getName();
    }
  }
}
