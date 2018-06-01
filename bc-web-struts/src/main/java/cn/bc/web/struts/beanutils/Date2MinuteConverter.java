package cn.bc.web.struts.beanutils;

import cn.bc.core.exception.CoreException;
import org.apache.commons.beanutils.Converter;

import java.text.ParseException;
import java.util.Date;


/**
 * <p>
 * String-->Date2Minute<--Date 转换器
 * </p>
 * <p>
 * 额外处理如下情况的转换：
 * <p>
 * 1){@link String} --> {@link Date2Minute}
 * </p>
 * <p>
 * 2){@link Date} --> {@link Date2Minute}
 * </p>
 * <p>
 * 3)"" --> null
 * </p>
 * </p>
 *
 * @author dragon
 * @see Date2Minute
 * @since 2010-12-01
 */
public class Date2MinuteConverter implements Converter {
  public Object convert(@SuppressWarnings("rawtypes") Class clazz, Object value) {
    if (value == null)
      return null;
    if (value instanceof String) {// String-->Date2Minute
      if (!"".equals((String) value)) {
        try {
          return new Date2Minute(Date2Minute.formater.parse(
            (String) value).getTime());
        } catch (ParseException e) {
          throw new CoreException(e);
        }
      } else {
        return null;
      }
    } else if (value instanceof Date) {// Date-->Date2Minute
      return new Date2Minute(((Date) value).getTime());
    } else {
      return value;
    }
  }
}
