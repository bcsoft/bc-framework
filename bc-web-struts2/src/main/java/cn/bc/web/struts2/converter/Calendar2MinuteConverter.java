package cn.bc.web.struts2.converter;

import org.apache.struts2.util.StrutsTypeConverter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Map;

/**
 * struts2 Calendar与字符串间精确到分钟的转换器
 *
 * @author dragon 2015/9/7
 */
public class Calendar2MinuteConverter extends StrutsTypeConverter {
  private final static Logger logger = LoggerFactory.getLogger(Calendar2MinuteConverter.class);
  private DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

  @Override
  public Object convertFromString(Map context, String[] values, Class toClass) {
    logger.debug("convertFromString: values={}, toClass={}", values, toClass);
    if (values == null || values.length == 0 || values[0] == null || values[0].isEmpty()) {
      return null;
    } else {
      Calendar c = Calendar.getInstance();
      c.setTimeInMillis(f.parseDateTime(values[0]).getMillis());
      return c;
    }
  }

  @Override
  public String convertToString(Map context, Object o) {
    logger.debug("convertToString: o={}", o);
    return o == null ? null : f.print(((Calendar) o).getTimeInMillis());
  }
}