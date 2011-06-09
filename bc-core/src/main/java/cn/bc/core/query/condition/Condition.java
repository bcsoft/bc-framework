/**
 * 
 */
package cn.bc.core.query.condition;

import java.util.List;



/**
 * 查询条件
 * @author dragon
 *
 */
public interface Condition{
	/**
	 * @return 条件表达式,null用""代替
	 */
	String getExpression();
	
	/**
	 * @return 条件参数
	 */
	List<Object> getValues();
}
