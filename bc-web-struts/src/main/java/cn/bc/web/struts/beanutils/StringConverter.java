package cn.bc.web.struts.beanutils;

import org.apache.commons.beanutils.Converter;

/**
 * 将空字符""串转换为null的String转换。
 * 该转换器与org.apache.commons.beanutils.Converter.StringConverter的区别仅在于
 * ""到null的转换处理
 *
 * @author dragon
 * @see org.apache.commons.beanutils.Converter.StringConverter
 * @since 2009-02-22
 */
public final class StringConverter implements Converter {
  public StringConverter() {
  }

  public Object convert(@SuppressWarnings("rawtypes") Class type, Object value) {
    if (value == null || "".equals(value.toString())) {
      return (String) null;
    } else {
      return value.toString();
    }
  }
}
