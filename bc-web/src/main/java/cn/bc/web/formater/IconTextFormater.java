/**
 *
 */
package cn.bc.web.formater;

import java.util.ArrayList;
import java.util.List;

/**
 * 带图标、文字的格式化
 *
 * @author dragon
 */
public class IconTextFormater extends AbstractFormater<Object> implements ExportText {
  private List<Icon> iconsBeforeText;
  private List<Icon> iconsAfterText;

  public IconTextFormater() {
  }

  @Override
  public Object format(Object context, Object value) {
    StringBuffer v = new StringBuffer();

    // 前置图标
    appendIcons(this.getIconsBeforeText(context, value), v);

    // 显示的文本
    Object _value = this.formatValue(context, value);
    if (_value != null)
      v.append(_value);

    // 后置图标
    appendIcons(this.getIconsAfterText(context, value), v);

    if (v.length() > 0) {
      return v.toString();
    } else {
      return "&nbsp;";
    }
  }

  protected Object formatValue(Object context, Object value) {
    return value;
  }

  public String getExportText(Object context, Object value) {
    return (String) formatValue(context, value);
  }

  // 生成图标的 HTML
  private void appendIcons(List<Icon> icons, StringBuffer c) {
    if (icons == null || icons.isEmpty())
      return;

    c.append("<ul class=\"inputIcons incell\">");
    for (Icon i : icons) {
      c.append(buildInputIconHtml("li", i.getId(), i.getClazz(), i.getTitle()
        , null, i.getClick(), i.getClickArguments(), null));
    }
    c.append("</ul>");
  }

  /**
   * 构建 inputIcon 小图标的Html
   *
   * @param tag       使用的标签
   * @param id
   * @param clazz     类名
   * @param title     鼠标提示信息
   * @param label     显示的文字
   * @param click     点击函数
   * @param clickArgs 点击函数预设的参数
   * @param style     css样式设置
   * @return
   */
  public static String buildInputIconHtml(String tag, String id, String clazz
    , String title, String label, String click, String clickArgs, String style) {
    StringBuffer c = new StringBuffer();
    c.append("<" + tag + " class=\"inputIcon");

    // css class
    if (clazz != null) c.append(" " + clazz);
    c.append("\"");

    // id
    if (id != null) c.append(" id=\"" + id + "\"");

    // click
    if (click != null) c.append(" data-click=\"" + click + "\"");

    // click-args
    if (clickArgs != null) c.append(" data-click-args='" + clickArgs + "'");

    // title
    if (title != null) c.append(" title=\"" + title + "\"");

    // style
    if (style != null) c.append(" style=\"" + style + "\"");

    c.append(">");

    // label
    if (label != null) c.append(label);

    c.append("</" + tag + ">");

    return c.toString();
  }

  public List<Icon> getIconsBeforeText(Object context, Object value) {
    return getIconsBeforeText();
  }

  public List<Icon> getIconsBeforeText() {
    if (iconsBeforeText == null)
      iconsBeforeText = new ArrayList<Icon>();
    return iconsBeforeText;
  }

  public List<Icon> getIconsAfterText(Object context, Object value) {
    return getIconsAfterText();
  }

  public List<Icon> getIconsAfterText() {
    if (iconsAfterText == null)
      iconsAfterText = new ArrayList<Icon>();
    return iconsAfterText;
  }

  /**
   * 添加一个前置图标
   *
   * @param icon 图标
   * @return
   */
  public IconTextFormater addIconBeforeText(Icon icon) {
    this.getIconsBeforeText().add(icon);
    return this;
  }

  /**
   * 添加一个后置图标
   *
   * @param icon 图标
   * @return
   */
  public IconTextFormater addIconAfterText(Icon icon) {
    this.getIconsAfterText().add(icon);
    return this;
  }

  /**
   * 小图标
   */
  public static class Icon {
    private String id;
    private String clazz;
    private String click;
    private String title;
    private String clickArguments;

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
  }
}