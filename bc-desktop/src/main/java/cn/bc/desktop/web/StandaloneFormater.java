/**
 * 
 */
package cn.bc.desktop.web;

import cn.bc.web.formater.AbstractFormater;

/**
 * 快捷方式类型值的格式化
 * 
 * @author dragon
 * 
 */
public class StandaloneFormater extends AbstractFormater<String> {
	private String yes = "外部链接";
	private String no = "内部链接";

	public StandaloneFormater() {

	}

	public StandaloneFormater(String yes, String no) {
		this();
		this.yes = yes;
		this.no = no;
	}

	public String format(Object context, Object value) {
		if (value == null)
			return null;
		if (value instanceof Boolean)
			return ((Boolean) value).booleanValue() ? yes : no;
		else if (value instanceof String)
			return "true".equalsIgnoreCase((String) value) ? yes : no;
		else
			return value.toString();
	}
}
