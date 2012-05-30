/**
 * 
 */
package cn.bc.web.formater;

import java.text.SimpleDateFormat;
import java.util.Date;

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

	public String format(Object context, Object value) {
		Date fromDate = getFromDate(context, value);
		Date toDate = getToDate(context, value);
		if (fromDate == null) {
			if (toDate == null) {
				return "";
			} else {
				return (this.connector != null ? this.connector
						+ format.format(toDate) : "～" + format.format(toDate));
			}
		} else {
			if (toDate == null) {
				return format.format(fromDate)
						+ (!this.useEmptySymbol ? "～" : "");
			} else {
				return (this.connector != null ? format.format(fromDate)
						+ this.connector + format.format(toDate) : format
						.format(fromDate) + "～" + format.format(toDate));
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

	private boolean useEmptySymbol;
	private String connector;

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
	 * 日期之间使用的连接符如"~"或","
	 * 
	 * @param connector
	 * @return
	 */
	public DateRangeFormater setConnector(String connector) {
		this.connector = connector;
		return this;
	}

}
