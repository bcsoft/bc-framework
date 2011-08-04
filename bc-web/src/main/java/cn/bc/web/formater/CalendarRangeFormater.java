/**
 * 
 */
package cn.bc.web.formater;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 日期范围的格式化
 * <p>
 * 如输入两个日期信息，默认显示类似“2011-01-01～2011-02-01”的格式
 * </p>
 * 
 * @author dragon
 * 
 */
public abstract class CalendarRangeFormater extends AbstractFormater {
	private SimpleDateFormat format;

	public CalendarRangeFormater() {
		//默认日期格式
		format = new SimpleDateFormat("yyyy-MM-dd");
	}

	/**
	 * @param pattern 日期格式，如yyy-MM-dd HH:ss:mm
	 */
	public CalendarRangeFormater(String pattern) {
		format = new SimpleDateFormat(pattern);
	}

	public String format(Object context, Object value) {
		Calendar fromDate = getFromDate(context, value);
		Calendar toDate = getToDate(context, value);
		if (fromDate == null) {
			if (toDate == null) {
				return "";
			} else {
				return "～" + format.format(toDate.getTime());
			}
		} else {
			if (toDate == null) {
				return format.format(fromDate.getTime()) + "～";
			} else {
				return format.format(fromDate.getTime()) + "～"
						+ format.format(toDate.getTime());
			}
		}
	}

	public Calendar getFromDate(Object context, Object value) {
		//默认将传入的值当作起始日期值
		return (Calendar) value;
	}

	/**
	 * 从上下文获取结束日期的值
	 * @param context
	 * @param value
	 * @return
	 */
	public abstract Calendar getToDate(Object context, Object value);
}
