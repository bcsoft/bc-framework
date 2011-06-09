package cn.bc.web.struts.beanutils;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.beanutils.Converter;

import cn.bc.core.exception.CoreException;


/**
 * <p>
 * String-->Date2Day<--Date 转换器
 * </p>
 * <p>
 * 额外处理如下情况的转换：
 * <p>
 * 1){@link String} --> {@link Date2Day}
 * </p>
 * <p>
 * 2){@link Date} --> {@link Date2Day}
 * </p>
 * <p>
 * 3)"" --> null
 * </p>
 * </p>
 * 
 * @author dragon
 * @since 2010-12-01
 * @see Date2Day
 */
public class Date2DayConverter implements Converter {
	@SuppressWarnings("unchecked")
	public Object convert(Class clazz, Object value) {
		if (value == null)
			return null;
		if (value instanceof String) {// String-->Date2Day
			if (!"".equals((String) value)) {
				try {
					return new Date2Day(Date2Day.formater.parse((String) value)
							.getTime());
				} catch (ParseException e) {
					throw new CoreException(e);
				}
			} else {
				return null;
			}
		} else if (value instanceof Date) {// Date-->Date2Day
			return new Date2Day(((Date) value).getTime());
		} else {
			return value;
		}
	}
}
