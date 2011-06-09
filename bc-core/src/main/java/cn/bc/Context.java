/**
 * 
 */
package cn.bc;

/**
 * 系统上下文
 * @author dragon
 *
 */
public interface Context {
	/**
	 * 获取指定属性的值
	 * @param key 属性名称
	 * @return
	 */
	Object getAttribute(String key);
	
	/**
	 * 设置或添加指定的属性
	 * @param key 属性名称	
	 * @param value 属性值
	 */
	void setAttribute(String key, Object value);
}
