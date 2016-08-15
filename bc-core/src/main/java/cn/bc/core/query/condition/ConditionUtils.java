/**
 *
 */
package cn.bc.core.query.condition;

import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.impl.*;
import cn.bc.core.util.StringUtils;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Collection;

/**
 * 条件工具类
 *
 * @author dragon
 */
public class ConditionUtils {
	/**
	 * 生成通过逗号连接的多个值转换为的查询条件的简易方法
	 *
	 * @param actionFieldValue action属性或字段对应的值
	 * @param dbColumnName     查询数据库时使用的名称，如"select a.id,..."的id列对应为"a.id"
	 * @return
	 */
	public static Condition toConditionByComma4StringValue(
			String actionFieldValue, String dbColumnName) {
		Condition actionFieldCondition = null;
		if (actionFieldValue != null && actionFieldValue.trim().length() > 0) {
			String[] ss = actionFieldValue.split(",");
			if (ss.length == 1) {
				actionFieldCondition = new EqualsCondition(dbColumnName, ss[0]);
			} else {
				actionFieldCondition = new InCondition(dbColumnName, ss);
			}
		}
		return actionFieldCondition;
	}

	/**
	 * 生成通过逗号连接的多个值转换为的查询条件的简易方法
	 *
	 * @param actionFieldValue action属性或字段对应的值
	 * @param dbColumnName     查询数据库时使用的名称，如"select a.id,..."的id列对应为"a.id"
	 * @return
	 */
	public static Condition toConditionByComma4IntegerValue(
			String actionFieldValue, String dbColumnName) {
		Condition actionFieldCondition = null;
		if (actionFieldValue != null && actionFieldValue.trim().length() > 0) {
			String[] ss = actionFieldValue.split(",");
			if (ss.length == 1) {
				actionFieldCondition = new EqualsCondition(dbColumnName,
						new Integer(ss[0]));
			} else {
				actionFieldCondition = new InCondition(dbColumnName,
						StringUtils.stringArray2IntegerArray(ss));
			}
		}
		return actionFieldCondition;
	}

	/**
	 * 合并多个条件为And条件
	 *
	 * @param conditions
	 * @return
	 */
	public static AndCondition mix2AndCondition(Condition... conditions) {
		AndCondition and = new AndCondition();
		if (conditions != null) {
			for (Condition c : conditions) {
				if (c != null) {
					and.add(c);
				}
			}
		}
		return and.isEmpty() ? null : and;
	}

	/**
	 * 合并多个条件为Or条件
	 *
	 * @param conditions
	 * @return
	 */
	public static OrCondition mix2OrCondition(Condition... conditions) {
		OrCondition and = new OrCondition();
		if (conditions != null) {
			for (Condition c : conditions) {
				if (c != null) {
					and.add(c);
				}
			}
		}
		return and.isEmpty() ? null : and;
	}

	/**
	 * 生成模糊查询条件
	 *
	 * @param query      模糊查询的文本，支持 "A B"(or)、"A+B"(and) 两种特殊模式和自定义 % 的位置
	 * @param fields     模糊查询的字段
	 * @param ignoreCase 是否忽略大小写
	 */
	public static Condition toFuzzyCondition(String query, String[] fields, boolean ignoreCase) {
		if (query == null || fields == null || fields.length == 0) return null;
		query = query.trim();
		if (query.isEmpty()) return null;

		// 用空格分隔多个查询条件值的处理
		String[] values;
		boolean isOr = query.indexOf("+") == -1;
		if (isOr) values = query.split(" ");            // "A B" 查询
		else values = query.split("\\+");              // "A+B" 查询

		// 生成模糊查询条件
		if (isOr) {                                     // "A B" 查询
			// ----(f1 like v1 or f1 like v2 or ..) or (f2 like v1 or f2 like v2 or ..) or ..
			OrCondition or = new OrCondition();
			or.setAddBracket(true);                     // 用括号将多个or条件括住
			for (String field : fields)
				for (String value : values) or.add(createLikeCondition(field, value, ignoreCase));
			return or;
		} else {                                        // "A+B" 查询
			// ----(f1 like v1 or f1 like v2 or ..) and (f2 like v1 or f2 like v2 or ..) and ..
			AndCondition and = new AndCondition();
			and.setAddBracket(true);                    // 用括号将多个and条件括住

			OrCondition or;
			for (String value : values) {
				or = new OrCondition();
				or.setAddBracket(true);
				and.add(or);
				for (String field : fields) or.add(createLikeCondition(field, value, ignoreCase));
			}

			return and;
		}
	}

