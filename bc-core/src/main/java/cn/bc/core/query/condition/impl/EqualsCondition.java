/**
 * 
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.QueryOperator;

/**
 * 相等条件
 * 
 * @author dragon
 * 
 */
public class EqualsCondition extends SimpleCondition {
	public EqualsCondition(String name, Object value) {
		super(name, value, QueryOperator.Equals);
	}
}
