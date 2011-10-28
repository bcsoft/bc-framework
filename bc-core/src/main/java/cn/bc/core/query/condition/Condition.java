/**
 * 
 */
package cn.bc.core.query.condition;

import java.util.List;

/**
 * 查询条件
 * 
 * @author dragon
 * 
 */
public interface Condition {
	/**
	 * 条件表达式,null用""代替
	 * 
	 * @return
	 */
	String getExpression();

	/**
	 * 带别名的条件表达式,null用""代替
	 * 
	 * @param alias
	 *            别名,为空代表不使用别名
	 * @return
	 */
	String getExpression(String alias);

	/**
	 * @return 条件参数
	 */
	List<Object> getValues();
}
