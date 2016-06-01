/**
 *
 */
package cn.bc.web.formater;

import java.text.DecimalFormat;

/**
 * 货币格式化
 *
 * @author dragon
 */
public class CurrencyFormatter extends AbstractFormater<String> {
	/**
	 * 对齐方式
	 */
	public enum Align {
		/**
		 * 居左
		 */
		Left("left"),

		/**
		 * 居右
		 */
		Right("right"),

		/**
		 * 居中
		 */
		Center("center"),

		/**
		 * 未设置
		 */
		None("none");

		private final String value;

		Align(String value) {
			this.value = value;
		}

		public String value() {
			return value;
		}
	}

	private static String COMMON = "##,###,###,##0";
	private DecimalFormat formatter;
	private Align align = Align.None;
	private String pattern;
	private String precision = ".00";

	public CurrencyFormatter() {
		pattern = createPattern("¥", precision);        // 默认人民币符号精确到分
		formatter = new DecimalFormat(pattern);
	}

	/**
	 * @param symbol    货币符号
	 * @param precision 小数精度
	 */
	public CurrencyFormatter(String symbol, int precision) {
		this.precision = precision > 0 ? "." + new String(new char[precision]).replace("\0", "0") : "";
		this.pattern = createPattern(symbol, this.precision);
		this.formatter = new DecimalFormat(pattern);
	}

	/**
	 * @param symbol 货币符号
	 */
	public CurrencyFormatter(String symbol) {
		this(symbol, 2);
	}

	/**
	 * @param precision 小数精度
	 */
	public CurrencyFormatter(int precision) {
		this("¥", precision);
	}

	public CurrencyFormatter setAlign(Align align) {
		this.align = align;
		return this;
	}

	/**
	 * 构建金额格式化模式
	 *
	 * @param symbol    金额前缀符号
	 * @param precision 小数精度
	 * @return 如 "¥ ##,###,###,##0.00;¥ -##,###,###,##0.00"
	 */
	public static String createPattern(String symbol, int precision) {
		return createPattern(symbol, precision > 0 ? "." + new String(new char[precision]).replace("\0", "0") : null);
	}

	/**
	 * 构建金额格式化模式
	 *
	 * @param symbol    金额前缀符号
	 * @param precision 小数精度, 格式为 ".00"
	 * @return 如 "¥ ##,###,###,##0.00;¥ -##,###,###,##0.00"
	 */
	public static String createPattern(String symbol, String precision) {
		if (precision != null) {// "##,###,###,##0"、"##,###,###,##0.00"、"¥ ##,###,###,##0;¥ -##,###,###,##0"、"¥ ##,###,###,##0.00;¥ -##,###,###,##0.00"
			return symbol == null ? COMMON : symbol + " " + COMMON + precision + ";" + symbol + " -" + COMMON + precision;
		} else { // "##,###,###,##0"、"##,###,###,##0.00"、"¥ ##,###,###,##0"、"¥ ##,###,###,##0.00"
			return symbol == null ? COMMON : symbol + " " + COMMON + ";" + symbol + " -" + COMMON;
		}
	}

	public String format(Object context, Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof Number || value instanceof String) {
			if (value instanceof String) {
				try {
					value = new Double(value.toString());
				} catch (NumberFormatException e) {
					return (String) value;
				}
			}
			if (align != Align.None)
				return "<div style=\"text-align:" + align.value() + "\">" + formatter.format(value) + "</div>";
			else
				return formatter.format(value);
		} else {
			return value.toString();
		}
	}

	@Override
	public String getExportText(Object context, Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof Number || value instanceof String) {
			if (value instanceof String) {
				try {
					value = new Double(value.toString());
				} catch (NumberFormatException e) {
					return (String) value;
				}
			}
			return new DecimalFormat("0" + precision).format(value);
		} else {
			return value.toString();
		}
	}
}