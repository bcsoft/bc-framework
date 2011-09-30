/**
 * 
 */
package cn.bc.web.formater;

import java.text.MessageFormat;

/**
 * 超链接的格式化
 * <p>
 * 生成类似&lt;a ...&gt;XXXX&lt;/a&gt;的格式
 * </p>
 * 
 * @author dragon
 * 
 */
public abstract class LinkFormater extends AbstractFormater<String> {
	private String pattern;
	private String modultType;

	// public final static String TPL =
	// "<a href=\"{1}\" class=\"bc-link\" data-mtype=\"{2}\">{0}</a>";

	/**
	 * @param pattern
	 *            url格式，如"/bc/user/open?id={0}&name={1}"
	 */
	public LinkFormater(String pattern, String modultType) {
		this.pattern = pattern;
		this.modultType = modultType;
	}

	public String format(Object context, Object value) {
		Object _value;
		if (value instanceof Formater) {
			_value = ((Formater<?>) value).format(context, value);
		} else {
			_value = value;
		}
		Object[] params = getParams(context, _value);
		String href = MessageFormat.format(this.pattern, params);
		String t;
		String tpl = "<a href=\"" + href + "\" class=\"bc-link\" data-mtype=\""
				+ this.modultType + "\"";

		// 任务栏显示的标题
		t = this.getTaskbarTitle(context, value);
		if (t != null)
			tpl += " data-title=\"" + t + "\"";

		// 对话框的id
		t = this.getWinId(context, value);
		if (t != null)
			tpl += " data-mid=\"" + t + "\"";

		t = this.getLinkText(context, value);
		if (t != null)
			tpl += ">" + t + "</a>";
		else
			tpl += ">" + value + "</a>";

		return tpl;
		// return MessageFormat.format(TPL, value,
		// MessageFormat.format(this.pattern, params), modultType);
	}

	/**
	 * 和获取url的格式化参数信息
	 * 
	 * @param context
	 *            上下文
	 * @param value
	 *            参数值
	 * @return
	 */
	public abstract Object[] getParams(Object context, Object value);

	/**
	 * 任务栏显示的标题
	 * 
	 * @param context
	 * @param value
	 * @return
	 */
	public String getTaskbarTitle(Object context, Object value) {
		return null;
	}

	/**
	 * 窗口的id
	 * 
	 * @param context
	 * @param value
	 * @return
	 */
	public String getWinId(Object context, Object value) {
		return null;
	}

	/**
	 * 链接显示的文字
	 * 
	 * @param context
	 * @param value
	 * @return
	 */
	public String getLinkText(Object context, Object value) {
		return null;
	}
}
