package cn.bc.web.struts2.converter;

import org.apache.struts2.util.StrutsTypeConverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * struts2 LocalTime 与字符串(HH:mm)间的转换
 *
 * @author dragon 2015-11-10
 */
public class LocalTimeConverter extends StrutsTypeConverter {
  private static DateTimeFormatter f = DateTimeFormatter.ISO_LOCAL_TIME;

  @Override
  public Object convertFromString(Map context, String[] values, Class toClass) {
    if (values == null || values.length == 0 || values[0] == null || values[0].isEmpty()) {
      return null;
    } else {
      return LocalTime.parse(values[0], f);
    }
  }

  @Override
  public String convertToString(Map context, Object o) {
    return o == null ? null : ((LocalTime) o).format(f);
  }
}