/**
 * 
 */
package cn.bc.web.formater;

import java.text.MessageFormat;

import cn.bc.core.util.StringUtils;

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
	protected String urlPattern;
	protected String moduleKey;

	public LinkFormater() {
	}

	/**
	 * @param urlPattern
	 *            url格式，不要包含上下文路径，如"/bc/user/open?id={0}&name={1}"
	 * @param moduleKey
	 *            所链接到模块的标识键
	 */
	public LinkFormater(String urlPattern, String moduleKey) {
		this.urlPattern = urlPattern;
		this.moduleKey = moduleKey;
	}

	public String format(Object context, Object value) {
		Object _value;
		if (value instanceof Formater) {
			_value = ((Formater<?>) value).format(context, value);
		} else {
			_value = value;
		}
		String label = this.getLinkText(context, value);
		if (label != null && label.length() > 0) {
			Object[] params = getParams(context, _value);
			String href = MessageFormat.format(this.urlPattern, params);
			String t;
			String tpl = "<a href=\"" + href
					+ "\" class=\"bc-link\" data-mtype=\"" + this.moduleKey
					+ "\"";

			// 任务栏显示的标题
			t = this.getTaskbarTitle(context, value);
			if (t != null)
				tpl += " data-title=\"" + t + "\"";

			// 对话框的id
			t = this.getWinId(context, value);
			if (t != null)
				tpl += " data-mid=\"" + t + "\"";

			tpl += ">" + label + "</a>";
			return tpl;
		} else {
			return "&nbsp;";
		}
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
	 * 链接显示的文字
	 * 
	 * @param context
	 * @param value
	 * @return
	 */
	public String getLinkText(Object context, Object value) {
		return StringUtils.toString(value);
	}

	/**
	 * 任务栏显示的标题
	 * 
	 * @param context
	 * @param value
	 * @return
	 */
	public String getTaskbarTitle(Object context, Object value) {
		return this.getLinkText(context, value);
	}

	/**
	 * 窗口的id
	 * 
	 * @param context
	 * @param value
	 * @return
	 */
	public String getWinId(Object context, Object value) {
		return this.moduleKey + value;
	}
}
