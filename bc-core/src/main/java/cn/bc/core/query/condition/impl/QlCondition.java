/**
 * 
 */
package cn.bc.core.query.condition.impl;

import java.util.List;

import cn.bc.core.query.condition.Condition;


/**
 * 基于查询语言的条件，如sql、hql
 * 
 * @author dragon
 * 
 */
public class QlCondition implements Condition {
	protected String ql;
	protected List<Object> values;

	public QlCondition(String ql, List<Object> values) {
		this.ql = ql;
		this.values = values;
	}

	public String getExpression() {
		return ql;
	}

	public List<Object> getValues() {
		return values;
	}
}
