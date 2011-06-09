package cn.bc.web.formater;

/**
 * 值格式化接口
 * 
 * @author dragon
 * 
 */
public interface Formater {
	/**
	 * 格式化指定的值为字符串
	 * 
	 * @param value
	 * @return
	 */
	String format(Object value);
}
