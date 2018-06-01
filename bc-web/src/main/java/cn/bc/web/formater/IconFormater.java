/**
 *
 */
package cn.bc.web.formater;

/**
 * 带操作图标的格式化
 * <p>格式化后的结果为 "TEXT[ICON]" 或 "[ICON]TEXT"，默认图标紧跟在文字后面</p>
 *
 * @author dragon
 */
public class IconFormater extends AbstractFormater<Object> implements ExportText {
  private String iconClass;// 图标的样式类
  private String iconTip;// 图标的鼠标提示信息
  private String iconFn;// 图标的点击函数
  private String iconFnArgs;// 图标点击函数的参数
  private boolean beforeText;// 图标是否放在文字前

  public IconFormater(String iconClass, String iconTip, String iconFn) {
    this(iconClass, iconTip, iconFn, false);
  }

  public IconFormater(String iconClass, String iconTip, String iconFn, boolean beforeText) {
    this.iconClass = iconClass;
    this.iconTip = iconTip;
    this.iconFn = iconFn;
    this.beforeText = beforeText;
  }

  /**
   * 判断是否显示图标，默认为显示
   */
  public boolean showIcon(Object context, Object value) {
    return true;
  }

  /**
   * 获取图标的样式类
   */
  public String getIconClass(Object context, Object value) {
    return iconClass;
  }

  /**
   * 获取图标的鼠标提示信息
   */
  public String getIconTip(Object context, Object value) {
    return iconTip;
  }

  /**
   * 获取图标点击函数
   */
  public String getIconFn(Object context, Object value) {
    return iconFn;
  }

  /**
   * 获取图标点击函数的参数
   */
  public String getIconFnArgs(Object context, Object value) {
    return iconFnArgs;
  }

  public IconFormater setIconClass(String iconClass) {
    this.iconClass = iconClass;
    return this;
  }

  public IconFormater setIconTip(String iconTip) {
    this.iconTip = iconTip;
    return this;
  }

  public IconFormater setIconFn(String iconFn) {
    this.iconFn = iconFn;
    return this;
  }

  public IconFormater setIconFnArgs(String iconFnArgs) {
    this.iconFnArgs = iconFnArgs;
    return this;
  }

  @Override
  public Object format(Object context, Object value) {
    StringBuffer v = new StringBuffer();

    // 文字节点 html
    String text = this.getTextNode(context, value);

    boolean showIcon = this.showIcon(context, value);
    if (showIcon) {
      // 图标
      Icon icon = new Icon();
      icon.setClazz(this.getIconClass(context, value));
      icon.setTitle(this.getIconTip(context, value));
      icon.setClick(this.getIconFn(context, value));
      icon.setClickArguments(this.getIconFnArgs(context, value));

      // 图标在文字前
      if (this.beforeText) v.append(icon.wrap());

      // 显示的文字
      if (text != null) v.append(text);

      // 图标在文字后
      if (!this.beforeText) v.append(icon.wrap());
    } else {
      if (text != null) v.append(text);
    }

    if (v.length() > 0) {
      return v.toString();
    } else {
      return "&nbsp;";
    }
  }

  /**
   * 获取文字部分的 html 代码，默认为 value 的值
   */
  public String getTextNode(Object context, Object value) {
    return value != null ? value.toString() : null;
  }

  public String getExportText(Object context, Object value) {
    return this.getTextNode(context, value);
  }
}