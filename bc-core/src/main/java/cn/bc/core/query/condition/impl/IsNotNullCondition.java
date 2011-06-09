/**
 * 
 */
package cn.bc.core.query.condition.impl;

import java.util.List;

import cn.bc.core.query.condition.Condition;



/**
 * is not null条件
 * @author dragon
 *
 */
public class IsNotNullCondition implements Condition {
	protected String name;
	
	public IsNotNullCondition(String name){
		this.name = name;
	}

	public String getExpression() {
		return this.name + " is not null";
	}

	public List<Object> getValues() {
		return null;
	}
}
