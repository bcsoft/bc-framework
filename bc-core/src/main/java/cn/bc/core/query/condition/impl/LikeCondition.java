/**
 * 
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.QueryOperator;


/**
 * 模糊匹配条件
 * @author dragon
 *
 */
public class LikeCondition extends SimpleCondition {
	public LikeCondition(String name, Object value) {
		super(name, value, QueryOperator.Like);
	}
}
