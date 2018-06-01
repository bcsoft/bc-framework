/**
 *
 */
package cn.bc.investigate.web.struts2;

import cn.bc.web.formater.AbstractFormater;

import java.util.Map;

/**
 * 是否作答的格式化
 *
 * @author dragon
 */
public class RespondStausFormater extends AbstractFormater<Object> {
  public RespondStausFormater setKvs(Map<String, ? extends Object> kvs) {
    return this;
  }

  public RespondStausFormater() {
  }

  public RespondStausFormater(Map<String, ? extends Object> kvs) {
  }

  public Object format(Object context, Object value) {
    if (value == null) {
      return "未作答";
    } else {
      return "已作答";
    }

  }
}
