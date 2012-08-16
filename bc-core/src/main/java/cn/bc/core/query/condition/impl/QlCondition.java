/**
 * 
 */
package cn.bc.core.query.condition.impl;

import java.util.ArrayList;
import java.util.List;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.util.StringUtils;

/**
 * 基于查询语言的条件，如sql、hql
 * 
 * @author dragon
 * 
 */
public class QlCondition implements Condition {
	protected String ql;
	protected List<Object> values;

	public QlCondition(String ql) {
		this.ql = ql;
	}

	public QlCondition(String ql, List<Object> values) {
		this.ql = ql;
		this.values = values;
	}

	public QlCondition(String ql, Object[] values) {
		this.ql = ql;
		if (values != null && values.length > 0) {
			this.values = new ArrayList<Object>();
			for (Object value : values)
				this.values.add(value);
		}
	}

	public String getExpression() {
		return getExpression(null);
	}

	public String getExpression(String alias) {
		// 这里或略别名
		return ql;
	}

	public List<Object> getValues() {
		return values;
	}

	public static Object convertValue(String type, String value,
			boolean toLikeValue) {
		return convertValueByType(type,
				toLikeValue ? (value.indexOf("%") == -1 ? "%" + value + "%"
						: value) : value);
	}

	/**
	 * @param type
	 *            值类型："int"|"long"|"float"|"date"|"startDate"|"endDate"|
	 *            "calendar"| "startCalendar"|"endCalendar"
	 * @param value
	 * @return
	 */
	public static Object convertValueByType(String type, String value) {
		return StringUtils.convertValueByType(type, value);
	}

	@Override
	public String toString() {
		return "ql="
				+ this.getExpression()
				+ ",args="
				+ org.springframework.util.StringUtils
						.collectionToCommaDelimitedString(this.getValues());
	}
}
