/**
 * 
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.QueryOperator;

/**
 * 小于条件
 * 
 * @author dragon
 * 
 */
public class LessThanCondition extends SimpleCondition {
	public LessThanCondition(String name, Object value) {
		super(name, value, QueryOperator.LessThan);
	}
}
