/**
 * 
 */
package cn.bc.web.ws.dotnet.formater;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bc.web.formater.AbstractFormater;

/**
 * 日期类型值的格式化
 * 
 * @author dragon
 * 
 */
public class WSCalendarFormater extends AbstractFormater<Calendar> {
	private SimpleDateFormat format;

	public WSCalendarFormater() {
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public WSCalendarFormater(String pattern) {
		format = new SimpleDateFormat(pattern);
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
			Date t;
			try {
				t = format.parse(value.toString().replace("T", " ")
						.replace("+08:00", ""));
				Calendar c = Calendar.getInstance();
				c.setTime(t);
				return c;
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
