/**
 * 
 */
package cn.bc.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.util.DateUtils;


/**
 * 简单的符号比较条件， condition的格式为：{ql:"...",values:[{type="...",value:"..."},...]}
 * 
 * @author dragon
 * 
 */
public class WebCondition implements Condition {
	static Log logger = LogFactory.getLog(WebCondition.class);
	protected String ql;// 查询语句
	private List<Object> values = new ArrayList<Object>(); // 查询参数的值

	public WebCondition(String condition) {
		try {
			JSONObject json = new JSONObject(condition);
			this.ql = json.getString("ql");// 查询字符串
			JSONArray valueConfigs = json.getJSONArray("values");// 值配置
			for (int i = 0; i < valueConfigs.length(); i++) {
				values.add(parseValue(valueConfigs.getJSONObject(i)));
			}
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public String getExpression() {
		return this.ql;
	}

	public List<Object> getValues() {
		return this.values;
	}

	/**
	 * 根据json的配置解析出对应类型的参数值
	 * 
	 * @param valueConfig
	 *            {type:"...",value:"..."},
	 *            其中type为string、date、int、long、float、double
	 * @return
	 * @throws JSONException
	 */
	public static Object parseValue(JSONObject valueConfig)
			throws JSONException {
		String valueType, valueStr;
		valueType = valueConfig.getString("type");
		valueStr = valueConfig.getString("value");
		return parseValue(valueStr, valueType);
	}

	/**
	 * 根据json的配置解析出对应类型的参数值
	 * 
	 * @param valueConfig
	 *            {type:"...",value:"..."},
	 *            其中type为string、date、int、long、float、double
	 * @return
	 * @throws JSONException
	 */
	public static Object parseValue(String valueStr, String valueType) {
		if ("string".equalsIgnoreCase(valueType)) {
			return valueStr;
		} else if ("date".equalsIgnoreCase(valueType)) {
			return DateUtils.getDate(valueStr);
		} else if ("int".equalsIgnoreCase(valueType)) {
			return new Integer(valueStr);
		} else if ("long".equalsIgnoreCase(valueType)) {
			return new Long(valueStr);
		} else if ("float".equalsIgnoreCase(valueType)) {
			return new Float(valueStr);
		} else if ("double".equalsIgnoreCase(valueType)) {
			return new Double(valueStr);
		} else if ("null".equalsIgnoreCase(valueType)) {
			return null;
		} else {
			throw new RuntimeException("未受支持的类型:valueType=" + valueType);
		}
	}
}
