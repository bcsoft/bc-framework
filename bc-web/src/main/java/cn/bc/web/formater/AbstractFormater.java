/**
 *
 */
package cn.bc.web.formater;

/**
 * 格式化抽象基类
 *
 * @author dragon
 */
public abstract class AbstractFormater<T> implements Formater<T>, ExportText {
  public abstract T format(Object context, Object value);

  public T format(Object value) {
    return format(null, value);
  }

  public String getExportText(Object context, Object value) {
    T v = this.format(context, value);
    return v == null ? null : v.toString();
  }
}
