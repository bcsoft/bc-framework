package cn.bc.report.chart.hightcharts;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONObject_ extends JSONObject {
	public JSONObject_() {
		super();
	}

	public JSONObject_(JSONObject defaultOption) throws JSONException {
		init(defaultOption);
	}

	/**
	 * 根据默认的json配置初始化
	 * 
	 * @param defaultOption
	 *            默认配置
	 * @throws JSONException
	 */
	private void init(JSONObject defaultOption) throws JSONException {
		if (defaultOption == null)
			return;

		// 复制数据
		@SuppressWarnings("unchecked")
		Iterator<String> itor = defaultOption.keys();
		String key;
		while (itor.hasNext()) {
			key = itor.next();
			this.put(key, defaultOption.get(key));
		}

		// 特殊处理
		convertFrom(defaultOption);
	};

	/**
	 * 根据默认的json配置初始化
	 * 
	 * @param defaultOption
	 *            默认配置
	 * @throws JSONException
	 */
	protected void convertFrom(JSONObject defaultOption) throws JSONException {
		// do nothing
	};

	protected String _getString(String key) {
		if (this.has(key)) {
			try {
				return this.getString(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected boolean _getBoolean(String key) {
		if (this.has(key)) {
			try {
				return this.getBoolean(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	protected Object _get(String key) {
		if (this.has(key)) {
			try {
				return this.get(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected void _add(String key, Object value) {
		if (value == null) {
			this.remove(key);
		} else {
			try {
				this.put(key, value);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
