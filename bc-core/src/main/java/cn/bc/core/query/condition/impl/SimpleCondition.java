/**
 * 
 */
package cn.bc.core.query.condition.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.bc.core.query.QueryOperator;
import cn.bc.core.query.condition.Condition;


/**
 * 简单的符号比较条件
 * 
 * @author dragon
 * @see {@link QueryOperator},{@link Condition}
 */
public class SimpleCondition implements Condition {
	protected String name;
	protected QueryOperator operator;
	protected List<Object> values = new ArrayList<Object>(1);

	@SuppressWarnings("rawtypes")
	public SimpleCondition(String name, Object value, QueryOperator operator) {
		this.name = name;
		this.operator = operator;
		if (operator == QueryOperator.In || operator == QueryOperator.NotIn) {
			if (value.getClass().isArray()) {
				for (int i = 0; i < Array.getLength(value); i++) {
					this.values.add(Array.get(value, i));
				}
			} else if (value instanceof Collection) {
				for (Object v : (Collection) value) {
					this.values.add(v);
				}
			}
		} else if (operator == QueryOperator.Like) {
			this.values.add("%" + String.valueOf(value) + "%");
		} else if (operator == QueryOperator.LikeLeft) {
			this.values.add("%" + String.valueOf(value));
		} else if (operator == QueryOperator.LikeRight) {
			this.values.add(String.valueOf(value) + "%");
		} else if (operator == QueryOperator.IsNull
				|| operator == QueryOperator.IsNotNull) {

		} else {
			this.values.add(value);
		}
	}

	public String getExpression() {
		StringBuilder e = new StringBuilder(name + " "
				+ this.operator.toSymbol());
		if (operator == QueryOperator.In || operator == QueryOperator.NotIn) {
			e.append(" (");
			for (int i = 0; i < values.size(); i++) {
				e.append(i == 0 ? "?" : ",?");
			}
			e.append(")");
		} else if (operator == QueryOperator.IsNull || operator == QueryOperator.IsNotNull) {
			
		} else {
			e.append(" ?");
		}
		return e.toString();
	}

	public List<Object> getValues() {
		return values;
	}

	protected String getName() {
		return this.name;
	}
}
