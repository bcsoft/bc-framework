/**
 * 
 */
package cn.bc.web.formater;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 日期范围的格式化
 * <p>
 * 如输入两个日期信息，默认显示类似“2011-01-01～2011-02-01”的格式
 * </p>
 * 
 * @author dragon
 * 
 */
public abstract class DateRangeFormater extends AbstractFormater<String> {
	private SimpleDateFormat format;

	public DateRangeFormater() {
		// 默认日期格式
		format = new SimpleDateFormat("yyyy-MM-dd");
	}

	/**
	 * @param pattern
	 *            日期格式，如yyy-MM-dd HH:mm:ss
	 */
	public DateRangeFormater(String pattern) {
		format = new SimpleDateFormat(pattern);
	}

	@SuppressWarnings("rawtypes")
	public String format(Object context, Object value) {
		Date fromDate = getFromDate(context, value);
		Date toDate = getToDate(context, value);
		Map contract = (Map) context;
		String strValue = "";
		// 如果日期前需要设值
		if (this.beforeDateStr != null) {
			strValue = (String) contract.get(this.beforeDateStr);
		}
		if (fromDate == null) {
			if (toDate == null) {
				return "";
			} else {
				// 如果使用最后一个日期使用括号
				if (this.toDateUseBracket) {
					if (strValue != null && strValue.length() > 0) {
						return "(" + strValue + ")" + "("
								+ format.format(toDate) + ")";
					} else {
						return "(" + format.format(toDate) + ")";
					}
				} else {
					if (strValue != null && strValue.length() > 0) {
						return "("
								+ strValue
								+ ")"
								+ (this.connector != null ? this.connector
										+ format.format(toDate) : "～"
										+ format.format(toDate));
					} else {
						return (this.connector != null ? this.connector
								+ format.format(toDate) : "～"
								+ format.format(toDate));

					}

				}
			}
		} else {
			if (toDate == null) {
				// 如果日期前存在在值
				if (strValue != null && strValue.length() > 0) {
					return "(" + strValue + ")" + format.format(fromDate)
							+ (!this.useEmptySymbol ? "～" : "");
				} else {
					return format.format(fromDate)
							+ (!this.useEmptySymbol ? "～" : "");
				}
			} else {
				if (this.toDateUseBracket) {
					// 如果日期前存在在值
					if (strValue != null && strValue.length() > 0) {
						return "(" + strValue + ")" + format.format(fromDate)
								+ "(" + format.format(toDate) + ")";
					} else {
						return format.format(fromDate) + "("
								+ format.format(toDate) + ")";
					}
				} else {
					// 如果日期前存在在值
					if (strValue != null && strValue.length() > 0) {
						return "("
								+ strValue
								+ ")"
								+ (this.connector != null ? format
										.format(fromDate)
										+ this.connector
										+ format.format(toDate) : format
										.format(fromDate)
										+ "～"
										+ format.format(toDate));
					} else {
						return (this.connector != null ? format
								.format(fromDate)
								+ this.connector
								+ format.format(toDate) : format
								.format(fromDate) + "～" + format.format(toDate));
					}
				}
			}
		}
	}

	public Date getFromDate(Object context, Object value) {
		// 默认将传入的值当作起始日期值
		return (Date) value;
	}

	/**
	 * 从上下文获取结束日期的值
	 * 
	 * @param context
	 * @param value
	 * @return
	 */
	public abstract Date getToDate(Object context, Object value);

	private boolean useEmptySymbol;// 是否使用空连接符
	private boolean toDateUseBracket; // 最后一个日期是否使用括号
	private String connector;// 两个日期之间的连接符
	private String beforeDateStr;// 视图中mapRow的key值

	/**
	 * 当没有结束日期时，是否使用"~"号，默认为使用，true为不使用
	 * 
	 * @param useEmptySymbol
	 */
	public DateRangeFormater setUseEmptySymbol(boolean useEmptySymbol) {
		this.useEmptySymbol = useEmptySymbol;
		return this;
	}

	/**
	 * 最后一个日期是否使用括号
	 * 
	 * @param toDateUseBracket
	 * @return
	 */
	public DateRangeFormater setToDateUseBracket(boolean toDateUseBracket) {
		this.toDateUseBracket = toDateUseBracket;
		return this;
	}

	/**
	 * 日期之间使用的连接符如"~"或","
	 * 
	 * @param connector
	 *            如"~"或","
	 * @return
	 */
	public DateRangeFormater setConnector(String connector) {
		this.connector = connector;
		return this;
	}

	/**
	 * 日期前的显示值：如 (张三)2011-01-01～2011-02-01
	 * 
	 * @param beforeDateStr
	 *            视图中mapRow的key值(不支持日期格式的key值)
	 * @return
	 */
	public DateRangeFormater setBeforeDateStr(String beforeDateStr) {
		this.beforeDateStr = beforeDateStr;
		return this;
	}

}
