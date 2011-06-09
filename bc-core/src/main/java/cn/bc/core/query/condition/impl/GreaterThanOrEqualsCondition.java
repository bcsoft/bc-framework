/**
 * 
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.QueryOperator;


/**
 * 大于条件
 * @author dragon
 *
 */
public class GreaterThanOrEqualsCondition extends SimpleCondition {
	public GreaterThanOrEqualsCondition(String name,Object value){
		super(name,value,QueryOperator.GreaterThanOrEquals);
	}
}