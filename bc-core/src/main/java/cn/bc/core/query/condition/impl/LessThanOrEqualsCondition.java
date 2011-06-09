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
public class LessThanOrEqualsCondition extends SimpleCondition {
	public LessThanOrEqualsCondition(String name, Object value) {
		super(name, value, QueryOperator.LessThanOrEquals);
	}
}
