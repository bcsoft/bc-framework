/**
 *
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.condition.Condition;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 以指定的方式合并条件
 *
 * @author dragon
 */
public abstract class MixCondition implements Condition {
  protected List<Condition> conditions = new ArrayList<Condition>();
  protected OrderCondition orderCondition;
  protected String misSymbol;
  private boolean addBracket;// 是否将表达式用括号括住

  public MixCondition(String misSymbol) {
    this.misSymbol = misSymbol;
  }

  public MixCondition(String misSymbol, Condition... conditions) {
    this(misSymbol);
    this.add(conditions);
  }

  /**
   * 判断条件是否为空
   *
   * @return
   */
  public boolean isEmpty() {
    return conditions.isEmpty()
      && (orderCondition == null || orderCondition.isEmpty());
  }

  public boolean isAddBracket() {
    return addBracket;
  }

  public MixCondition setAddBracket(boolean addBracket) {
    this.addBracket = addBracket;
    return this;
  }

  /**
   * 以and(和)方式合并给定的条件
   *
   * @param conditions 要合并的条件
   * @return
   */
  public MixCondition add(Condition... conditions) {
    if (conditions != null) {
      for (Condition condition : conditions) {
        if (condition instanceof OrderCondition)
          this.addOrder((OrderCondition) condition);
        else if (condition != null)
          this.conditions.add(condition);
      }
    }
    return this;
  }

  private void addOrder(OrderCondition condition) {
    if (orderCondition == null)
      this.orderCondition = new OrderCondition();
    this.orderCondition.add(condition);
  }

  public String getExpression() {
    return getExpression(null);
  }

  public String getExpression(String alias) {
    return getExpression(alias, false);
  }

  /**
   * @param alias
   * @param noOrderBy
   * @return
   */
  public String getExpression(String alias, boolean noOrderBy) {
    if (conditions.isEmpty()
      && (orderCondition == null || orderCondition.isEmpty())) {
      return "";
    } else {
      StringBuffer mc = new StringBuffer();
      boolean add = false;
      if (conditions != null && !conditions.isEmpty()) {
        add = conditions.size() > 1 && isAddBracket();
        if (add)
          mc.append("(");
        int i = 0;
        for (Condition condition : conditions) {
          mc.append((i == 0 ? "" : " " + this.misSymbol + " ")
            + condition.getExpression(alias));
          i++;
        }
        if (add)
          mc.append(")");
      }

      if ((!add && mc.length() > 0) || (add && mc.length() > 2)) {
        if (this.orderCondition != null && !noOrderBy) {
          String order = this.orderCondition.getExpression(alias);
          if (order != null && order.length() > 0)
            mc.append(" order by " + order);
        }

        return mc.toString();
      } else {
        if (this.orderCondition != null && !noOrderBy) {
          String order = this.orderCondition.getExpression(alias);
          if (order != null && order.length() > 0) {
            return "order by " + order;
          } else {
            return "";
          }
        }
        return "";
      }
    }
  }

  public List<Object> getValues() {
    List<Object> args = new ArrayList<Object>();
    for (Condition condition : conditions) {
      if (condition.getValues() != null)
        args.addAll(condition.getValues());
    }
    return args;
  }

  @Override
  public String toString() {
    return "ql="
      + this.getExpression()
      + ",args="
      + StringUtils
      .collectionToCommaDelimitedString(this.getValues());
  }
}
