package cn.bc.web.formater;

import java.util.ArrayList;
import java.util.List;

/**
 * 小图标集
 * Created by dragon on 2014/7/15.
 */
public class Icons {
  private List<Icon> icons;
  private String tag = "ul";
  private String clazz = "inputIcons incell";

  public Icons() {
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getClazz() {
    return clazz;
  }

  public void setClazz(String clazz) {
    this.clazz = clazz;
  }

  private List<Icon> getIcons() {
    if (icons == null)
      icons = new ArrayList<Icon>();
    return icons;
  }

  public Icons add(Icon icon) {
    getIcons().add(icon);
    return this;
  }

  /**
   * 构建图标集的Html
   *
   * @return
   */
  public String toString() {
    StringBuffer c = new StringBuffer();
    c.append("<" + this.getTag());

    // css class
    String clazz = this.getClazz();
    if (clazz != null) c.append(" class=\"" + clazz + "\"");
    c.append(">");

    for (Icon icon : this.getIcons()) {
      c.append(icon.toString());
    }

    c.append("</" + this.getTag() + ">");

    return c.toString();
  }
}