/**
 *
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.QueryOperator;

import java.io.Serializable;
import java.util.Collection;

/**
 * not in条件
 *
 * @author dragon
 */
public class NotInCondition extends SimpleCondition {
  public NotInCondition(String name, Serializable[] values) {
    super(name, values, QueryOperator.NotIn);
  }

  public NotInCondition(String name, Collection<? extends Object> values) {
    super(name, values, QueryOperator.NotIn);
  }
}
