package cn.bc.web.formater;

/**
 * 小图标
 * Created by dragon on 2014/7/3.
 */
public class Icon {
  private String id;
  private String clazz;
  private String click;
  private String title;
  private String clickArguments;
  private String label;
  private String style;
  private String tag = "li";

  public Icon() {
  }

  public Icon(String clazz, String click, String title) {
    this(clazz, click, title, null, null);
  }

  public Icon(String clazz, String click, String title, String id) {
    this(clazz, click, title, id, null);
  }

  public Icon(String clazz, String click, String title, String id, String clickArguments) {
    this.clazz = clazz;
    this.click = click;
    this.title = title;
    this.id = id;
    this.clickArguments = clickArguments;
  }

  public String getId() {
    return id;
  }

  public Icon setId(String id) {
    this.id = id;
    return this;
  }

  public String getClazz() {
    return clazz;
  }

  public Icon setClazz(String clazz) {
    this.clazz = clazz;
    return this;
  }

  public String getClick() {
    return click;
  }

  public Icon setClick(String click) {
    this.click = click;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public Icon setTitle(String title) {
    this.title = title;
    return this;
  }

  public String getClickArguments() {
    return clickArguments;
  }

  public Icon setClickArguments(String clickArguments) {
    this.clickArguments = clickArguments;
    return this;
  }

  public String getLabel() {
    return label;
  }

  public Icon setLabel(String label) {
    this.label = label;
    return this;
  }

  public String getStyle() {
    return style;
  }

  public Icon setStyle(String style) {
    this.style = style;
    return this;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  /**
   * 构建图标的Html
   *
   * @return
   */
  public String toString() {
    StringBuffer c = new StringBuffer();
    c.append("<" + this.getTag() + " class=\"inputIcon");

    // css class
    String clazz = this.getClazz();
    if (clazz != null) c.append(" " + clazz);
    c.append("\"");

    // id
    String id = this.getId();
    if (id != null) c.append(" id=\"" + id + "\"");

    // click
    String click = this.getClick();
    if (click != null) c.append(" data-click=\"" + click + "\"");

    // click-args
    String clickArgs = this.getClickArguments();
    if (clickArgs != null) c.append(" data-click-args='" + clickArgs + "'");

    // title
    String title = this.getTitle();
    if (title != null) c.append(" title=\"" + title + "\"");

    // style
    String style = this.getStyle();
    if (style != null) c.append(" style=\"" + style + "\"");

    c.append(">");

    // label
    String label = this.getLabel();
    if (label != null) c.append(label);

    c.append("</" + this.getTag() + ">");

    return c.toString();
  }

  /**
   * 生成图标被默认容器包裹后的HTML代码
   *
   * @return
   */
  public String wrap() {
    return "<ul class=\"inputIcons incell\">" + this.toString() + "</ul>";
  }
}
