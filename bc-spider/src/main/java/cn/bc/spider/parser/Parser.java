package cn.bc.spider.parser;


/**
 * 数据解析转换器接口
 *
 * @param <T>
 * @author dragon
 */
public interface Parser<T> {
  T parse(Object data);
}
