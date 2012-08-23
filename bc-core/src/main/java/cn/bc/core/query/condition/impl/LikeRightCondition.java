/**
 * 
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.QueryOperator;

/**
 * 右模糊匹配条件
 * 
 * @author dragon
 * 
 */
public class LikeRightCondition extends SimpleCondition {
	public LikeRightCondition(String name, Object value) {
		super(name, value, QueryOperator.LikeRight);
	}
}
