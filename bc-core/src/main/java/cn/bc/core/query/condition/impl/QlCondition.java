/**
 * 
 */
package cn.bc.core.query.condition.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;

import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.util.DateUtils;

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
		return convertValueByType(type, toLikeValue ? "%" + value + "%" : value);
	}

	/**
	 * @param type
	 *            值类型："int"|"long"|"float"|"date"|"startDate"|"endDate"|"calendar"|
	 *            "startCalendar"|"endCalendar"
	 * @param value
	 * @return
	 */
	public static Object convertValueByType(String type, String value) {
		if (type == null || type.length() == 0)
			return value;

		if (type.equals("int")) {
			return new Integer(value);
		} else if (type.equals("string")) {
			return value;
		} else if (type.equals("long")) {
			return new Long(value);
		} else if (type.equals("float")) {
			return new Float(value);
		} else if (type.equals("date")) {
			return DateUtils.getDate(value);
		} else if (type.equals("startDate")) {
			Date date = DateUtils.getDate(value);
			DateUtils.setToZeroTime(date);
			return date;
		} else if (type.equals("endDate")) {
			Date date = DateUtils.getDate(value);
			DateUtils.setToMaxTime(date);
			return date;
		} else if (type.equals("calendar")) {
			return DateUtils.getCalendar(value);
		} else if (type.equals("startCalendar")) {
			Calendar calendar = DateUtils.getCalendar(value);
			DateUtils.setToZeroTime(calendar);
			return calendar;
		} else if (type.equals("endCalendar")) {
			Calendar calendar = DateUtils.getCalendar(value);
			DateUtils.setToMaxTime(calendar);
			return calendar;
		} else {
			throw new CoreException("unsupport value type: type=" + type
					+ ",value=" + value);
		}
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
