package cn.bc.web.struts2.converter;

import org.apache.struts2.util.StrutsTypeConverter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * struts2 Date与字符串间精确到分钟的转换器
 *
 * @author dragon 2015/9/7
 */
public class Date2MinuteConverter extends StrutsTypeConverter {
	private final static Logger logger = LoggerFactory.getLogger(Date2MinuteConverter.class);
	private DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		logger.debug("convertFromString: values={}, toClass={}", values, toClass);
		if (values == null || values.length == 0 || values[0] == null || values[0].isEmpty()) {
			return null;
		} else {
			return f.parseDateTime(values[0]).toDate();
		}
	}

	@Override
	public String convertToString(Map context, Object o) {
		logger.debug("convertToString: o={}", o);
		return o == null ? null : f.print(((Date) o).getTime());
	}
}