/**
 * 
 */
package cn.bc.web.formater;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 布尔类型值的格式化
 * 
 * @author dragon
 * 
 */
public class FileSizeFormater implements Formater {
	private static NumberFormat format = new DecimalFormat("#.#");

	public FileSizeFormater() {
	}

	public String format(Object value) {
		if (value == null)
			return null;
		if (value instanceof Number) {
			float n = ((Number) value).floatValue();
			if (n < 1024)
				return n + "Bytes";// 字节
			else if (n < 1024 * 1024)
				return format.format(((float) n) / 1024f) + "KB";// KB
			else
				return format.format(((float) n) / 1024f / 1024f) + "MB";// MB
		} else {
			return value.toString();
		}
	}
}
