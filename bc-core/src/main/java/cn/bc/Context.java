/**
 * 
 */
package cn.bc;

/**
 * 上下文
 * @author dragon
 *
 */
public interface Context {
	/** 系统上下文保存到session中的键值 */
	public static String KEY = "bc-context";
	
	/**
	 * 获取指定属性的值
	 * @param key 属性名称
	 * @return
	 */
	<T> T getAttr(String key);
	
	/**
	 * 设置或添加指定的属性
	 * @param key 属性名称	
	 * @param value 属性值
	 */
	Context setAttr(String key, Object value);
}
