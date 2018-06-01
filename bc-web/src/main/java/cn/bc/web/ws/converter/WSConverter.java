package cn.bc.web.ws.converter;


/**
 * 调用WebService接口返回的XML信息到对象的转换器
 *
 * @author dragon
 */
public interface WSConverter<T> {
  T convert(String xml);
}
