package cn.bc.web.struts2.converter;

import org.apache.struts2.util.StrutsTypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * struts2 LocalDate 与字符串(yyyy-MM-dd)间的转换
 *
 * @author dragon 2015-11-10
 */
public class LocalDateConverter extends StrutsTypeConverter {
	private static DateTimeFormatter f = DateTimeFormatter.ISO_LOCAL_DATE;

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		if (values == null || values.length == 0 || values[0] == null || values[0].isEmpty()) {
			return null;
		} else {
			return LocalDate.parse(values[0], f);
		}
	}

	@Override
	public String convertToString(Map context, Object o) {
		return o == null ? null : ((LocalDate) o).format(f);
	}
}