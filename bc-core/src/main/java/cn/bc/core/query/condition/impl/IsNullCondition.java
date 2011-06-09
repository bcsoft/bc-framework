/**
 * 
 */
package cn.bc.core.query.condition.impl;

import java.util.List;

import cn.bc.core.query.condition.Condition;



/**
 * is null条件
 * @author dragon
 *
 */
public class IsNullCondition implements Condition {
	protected String name;
	
	public IsNullCondition(String name){
		this.name = name;
	}

	public String getExpression() {
		return this.name + " is null";
	}

	public List<Object> getValues() {
		return null;
	}
}
