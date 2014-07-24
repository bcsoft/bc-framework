/**
 * 
 */
package cn.bc.web.formater;

import java.text.DecimalFormat;

/**
 * 数字格式化为金额字符串
 * 
 * @author dragon
 * 
 */
public class MoneyFormater extends AbstractFormater<String> {
	protected DecimalFormat format;
	private String pattern = "##,###,###,###.#";

	public MoneyFormater() {
		format = new DecimalFormat("￥ " + pattern);
	}

	public MoneyFormater(boolean noSymble) {
		if(noSymble){
			format = new DecimalFormat(pattern);
		}else{
			format = new DecimalFormat("￥ " + pattern);
		}
	}

	public MoneyFormater(String pattern) {
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

	@Override
	public String getExportText(Object context, Object value) {
		if (value == null)
			return null;
		if (value instanceof Number)
			return new DecimalFormat(pattern).format((Number) value);
		else
			return value.toString();
	}
}
