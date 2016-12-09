package cn.bc.core.util;

import cn.bc.BCConstants;
import cn.bc.core.exception.CoreException;
import org.commontemplate.tools.TemplateRenderer;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.FileCopyUtils;

import javax.ws.rs.core.Response;
import java.io.*;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 使用commontemplate的模板工具类
 *
 * @author dragon
 */
public class TemplateUtils {
	/* 模板附件保存的根路径 */
	public static String ROOT_PATH = "/bcdata/template";
	protected static Logger logger = LoggerFactory.getLogger(TemplateUtils.class);

	private TemplateUtils() {
	}

	public void setRootPath(String rootPath) {
		ROOT_PATH = rootPath;
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
		if (null == args)
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
		String pattern = BCConstants.PATTERN_FIND_SPEC_KEYS;
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

	/**
	 * 获取指定编码模板对象的字符串内容信息
	 * <p>
	 * 如果模板为带附件类型的，而且此附件的内容是纯文本类型，则自动读取此附件的内容返回。
	 * 此方法用于解耦对 bc-template 模块 cn.bc.template.service.TemplateService.getContent(String)的依赖
	 * </p>
	 *
	 * @param code 模板编码，如果含字符":"，则进行分拆，前面部分为编码，后面部分为版本号，如果没有字符":"，将获取当前最新版本
	 * @return 如果模板不是纯文本类型或无法查询到，返回 null，否则返回模板内容
	 */
	public static String getContent(String code){
		if (code == null || code.isEmpty()) return null;

		// 解析出编码和版本
		int i = code.indexOf(":");
		String version = null;
		if (i != -1) {
			version = code.substring(i + 1);
			code = code.substring(0, i);
		}

		JdbcTemplate jdbcTemplate = SpringUtils.getBean(JdbcTemplate.class);
		if(jdbcTemplate == null) throw new CoreException("must config JdbcTemplate instance in Spring Context.");

		// 构建查询语句
		String sql = "select tt.is_path as is_attach, t.content as content, t.path as path" +
				"\n  from bc_template t" +
				"\n  inner join bc_template_type tt on tt.id = t.type_id" +
				"\n  where t.code = ?";
		boolean hasVersion = (version != null && !version.isEmpty());
		if(hasVersion) sql += "\n  and t.version_ = ?";
		sql += "\n  order by t.status_ asc, t.version_ desc limit 1";

		try {
			logger.debug("code={}, version={}, sql={}", code, version, sql);
			// 获取查询结果
			Map<String, Object> m;
			if(hasVersion) m = jdbcTemplate.queryForMap(sql, code, version);
			else m = jdbcTemplate.queryForMap(sql, code);

			// 解析处理
			boolean isAttach = (boolean) m.get("is_attach");
			if(!isAttach){
				return (String) m.get("content");
			}else{
				String path = ROOT_PATH + "/"  + m.get("path");
				logger.debug("path={}", path);
				return FileCopyUtils.copyToString(new FileReader(path));
			}
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (FileNotFoundException e) {
			throw new CoreException(e.getMessage(), e);
		} catch (IOException e) {
			throw new CoreException(e.getMessage(), e);
		}
	}

	/**
	 * 将数据渲染为 Excel
	 * @param data 数据
	 * @param excelTemplate Excel 模板
	 * @param out 输出的 Excel 数据流
	 */
	public static void renderExcel(Map<String, Object> data, InputStream excelTemplate, OutputStream out) {
		try {
			// 构建模板参数
			Context context = new Context();
			context.putVar("now", ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)); // 当前时间
			if (data != null) data.forEach((k, v) -> context.putVar(k, v));

			// 生成 Excel
			JxlsHelper.getInstance().processTemplate(excelTemplate, out, context);
		} catch (IOException e) {
			throw new CoreException(e);
		}
	}

	/**
	 * 将数据渲染为 Excel
	 *
	 * @param data          数据
	 * @param excelTemplate Excel 模板
	 * @return 渲染后的 Excel 数据
	 */
	public static byte[] renderExcel(Map<String, Object> data, InputStream excelTemplate) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(1024)) {
			renderExcel(data, excelTemplate, out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new CoreException(e);
		}
	}
}