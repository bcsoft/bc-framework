package cn.bc.web.struts.beanutils;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.beanutils.Converter;

import cn.bc.core.exception.CoreException;


/**
 * <p>
 * String-->Date2Second<--Date 转换器
 * </p>
 * <p>
 * 额外处理如下情况的转换：
 * <p>
 * 1){@link String} --> {@link Date2Second}
 * </p>
 * <p>
 * 2){@link Date} --> {@link Date2Second}
 * </p>
 * <p>
 * 3)"" --> null
 * </p>
 * </p>
 * 
 * @author dragon
 * @since 2010-12-01
 * @see Date2Second
 */
public class Date2SecondConverter implements Converter {
	@SuppressWarnings("unchecked")
	public Object convert(Class clazz, Object value) {
		if (value == null)
			return null;
		if (value instanceof String) {// String-->Date2Second
			if (!"".equals((String) value)) {
				try {
					return new Date2Second(Date2Second.formater.parse(
							(String) value).getTime());
				} catch (ParseException e) {
					throw new CoreException(e);
				}
			} else {
				return null;
			}
		} else if (value instanceof Date) {// Date-->Date2Second
			return new Date2Second(((Date) value).getTime());
		} else {
			return value;
		}
	}
}
