/**
 *
 */
package cn.bc.web.formater;

import cn.bc.core.util.StringUtils;

import java.text.MessageFormat;

/**
 * 超链接的格式化
 * <p>
 * 生成类似&lt;a ...&gt;XXXX&lt;/a&gt;的格式
 * </p>
 *
 * @author dragon
 */
public abstract class LinkFormater extends AbstractFormater<Object> implements ExportText {
  protected String urlPattern;
  protected String moduleKey;

  public LinkFormater() {
  }

  /**
   * @param urlPattern url格式，不要包含上下文路径，如"/bc/user/open?id={0}&name={1}"
   * @param moduleKey  所链接到模块的标识键
   */
  public LinkFormater(String urlPattern, String moduleKey) {
    this.urlPattern = urlPattern;
    this.moduleKey = moduleKey;
  }

  public Object format(Object context, Object value) {
    // 判断是否应该格式化链接
    if (!this.isLinkable(context, value)) {
      return value != null ? value.toString() : "";
    }

    Object _value;
    if (value instanceof Formater) {
      _value = ((Formater<?>) value).format(context, value);
    } else {
      _value = value;
    }
    String label = this.getLinkText(context, value);
    if (label != null && label.length() > 0) {
      Object[] params = getParams(context, _value);
      String href = MessageFormat.format(this.urlPattern, params);
      return buildLinkHtml(label, href, this.getTaskbarTitle(context, value), this.moduleKey, this.getWinId(context, value));
    } else {
      return "&nbsp;";
    }
  }

  /**
   * 构建超链接的 Html
   *
   * @param label 显示的文字
   * @param href  超链接
   * @param title 任务栏标题
   * @param mtype [可选]模块类型
   * @param mid   [可选]对话框ID
   * @return
   */
  public static String buildLinkHtml(String label, String href, String title, String mtype, String mid) {
    String tpl = "<a href=\"" + href + "\" class=\"bc-link\"";

    // 任务栏显示的标题
    if (mtype != null) tpl += " data-mtype=\"" + mtype + "\"";

    // 任务栏显示的标题
    if (title != null) tpl += " data-title=\"" + title + "\"";

    // 对话框的id
    if (mid != null) tpl += " data-mid=\"" + mid + "\"";

    tpl += ">" + label + "</a>";
    return tpl;
  }

  /**
   * 确定是否应该格式化连接
   *
   * @param context 上下文
   * @param value   当前值
   * @return
   */
  protected boolean isLinkable(Object context, Object value) {
    return true;
  }

  /**
   * 和获取url的格式化参数信息
   *
   * @param context 上下文
   * @param value   参数值
   * @return
   */
  public abstract Object[] getParams(Object context, Object value);

  /**
   * 链接显示的文字
   *
   * @param context
   * @param value
   * @return
   */
  public String getLinkText(Object context, Object value) {
    return StringUtils.toString(value);
  }

  public String getExportText(Object context, Object value) {
    return getLinkText(context, value);
  }

  /**
   * 任务栏显示的标题
   *
   * @param context
   * @param value
   * @return
   */
  public String getTaskbarTitle(Object context, Object value) {
    return this.getLinkText(context, value);
  }

  /**
   * 窗口的id
   *
   * @param context
   * @param value
   * @return
   */
  public String getWinId(Object context, Object value) {
    return this.moduleKey + value;
  }
}
