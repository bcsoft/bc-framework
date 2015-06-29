/**
 *
 */
package cn.bc.web.ws.dotnet.formater;

import cn.bc.web.formater.AbstractFormater;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Calendar;
import java.util.Date;

/**
 * 日期类型值的格式化, 默认按ISO8601标准格式 yyyy-MM-dd'T'HH:mm:ss.SSSZZ
 *
 * @author dragon
 */
public class WSCalendarFormatter extends AbstractFormater<Calendar> {
	private DateTimeFormatter format;

	public WSCalendarFormatter() {
		format = ISODateTimeFormat.dateTime();// DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
	}

	public WSCalendarFormatter(String pattern) {
		format = DateTimeFormat.forPattern(pattern);
	}

	public Calendar format(Object context, Object value) {
		if (value == null)
			return null;
		if (value instanceof Calendar) {
			return (Calendar) value;
		} else if (value instanceof Date) {
			Calendar c = Calendar.getInstance();
			c.setTime((Date) value);
			return c;
		} else {
			return format.parseDateTime(value.toString()).toCalendar(null);
		}
	}
}