	/**
	 * 生成查询条件（模糊查询或纯高级查询）
	 *
	 * @param query       查询配置，如果为json数组字符串则按高级查询处理，否则按模糊查询处理
	 * @param fuzzyFields 模糊查询的字段列表
	 */
	public static Condition toCondition(String query, String[] fuzzyFields) {
		return toCondition(query, fuzzyFields, false);
	}

	/**
	 * 生成查询条件（混合模糊查询和高级查询）
	 *
	 * @param query       查询配置，如果为json数组字符串则按高级查询处理，否则按模糊查询处理
	 * @param fuzzyFields 模糊查询的字段列表
	 * @param ignoreCase  模糊查询条件是否忽略大小写
	 */
	public static Condition toCondition(String query, String[] fuzzyFields, boolean ignoreCase) {
		if (query == null) return null;
		query = query.trim();
		if (query.isEmpty()) return null;

		// 非高级查询的处理
		boolean isAdvance = query.startsWith("[");
		if (!isAdvance) return toFuzzyCondition(query, fuzzyFields, ignoreCase);

		// 高级查询的处理: [{id, value, operator[, type][, label]}]
		JsonArray qs = Json.createReader(new StringReader(query)).readArray();
		if (qs.isEmpty()) return null;
		JsonObject q;
		AndCondition and = new AndCondition();
		and.setAddBracket(true);
		for (int i = 0; i < qs.size(); i++) {
			q = qs.getJsonObject(i);
			if ("_fuzzy_".equals(q.getString("id"))) {      // 模糊查询
				and.add(toFuzzyCondition(q.getString("value"), fuzzyFields, ignoreCase));
			} else {                                        // 高级查询
				and.add(toCondition(q.getString("id"), q.getString("operator"), q.getString("value"),
						q.containsKey("type") ? q.getString("type") : null));
			}
		}

		return and;
	}

	private static Condition toCondition(String id, String operator, String value, String type) {
		Object v = StringUtils.convertValueByType(type, value);
		Condition c;
		if ("=".equals(operator)) {
			c = new EqualsCondition(id, v);
		} else if (">".equals(operator)) {
			c = new GreaterThanCondition(id, v);
		} else if (">=".equals(operator)) {
			c = new GreaterThanOrEqualsCondition(id, v);
		} else if ("<".equals(operator)) {
			c = new LessThanCondition(id, v);
		} else if ("<=".equals(operator)) {
			c = new LessThanOrEqualsCondition(id, v);
		} else if ("<>".equals(operator) || "!=".equals(operator)) {
			c = new NotEqualsCondition(id, v);
		} else if ("in".equals(operator)) {
			if (v instanceof Collection) c = new InCondition(id, (Collection<Object>) v);
			else if (v instanceof Serializable[]) c = new InCondition(id, (Serializable[]) v);
			else throw new CoreException("un support value type: " + value.getClass() +
						" (id=" + id + ", operator=" + operator + ", value=" + value + ", type=" + type + ")");
		} else {
			throw new CoreException("un support operator: " + operator + " (id=" + id + ", value=" + value + ", type=" + type + ")");
		}
		return c;
	}

	/**
	 * 创建 like 条件
	 * <p>自动根据值是否在首末包含%符号来生成相应的 Like 条件</p>
	 *
	 * @param field      字段
	 * @param value      值
	 * @param ignoreCase 是否忽略大小写
	 */
	public static Condition createLikeCondition(String field, String value, boolean ignoreCase) {
		boolean s = value.startsWith("%");
		boolean e = value.endsWith("%");
		if (s && !e) {
			return new LikeRightCondition(field, value, ignoreCase);
		} else if (!s && e) {
			return new LikeLeftCondition(field, value, ignoreCase);
		} else {
			return new LikeCondition(field, value, ignoreCase);
		}
	}
}
