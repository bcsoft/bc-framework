/**
 * 
 */
package cn.bc.core.query;


/**
 * @author dragon
 * 
 */
@Deprecated
public interface DBQuery<T extends Object> extends
		Query<T> {
	/** 模糊匹配类型 */
	public enum LikeType {
		Left, Right, Both
	}

	/** 比较符 */
	public enum CompareType {
		/** 大于> */
		GreaterThan,
		/** 大于等于>= */
		GreaterThanOrEqual,
		/** 小于< */
		LessThan,
		/** 小于等于<= */
		LessThanOrEqual;

		public String toSymbol() {
			switch (this) {
			case GreaterThan:
				return ">";
			case GreaterThanOrEqual:
				return ">=";
			case LessThan:
				return "<";
			case LessThanOrEqual:
				return "<=";
			default:
				return this.toString();
			}
		}
	}

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

		public String toSymbol() {
			switch (this) {
			case GreaterThan_LessThan:
				return ">,<";
			case GreaterThan_LessThanOrEqual:
				return ">,<=";
			case GreaterThanOrEqual_LessThan:
				return ">=,<";
			case GreaterThanOrEqual_LessThanOrEqual:
				return ">=,<=";
			default:
				return this.toString();
			}
		}
	}

	/** 排序方式 */
	public enum Direction {
		Asc, Desc;

		public String toSymbol() {
			switch (this) {
			case Asc:
				return "asc";
			case Desc:
				return "desc";
			default:
				return this.toString();
			}
		}
	}

	/**转换条件连接符为and*/
	DBQuery and();

	/**转换条件连接符为or*/
	DBQuery or();

	/**将现有条件用括号括住*/
	DBQuery addParentheses();

	/**在条件后添加左括号*/
	DBQuery addLeftParenthesis();

	/**在条件后添加右括号*/
	DBQuery addRightParenthesis();

	DBQuery addOrderBy(String name, Direction direction);

	DBQuery addEqual(String name, Object value);

	DBQuery addEqual(String[] names, Object[] values);

	DBQuery addNotEqual(String name, Object value);

	DBQuery addNull(String name, Object value);

	DBQuery addNotNull(String name, Object value);

	DBQuery addIn(String name, Object... values);

	DBQuery addNotIn(String name, Object... values);

	DBQuery addLike(String name, Object value, LikeType like);

	DBQuery addRange(String name, Object value, CompareType range);

	DBQuery addRange(String name, Object firstValue, Object secondValue,
			RangeType range);
}
