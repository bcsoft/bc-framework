/**
 *
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.QueryOperator;

import java.io.Serializable;
import java.util.Collection;

/**
 * in条件,经常出现性能问题，尽量少用
 *
 * @author dragon
 */
public class InCondition extends SimpleCondition {
  public InCondition(String name, Serializable[] values) {
    super(name, values, QueryOperator.In);
  }

  public InCondition(String name, Collection<? extends Object> values) {
    super(name, values, QueryOperator.In);
  }
}
