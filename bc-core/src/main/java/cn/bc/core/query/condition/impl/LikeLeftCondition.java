/**
 * 
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.QueryOperator;


/**
 * 左模糊匹配条件
 * @author dragon
 *
 */
public class LikeLeftCondition extends SimpleCondition {
	public LikeLeftCondition(String name, Object value) {
		super(name, value, QueryOperator.LikeLeft);
	}
}
