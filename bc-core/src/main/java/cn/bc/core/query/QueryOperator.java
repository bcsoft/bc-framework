package cn.bc.core.query;

/**
 * 查询条件的比较符
 * 
 * @author dragon
 * 
 */
public enum QueryOperator {
	/** 大于> */
	GreaterThan,
	/** 大于等于>= */
	GreaterThanOrEquals,
	/** 小于< */
	LessThan,
	/** 小于等于<= */
	LessThanOrEquals,
	/** 等于= */
	Equals,
	/** 不等于!= */
	NotEquals,
	/** like */
	Like,
	/** like left */
	LikeLeft,
	/** like right */
	LikeRight,
	/** in */
	In,
	/** not in */
	NotIn,
	/** is null */
	IsNull,
	/** is not null */
	IsNotNull;

	public String toSymbol() {
		switch (this) {
		case GreaterThan:
			return ">";
		case GreaterThanOrEquals:
			return ">=";
		case LessThan:
			return "<";
		case LessThanOrEquals:
			return "<=";
		case Equals:
			return "=";
		case NotEquals:
			return "!=";
		case Like:
			return "like";
		case LikeLeft:
			return "like";
		case LikeRight:
			return "like";
		case IsNull:
			return "is null";
		case IsNotNull:
			return "is not null";
		case In:
			return "in";
		case NotIn:
			return "not in";
		default:
			return this.toString();
		}
	}

}
