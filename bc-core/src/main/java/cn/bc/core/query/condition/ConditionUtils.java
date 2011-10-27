/**
 * 
 */
package cn.bc.core.query.condition;

import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
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
	 * @param actionField
	 *            action属性或字段的名称
	 * @param dbColumn
	 *            查询数据库时使用的名称，如"select a.id,..."的id列对应为"a.id"
	 * @return
	 */
	public static Condition toConditionByComma4IntegerValue(String actionField,
			String dbColumn) {
		Condition actionFieldCondition = null;
		if (actionField != null && actionField.trim().length() > 0) {
			String[] ss = actionField.split(",");
			if (ss.length == 1) {
				actionFieldCondition = new EqualsCondition(dbColumn,
						new Integer(ss[0]));
			} else {
				actionFieldCondition = new InCondition(dbColumn,
						StringUtils.stringArray2IntegerArray(ss));
			}
		}
		return actionFieldCondition;
	}

	/**合并多个条件为And条件
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
}
