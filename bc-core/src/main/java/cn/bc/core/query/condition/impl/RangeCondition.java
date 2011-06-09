/**
 * 
 */
package cn.bc.core.query.condition.impl;

import java.util.ArrayList;
import java.util.List;

import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Condition;


/**
 * 范围条件
 * 
 * @author dragon
 * 
 */
public class RangeCondition implements Condition {
	/** 范围比较符 */
	public enum RangeType {
		/** >[value1],<[value2] */
		GreaterThan_LessThan,
		/** >[value1],<=[value2] */
		GreaterThan_LessThanOrEqual,
		/** >=[value1],<[value2] */
		GreaterThanOrEqual_LessThan,
		/** >=[value1],<=[value2] */
		GreaterThanOrEqual_LessThanOrEqual;

		public String[] toSymbol() {
			switch (this) {
			case GreaterThan_LessThan:
				return new String[] { ">", "<" };
			case GreaterThan_LessThanOrEqual:
				return new String[] { ">", "<=" };
			case GreaterThanOrEqual_LessThan:
				return new String[] { ">=", "<" };
			case GreaterThanOrEqual_LessThanOrEqual:
				return new String[] { ">=", "<=" };
			default:
				throw new CoreException("unsupport RangeType:" + toString());
			}
		}
	}

	protected String name;
	protected RangeType rangeType;
	protected List<Object> values = new ArrayList<Object>(1);

	public RangeCondition(String name, Object firstValue, Object secondValue,
			RangeType rangeType) {
		this.name = name;
		this.values.add(firstValue);
		this.values.add(secondValue);
		this.rangeType = rangeType;
	}

	public String getExpression() {
		String[] symbol = this.rangeType.toSymbol();
		return "(" + name + " " + symbol[0] + " ? and " + name + " "
				+ symbol[1] + " ?)";
	}

	public List<Object> getValues() {
		return values;
	}
}
