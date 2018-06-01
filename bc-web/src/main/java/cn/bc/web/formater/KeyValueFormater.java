/**
 *
 */
package cn.bc.web.formater;

import java.util.Map;

/**
 * 键值互换的格式化
 *
 * @author dragon
 */
public class KeyValueFormater extends AbstractFormater<Object> {
  private Map<String, ? extends Object> kvs;

  public KeyValueFormater setKvs(Map<String, ? extends Object> kvs) {
    this.kvs = kvs;
    return this;
  }

  public KeyValueFormater() {
  }

  public KeyValueFormater(Map<String, ? extends Object> kvs) {
    this.kvs = kvs;
  }

  public Object format(Object context, Object value) {
    if (value == null)
      return null;
    if (kvs == null)
      return "undefined";

    return kvs.get(value.toString()) != null ? kvs.get(value.toString())
      : value.toString();
  }
}
