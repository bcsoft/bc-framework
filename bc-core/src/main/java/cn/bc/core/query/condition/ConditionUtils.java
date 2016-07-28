/**
 *
 */
package cn.bc.core.query.condition;

import cn.bc.core.query.condition.impl.*;
import cn.bc.core.util.StringUtils;

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
	 * @param search     模糊查询的文本，支持 "A B"(or)、"A+B"(and) 两种特殊模式和自定义 % 的位置
	 * @param fields     模糊查询的字段
	 * @param ignoreCase 是否忽略大小写
	 */
	public static Condition fuzzySearch(String search, String[] fields, boolean ignoreCase) {
		if (search == null || search.isEmpty() || fields == null || fields.length == 0)
			return null;
		search = search.trim();

		// 用空格分隔多个查询条件值的处理
		String[] values;
		boolean isOr = search.indexOf("+") == -1;
		if (isOr) values = search.split(" ");            // "A B" 查询
		else values = search.split("\\+");              // "A+B" 查询

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
