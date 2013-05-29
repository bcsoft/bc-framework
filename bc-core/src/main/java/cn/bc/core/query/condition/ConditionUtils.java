/**
 * 
 */
package cn.bc.core.query.condition;

import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrCondition;
import cn.bc.core.util.StringUtils;

/**
 * 条件工具类
 * 
 * @author dragon
 * 
 */
public class ConditionUtils {
	/**
	 * 生成通过逗号连接的多个值转换为的查询条件的简易方法
	 * 
	 * @param actionFieldValue
	 *            action属性或字段对应的值
	 * @param dbColumnName
	 *            查询数据库时使用的名称，如"select a.id,..."的id列对应为"a.id"
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
	 * @param actionFieldValue
	 *            action属性或字段对应的值
	 * @param dbColumnName
	 *            查询数据库时使用的名称，如"select a.id,..."的id列对应为"a.id"
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
}
