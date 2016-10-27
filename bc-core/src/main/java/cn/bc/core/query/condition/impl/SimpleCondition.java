/**
 *
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.QueryOperator;
import cn.bc.core.query.condition.Condition;
import org.springframework.util.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
		} else if (operator == QueryOperator.Like || operator == QueryOperator.iLike) {
			String v = String.valueOf(value);
			this.values.add((v.startsWith("%") ? "" : "%") + v + (v.endsWith("%") ? "" : "%"));
		} else if (operator == QueryOperator.LikeLeft || operator == QueryOperator.iLikeLeft) {
			String v = String.valueOf(value);
			this.values.add(v + (v.endsWith("%") ? "" : "%"));
		} else if (operator == QueryOperator.LikeRight || operator == QueryOperator.iLikeRight) {
			String v = String.valueOf(value);
			this.values.add((v.startsWith("%") ? "" : "%") + v);
		} else if (operator == QueryOperator.IsNull
				|| operator == QueryOperator.IsNotNull) {

		} else {
			this.values.add(value);
		}
	}

	public String getExpression() {
		return getExpression(null);
	}

	public String getExpression(String alias) {
		String name = this.getName();
		if (alias != null && alias.length() > 0)
			name = alias + "." + name;
		StringBuilder e = new StringBuilder(name + " "
				+ this.operator.symbol());
		if (operator == QueryOperator.In || operator == QueryOperator.NotIn) {
			e.append(" (");
			for (int i = 0; i < values.size(); i++) {
				e.append(i == 0 ? "?" : ",?");
			}
			e.append(")");
		} else if (operator == QueryOperator.IsNull
				|| operator == QueryOperator.IsNotNull) {

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

	@Override
	public String toString() {
		return "ql="
				+ this.getExpression()
				+ ",args="
				+ StringUtils
				.collectionToCommaDelimitedString(this.getValues());
	}
}
