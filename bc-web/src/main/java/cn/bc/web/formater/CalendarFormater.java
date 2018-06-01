/**
 *
 */
package cn.bc.web.formater;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;

/**
 * 日期类型值的格式化
 *
 * @author dragon
 */
public class CalendarFormater extends AbstractFormater<Object> {
  protected DateTimeFormatter format;

  public CalendarFormater() {
    format = DateTimeFormat.forPattern("yyyy-MM-dd");
  }

  public CalendarFormater(String pattern) {
    format = DateTimeFormat.forPattern(pattern);
  }

  public String format(Object context, Object value) {
    if (value == null)
      return null;
    if (value instanceof Calendar)
      return format.print(((Calendar) value).getTimeInMillis());
    else if (value instanceof Date)
      return format.print(((Date) value).getTime());
    else
      return value.toString();
  }
}