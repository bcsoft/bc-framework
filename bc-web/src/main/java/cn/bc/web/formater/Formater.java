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
	 * @param value 要格式化的值
	 * @return
	 */
	String format(Object value);
	
	/**
	 * 在指定的上下文下格式化指定的值为字符串
	 * 
	 * @param value 要格式化的值
	 * @param context 上下文对象，如有必要可以利用此对象进行额外的扩展
	 * @return
	 */
	String format(Object context, Object value);
}
