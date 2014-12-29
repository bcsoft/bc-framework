/**
 * 
 */
package cn.bc.template.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import cn.bc.core.exception.CoreException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 使用FreeMarker的模板工具类
 * 
 * @author dragon
 * 
 */
public class FreeMarkerUtils {
	protected static Log logger = LogFactory.getLog(FreeMarkerUtils.class);

	private FreeMarkerUtils() {
	}

	/**
	 * 使用FreeMarker引擎对字符串进行格式化
	 * <p>
	 * 如源字符串为：国籍=${country}, 姓名=${name}<br>
	 * 则在args中分别添加key为country、name的值，如<br>
	 * args.put("country", "中华人民共和国");<br>
	 * args.put("name", "伏羲氏");<br>
	 * 则返回的值为：国籍=中华人民共和国, 姓名=伏羲氏<br>
	 * 另外，参数也接受类似:obj.property的格式，更多请参考FreeMarker的模板语法http://freemarker.org/
	 * </p>
	 * 
	 * @param tpl
	 *            所要格式化的字符串模板
	 * @param args
	 *            参数值
	 * @return 返回格式化好的字符串
	 * @throws Exception
	 */
	public static String format(String tpl, Map<String, Object> args) {
		return format(tpl, (Object) args);
	}

	public static String format(String tpl, Object args) {
		if (tpl == null || tpl.length() == 0)
			return "";
		// if (null == args || args.isEmpty())
		// return tpl;

		// 构建模板
		try {
			freemarker.template.Template template = new freemarker.template.Template(
					"innerName", new StringReader(tpl), null);

			// 合并数据模型与模板
			Writer out = new StringWriter();
			template.process(args, out);
			out.flush();
			out.close();

			return out.toString();
		} catch (Exception e) {
			throw new CoreException(e.getMessage(), e);
			//logger.warn(e.getMessage(), e);
			//return null;
		}
	}
}
