package cn.bc.web.struts2.converter;

import org.apache.struts2.util.StrutsTypeConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Map;

/**
 * struts2 LocalDateTime 与字符串(yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd HH:mm)间的转换
 *
 * @author dragon 2015-11-10
 */
public class LocalDateTimeConverter extends StrutsTypeConverter {
  private static DateTimeFormatter f = new DateTimeFormatterBuilder()
    .parseCaseInsensitive()
    .append(DateTimeFormatter.ISO_LOCAL_DATE)
    .appendLiteral(' ')
    .append(DateTimeFormatter.ISO_LOCAL_TIME)
    .toFormatter();

  @Override
  public Object convertFromString(Map context, String[] values, Class toClass) {
    if (values == null || values.length == 0 || values[0] == null || values[0].isEmpty()) {
      return null;
    } else {
      return LocalDateTime.parse(values[0], f);
    }
  }

  @Override
  public String convertToString(Map context, Object o) {
    return o == null ? null : ((LocalDateTime) o).format(f);
  }
}