/**
 *
 */
package cn.bc.web.formater;

import cn.bc.BCConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Entity状态值的格式化
 *
 * @author dragon
 */
public class EntityStatusFormater extends AbstractFormater<String> {
  private Map<String, String> statuses;

  public EntityStatusFormater() {
    statuses = new HashMap<String, String>();
    statuses.put(String.valueOf(BCConstants.STATUS_DISABLED), "已禁用");
    statuses.put(String.valueOf(BCConstants.STATUS_ENABLED), "启用中");
    statuses.put(String.valueOf(BCConstants.STATUS_DELETED), "已删除");
  }

  public EntityStatusFormater(Map<String, String> statuses) {
    this.statuses = statuses;
  }

  public String format(Object context, Object value) {
    if (value == null)
      return null;

    String f = statuses.get(value.toString());
    return f == null ? "undefined" : f;
  }
}
