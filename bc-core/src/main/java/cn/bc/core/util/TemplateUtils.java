package cn.bc.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	/**
	 * 获取文件中的${XXXX}占位标记的键名列表
	 * <p>
	 * 出现的任何异常都将导致返回null值
	 * </p>
	 * 
	 * @param is
	 *            纯文本文件流
	 * @return
	 */
	public static List<String> findMarkers(InputStream is) {
		if (is == null)
			return null;
		return TemplateUtils.findMarkers(loadText(is));
	}

	/**
	 * 获取纯文本文件(UTF-8编码)的文本内容
	 * 
	 * @param is
	 * @return
	 */
	public static String loadText(InputStream is) {
		return loadText(is, "UTF-8");
	}

	/**
	 * 获取纯文本文件流的文本内容，任何异常将导致返回null
	 * 
	 * @param is
	 * @param charsetName
	 *            文件编码
	 * @return
	 */
	public static String loadText(InputStream is, String charsetName) {
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(is, charsetName);
			BufferedReader br = new BufferedReader(reader);
			StringBuffer s = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				s.append(line);
			}
			return s.toString();
		} catch (Exception e) {
			if (logger.isWarnEnabled())
				logger.warn("loadText error:" + e.getMessage());
			return null;
		}
	}

	/**
	 * 获取文件中的${XXXX}占位标记的键名列表
	 * <p>
	 * 出现的任何异常都将导致返回null值
	 * </p>
	 * 
	 * @return
	 */
	public static List<String> findMarkers(String source) {
		List<String> markers = new ArrayList<String>();
		if (source == null)
			return markers;
		String pattern = "(?<=\\$\\{)([\\w\u4e00-\u9fa5]+)(?=\\})";
		if (logger.isDebugEnabled()) {
			logger.debug("pattern=" + pattern);
		}
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(source);
		String key;
		while (m.find()) {
			key = m.group();
			if (!markers.contains(key)) {
				markers.add(key);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("markers=" + markers);
		}
		return markers;
	}

	/**
	 * 将字符串写入到流
	 * 
	 * @param source
	 *            要写入到流中的字符串
	 * @param out
	 * @throws IOException
	 */
	public static void copy(String source, OutputStream out) throws IOException {
		if (out == null || source == null)
			return;
		try {
			out.write(source.getBytes());
			out.flush();
		} finally {
			try {
				out.close();
			} catch (IOException ex) {
			}
		}
	}
}