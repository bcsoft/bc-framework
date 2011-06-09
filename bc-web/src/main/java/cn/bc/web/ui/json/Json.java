package cn.bc.web.ui.json;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.core.util.StringUtils;
import cn.bc.web.ui.Render;
import cn.bc.web.ui.html.Text;

/**
 * json渲染
 * 
 * @author dragon
 * 
 */
public class Json implements Render {
	protected Log logger = LogFactory.getLog(getClass());
	public static final String PREFIX = "{";
	public static final String SUFFIX = "}";
	public static final String COLON = ":";
	public static final String COMMA = ",";

	/**
	 * 在字符串两边添加双引号
	 * 
	 * @param str
	 *            源字符串
	 * @return
	 */
	public static String wrapQuota(String str) {
		return StringUtils.wrapQuota(str);
	}

	protected Map<String, Render> attrs = new LinkedHashMap<String, Render>();

	/**
	 * 获取属性值
	 * 
	 * @param key
	 *            属性名
	 * @return 属性值
	 */
	public Render get(String key) {
		return attrs.get(key);
	}

	public StringBuffer render(StringBuffer main) {
		main.append(PREFIX);
		int i = 0;
		for (Entry<String, Render> e : attrs.entrySet()) {
			if (i > 0)
				main.append(COMMA);
			main.append(wrapQuota(e.getKey()) + COLON + e.getValue());
			i++;
		}
		main.append(SUFFIX);
		if (logger.isDebugEnabled())
			logger.debug(main.toString());
		return main;
	}

	public String toString() {
		StringBuffer main = new StringBuffer();
		render(main);
		return main.toString();
	}

	/**
	 * 设置属性值
	 * 
	 * @param key
	 *            属性名
	 * @param value
	 *            属性值
	 */
	public Json put(String key, Render value) {
		attrs.put(key, value);
		return this;
	}

	public Json put(String key, String value) {
		attrs.put(key, new Text(wrapQuota(value)));
		return this;
	}

	public Json put(String key, int value) {
		attrs.put(key, new Text(String.valueOf(value)));
		return this;
	}

	public Json put(String key, Long value) {
		attrs.put(key, new Text(String.valueOf(value)));
		return this;
	}

	public Json put(String key, boolean value) {
		attrs.put(key, new Text(String.valueOf(value)));
		return this;
	}
}
