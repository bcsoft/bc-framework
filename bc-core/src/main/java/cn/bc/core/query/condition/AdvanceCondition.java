/**
 *
 */
package cn.bc.core.query.condition;

import cn.bc.core.query.QueryOperator;

/**
 * 高级条件配置
 * <p>
 * <p>用于隐藏实际的sql语句，对外仅暴露条件的标识符，由 dao 层进行条件过滤，避免 sql 攻击</p>
 *
 * @author dragon 2016-10-14
 */
public interface AdvanceCondition {
	/**
	 * 标识符
	 */
	String getId();

	/**
	 * 类型
	 */
	QueryOperator getOperator();

	/**
	 * @return 参数值
	 */
	Object getValue();
}
