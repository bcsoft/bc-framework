/**
 *
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.QueryOperator;
import cn.bc.core.query.condition.AdvanceCondition;
import cn.bc.core.util.StringUtils;

import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * 高级条件配置接口的默认实现
 *
 * @author dragon
 */
public class SimpleAdvanceCondition implements AdvanceCondition {
	private String id;
	private QueryOperator operator;
	private Object value;

	public SimpleAdvanceCondition(String id, Object value) {
		this(id, QueryOperator.Equals, value);
	}

	public SimpleAdvanceCondition(String id, QueryOperator operator, Object value) {
		this.id = id;
		this.operator = operator;
		this.value = value;
	}

	public SimpleAdvanceCondition(JsonObject json) {
		// 验证json结构的有效性
		if (json == null || !(json.containsKey("id") && json.containsKey("operator") && json.containsKey("value")))
			throw new IllegalArgumentException("missing 'id', 'value' or 'operator' mapping, json=" + json);

		this.id = json.getString("id");
		this.operator = QueryOperator.symbolOf(json.getString("operator"));

		// value 处理
		String value = json.getString("value");
		String type = json.containsKey("type") ? json.getString("type") : null;
		this.value = StringUtils.convertValueByType(type, value);
	}

	public SimpleAdvanceCondition(JsonArray item) {
		// 验证 JsonArray 结构的有效性 [id, value[, type, operator]]
		if (item == null || item.size() < 2)
			throw new IllegalArgumentException("error array structure, jsonArray=" + item);

		this.id = item.getString(0);

		// 值
		String value = item.getString(1);
		String type = item.size() > 2 ? item.getString(2) : "string";
		this.value = StringUtils.convertValueByType(type, value);

		// 比较符
		this.operator = item.size() > 3 ? QueryOperator.symbolOf(item.getString(3)) : QueryOperator.Equals;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public QueryOperator getOperator() {
		return operator;
	}

	@Override
	public Object getValue() {
		return value;
	}
}
