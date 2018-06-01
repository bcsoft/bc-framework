/**
 *
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.condition.Condition;

import java.util.List;


/**
 * is null条件
 *
 * @author dragon
 */
public class IsNullCondition implements Condition {
  protected String name;

  public IsNullCondition(String name) {
    this.name = name;
  }

  public String getExpression() {
    return getExpression(null);
  }

  public String getExpression(String alias) {
    String e = this.name;
    if (alias != null && alias.length() > 0)
      e = alias + "." + e;
    return e + " is null";
  }

  public List<Object> getValues() {
    return null;
  }
}
