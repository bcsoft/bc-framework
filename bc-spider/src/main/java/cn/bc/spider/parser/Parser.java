package cn.bc.spider.parser;


/**
 * 数据解析转换器接口
 * 
 * @author dragon
 * 
 * @param <T>
 */
public interface Parser<T> {
	T parse(Object data);
}
