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
public abstract class AbstractFormater implements Formater {
	public abstract String format(Object context, Object value);

	public String format(Object value) {
		return format(null, value);
	}
}
