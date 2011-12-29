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

	/**
	 * 获取属性值
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		Render r = get(key);
		return r != null ? r.toString() : null;
	}

	/**
	 * 判断json是否为空
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return attrs.isEmpty();
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
		if (value != null)
			attrs.put(key, value);
		else
			attrs.remove(key);
		return this;
	}

	public Json put(String key, Object value) {
		if (value != null)
			this.put(key, new Text(wrapQuota(value.toString())));
		else
			attrs.remove(key);
		return this;
	}

	public Json put(String key, String value) {
		if (value != null)
			this.put(key, new Text(wrapQuota(value)));
		else
			attrs.remove(key);
		return this;
	}

	public Json put(String key, int value) {
		this.put(key, new Text(String.valueOf(value)));
		return this;
	}

	public Json put(String key, Number value) {
		if (value != null)
			this.put(key, new Text(value.toString()));
		else
			attrs.remove(key);
		return this;
	}

	public Json put(String key, boolean value) {
		this.put(key, new Text(String.valueOf(value)));
		return this;
	}
}
