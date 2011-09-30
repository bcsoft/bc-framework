package cn.bc.web.formater;

/**
 * 值格式化接口
 * 
 * @author dragon
 * 
 */
public interface Formater<T> {
	/**
	 * 格式化指定的值
	 * 
	 * @param value 要格式化的值
	 * @return
	 */
	T format(Object value);
	
	/**
	 * 在指定的上下文下格式化指定的值
	 * 
	 * @param value 要格式化的值
	 * @param context 上下文对象，如有必要可以利用此对象进行额外的扩展
	 * @return
	 */
	T format(Object context, Object value);
}
