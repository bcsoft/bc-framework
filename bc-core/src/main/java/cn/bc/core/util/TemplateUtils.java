package cn.bc.core.util;

import java.text.MessageFormat;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commontemplate.tools.TemplateRenderer;

/**
 * 使用commontemplate的模板工具类
 * 
 * @author dragon
 */
public class TemplateUtils {
	protected static Log logger = LogFactory.getLog(TemplateUtils.class);

	private TemplateUtils() {
	}

	/**
	 * 对字符串进行格式化，格式化的方式为：对源字符串(sourceString)中的{n}(其中n表示数字，从0开始 )进行替换
	 * 
	 * @param tpl
	 *            源字符串
	 * @param args
	 *            所要格式化的参数信息
	 * @return 返回格式化后的字符串
	 */
	public static String format(String tpl, Object[] args) {
		if (tpl == null || tpl.length() == 0)
			return "";
		if (null == args || args.length == 0)
			return tpl;
		return MessageFormat.format(tpl, args);
	}

	/**
	 * 使用commontemplate引擎对字符串进行格式化
	 * <p>
	 * 如源字符串为：国籍=${country}, 姓名=${name}<br>
	 * 则在args中分别添加key为country、name的值，如<br>
	 * args.put("country", "中华人民共和国");<br>
	 * args.put("name", "伏羲氏");<br>
	 * 则返回的值为：国籍=中华人民共和国, 姓名=伏羲氏<br>
	 * 另外，参数也接受类似:obj.property的格式，更多请参考commontemplate的模板语法<br>
	 * http://code.google.com/p/commontemplate/
	 * </p>
	 * 
	 * @param tpl
	 *            所要格式化的字符串模板
	 * @param args
	 *            参数值
	 * @return 返回格式化好的字符串
	 */
	public static String format(String tpl, Map<String, Object> args) {
		if (tpl == null || tpl.length() == 0)
			return "";
		if (null == args || args.isEmpty())
			return tpl;

		TemplateRenderer tplRender = new TemplateRenderer(tpl);
		tplRender.putAll(args);
		return tplRender.evaluate();
	}
}