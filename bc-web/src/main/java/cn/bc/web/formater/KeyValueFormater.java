/**
 * 
 */
package cn.bc.web.formater;

import java.util.Map;

/**
 * 键值互换的格式化
 * 
 * @author dragon
 * 
 */
public class KeyValueFormater implements Formater {
	private Map<String, String> kvs;

	public KeyValueFormater() {
	}

	public KeyValueFormater(Map<String, String> kvs) {
		this.kvs = kvs;
	}

	public String format(Object value) {
		if (value == null)
			return null;
		if (kvs == null)
			return "undefined";

		String f = kvs.get(value.toString());
		return f == null ? "undefined" : f;
	}
}
