/**
 *
 */
package cn.bc.web.formater;

/**
 * 针对通过id打开超链接的格式化
 * <p>
 * 生成类似&lt;a ...&gt;XXXX&lt;/a&gt;的格式
 * </p>
 *
 * @author dragon
 */
public abstract class LinkFormater4Id extends LinkFormater {

  /**
   * @param urlPattern url格式，不要包含上下文路径，如"/bc/user/open?id={0}"
   * @param moduleKey  所链接到模块的标识键
   */
  public LinkFormater4Id(String urlPattern, String moduleKey) {
    super(urlPattern, moduleKey);
  }

  /**
   * 获取id的值
   *
   * @param context
   * @param value
   * @return
   */
  public abstract String getIdValue(Object context, Object value);

  @Override
  public Object[] getParams(Object context, Object value) {
    String id = this.getIdValue(context, value);
    if (id == null)
      id = "";
    return new Object[]{id};
  }

  @Override
  public String getTaskbarTitle(Object context, Object value) {
    return this.getLinkText(context, value);
  }

  @Override
  public String getWinId(Object context, Object value) {
    return this.moduleKey + this.getIdValue(context, value);
  }

  @Override
  protected boolean isLinkable(Object context, Object value) {
    return this.getIdValue(context, value) != null;
  }
}
