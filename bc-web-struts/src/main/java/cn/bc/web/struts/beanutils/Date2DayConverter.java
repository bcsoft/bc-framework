package cn.bc.web.struts.beanutils;

import cn.bc.core.exception.CoreException;
import org.apache.commons.beanutils.Converter;

import java.text.ParseException;
import java.util.Date;


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
 * @see Date2Day
 * @since 2010-12-01
 */
public class Date2DayConverter implements Converter {
  public Object convert(@SuppressWarnings("rawtypes") Class clazz, Object value) {
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
