/**
 * 
 */
package cn.bc.web.formater;

import java.text.DecimalFormat;

/**
 * 数字类型值的格式化
 * 
 * @author dragon
 * 
 */
public class NumberFormater extends AbstractFormater<String> {
	protected DecimalFormat format;

	public NumberFormater() {
		format = new DecimalFormat("#.#");
	}

	public NumberFormater(String pattern) {
		format = new DecimalFormat(pattern);
	}

	public String format(Object context, Object value) {
		if (value == null)
			return null;
		if (value instanceof Number)
			return format.format((Number) value);
		else
			return value.toString();
	}
}
