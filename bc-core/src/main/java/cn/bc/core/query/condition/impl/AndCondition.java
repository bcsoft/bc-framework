/**
 * 
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.condition.Condition;


/**
 * 以and(和)方式合并条件
 * @author dragon
 *
 */
public class AndCondition extends MixCondition implements Condition{
	public AndCondition(){
		super("and");
	}
	
	public AndCondition(Condition... conditions){
		super("and",conditions);
	}
}
