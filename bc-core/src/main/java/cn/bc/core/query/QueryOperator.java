package cn.bc.core.query;

/**
 * 查询条件的比较符
 *
 * @author dragon
 */
public enum QueryOperator {
	/**
	 * 大于>
	 */
	GreaterThan(">", "大于"),
	/**
	 * 大于等于>=
	 */
	GreaterThanOrEquals(">=", "大于等于"),
	/**
	 * 小于
	 */
	LessThan("<", "小于"),
	/**
	 * 小于等于<=
	 */
	LessThanOrEquals("<=", "小于等于"),
	/**
	 * 等于=
	 */
	Equals("=", "等于"),
	/**
	 * 不等于!=
	 */
	NotEquals("!=", "不等于"),
	/**
	 * like
	 */
	Like("like", "包含"),
	/**
	 * like left
	 */
	LikeLeft("like", "开头包含", "like left"),
	/**
	 * like right
	 */
	LikeRight("like", "结尾包含", "like right"),
	/**
	 * ilike
	 */
	iLike("ilike", "包含(忽略大小写)"),
	/**
	 * ilike left
	 */
	iLikeLeft("ilike", "开头包含(忽略大小写)", "ilike left"),
	/**
	 * ilike right
	 */
	iLikeRight("ilike", "结尾包含(忽略大小写)", "ilike right"),
	/**
	 * in
	 */
	In("in", "在此范围内"),
	/**
	 * not in
	 */
	NotIn("not in", "不在此范围内"),
	/**
	 * is null
	 */
	IsNull("is null", "为空"),
	/**
	 * is not null
	 */
	IsNotNull("is not null", "不为空"),
	/**
	 * [X, Y]
	 */
	Range("[]", "大于等于 X 并且小于等于 Y"),
	/**
	 * [X, Y)
	 */
	RangeGteLt("[)", "大于等于 X 并且小于 Y"),
	/**
	 * (X, Y]
	 */
	RangeGtLte("(]", "大于 X 并且小于等于 Y"),
	/**
	 * (X, Y)
	 */
	RangeGtLt("()", "大于 X 并且小于 Y");

	private final String symbol;
	private final String label;
	private final String _symbol;

	QueryOperator(String symbol, String label) {
		this(symbol, label, symbol);
	}

	QueryOperator(String symbol, String label, String _symbol) {
		this.symbol = symbol;
		this.label = label;
		this._symbol = _symbol;
	}

	public String symbol() {
		return symbol;
	}

	/**
	 * 字符值转换为权举值
	 *
	 * @throws IllegalArgumentException 如果指定的值没有对应的权举值
	 */
	public static QueryOperator symbolOf(String symbol) {
		for (QueryOperator t : QueryOperator.values()) {
			if (t.symbol().equals(symbol)) return t;
		}
		throw new IllegalArgumentException("un support QueryOperator symbol: " + symbol);
	}

	/**
	 * 转换为查询语句
	 *
	 * @param key       查询的关键字
	 * @param firstName 开始占位符的名称
	 * @param lastName  结束占位符的名称
	 * @return 查询语句
	 */
	public String toRangeQuery(String key, String firstName, String lastName) {
		if (key == null || key.isEmpty()) throw new IllegalArgumentException("key should not be null or empty.");
		if ((firstName == null || firstName.isEmpty()) && (lastName == null || lastName.isEmpty()))
			throw new IllegalArgumentException("at lease has one name: firstName=" + firstName + ", lastName=" + lastName);

		if (this == Range || this == RangeGteLt || this == RangeGtLte || this == RangeGtLt) {
			StringBuffer q = new StringBuffer();
			if (firstName != null && !firstName.isEmpty())
				q.append(key + " " + (symbol().startsWith("[") ? ">=" : ">") + " " + firstName);
			if (lastName != null && !lastName.isEmpty()) {
				if (q.length() > 0) q.append(" and ");
				q.append(key + " " + (symbol().endsWith("]") ? "<=" : "<") + " " + lastName);
			}
			return q.toString();
		} else {
			throw new UnsupportedOperationException("only support Range Operator.");
		}
	}

	/**
	 * 转换为查询语句
	 *
	 * @param key  查询的关键字
	 * @param name 占位符名称
	 * @return 查询语句
	 */
	public String toQuery(String key, String name) {
		if (this == Range || this == RangeGteLt || this == RangeGtLte || this == RangeGtLt) {
			throw new UnsupportedOperationException("only support not Range Operator.");
		} else {
			return key + " " + symbol() + " " + name;
		}
	}
}
