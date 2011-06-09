/**
 * 
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.QueryOperator;

/**
 * 大于条件
 * 
 * @author dragon
 * 
 */
public class GreaterThanCondition extends SimpleCondition {
	public GreaterThanCondition(String name, Object value) {
		super(name, value, QueryOperator.GreaterThan);
	}
}