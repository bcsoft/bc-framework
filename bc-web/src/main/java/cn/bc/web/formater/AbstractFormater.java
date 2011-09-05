/**
 * 
 */
package cn.bc.web.formater;

/**
 * 格式化抽象基类
 * 
 * @author dragon
 * 
 */
public abstract class AbstractFormater<T> implements Formater<T> {
	public abstract T format(Object context, Object value);

	public T format(Object value) {
		return format(null, value);
	}
}
