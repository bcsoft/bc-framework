/**
 * 
 */
package cn.bc.web.formater;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期类型值的格式化
 * 
 * @author dragon
 * 
 */
public class CalendarFormater implements Formater {
	private SimpleDateFormat format;

	public CalendarFormater() {
		format = new SimpleDateFormat("yyyy-MM-dd");
	}

	public CalendarFormater(String pattern) {
		format = new SimpleDateFormat(pattern);
	}

	public String format(Object value) {
		if (value == null)
			return null;
		if (value instanceof Calendar)
			return format.format(((Calendar) value).getTime());
		else if (value instanceof Date)
			return format.format(value);
		else
			return value.toString();
	}
}
