/**
 *
 */
package cn.bc.template.util;

import cn.bc.core.util.TemplateUtils;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Xls文件处理工具类
 *
 * @author dragon
 */
public class XlsUtils {
	private static Logger logger = LoggerFactory.getLogger(XlsUtils.class);

	private XlsUtils() {
	}

	/**
	 * 获取文件无格式的文本内容
	 * <p>
	 * 注意如果文件内含表格，其内容将在最后；加载出现的任何异常都将导致返回null值
	 * </p>
	 *
	 * @param is
	 * @return
	 */
	public static String loadText(InputStream is) {
		if (is == null)
			return null;
		return loadText(loadDocument(is));
	}

	/**
	 * 获取文件无格式的文本内容
	 * <p>
	 * 注意如果文件内含表格，其内容将在最后；加载出现的任何异常都将导致返回null值
	 * </p>
	 *
	 * @param workbook
	 * @return
	 */
	public static String loadText(HSSFWorkbook workbook) {
		if (workbook == null)
			return null;
		ExcelExtractor extractor = new ExcelExtractor(workbook);
		String content = extractor.getText();
		if (logger.isDebugEnabled()) {
			logger.debug("====content start====");
			logger.debug(content);
			logger.debug("====content end====");
		}
		return content;
	}

	/**
	 * 获取文件中的${XXXX}占位标记的键名列表
	 * <p>
	 * 出现的任何异常都将导致返回null值
	 * </p>
	 *
	 * @return
	 */
	public static List<String> findMarkers(InputStream is) {
		if (is == null)
			return null;
		return TemplateUtils.findMarkers(loadText(is));
	}

	/**
	 * 格式化文档
	 *
	 * @param is
	 * @param markerValues 格式化参数
	 * @return 返回格式化后的文档
	 */
	public static HSSFWorkbook format(InputStream is,
	                                  Map<String, Object> markerValues) {
		HSSFWorkbook document = loadDocument(is);
		return format(document, markerValues);
	}

	/**
	 * 格式化文档：使用jxls组件的XLSTransformer
	 *
	 * @param workbook
	 * @param markerValues 格式化参数
	 * @return 返回格式化后的文档
	 */
	public static HSSFWorkbook format(HSSFWorkbook workbook,
	                                  Map<String, Object> markerValues) {
		if (workbook == null) {
			if (logger.isWarnEnabled())
				logger.warn("format error:document is null");
			return null;
		}

		XLSTransformer transformer = new XLSTransformer();
		transformer.transformWorkbook(workbook, markerValues);
		return workbook;
	}

	// 加载文档
	public static HSSFWorkbook loadDocument(InputStream is) {
		HSSFWorkbook workbook = null;
		try {
			workbook = new HSSFWorkbook(is);
		} catch (IOException e) {
			if (logger.isWarnEnabled())
				logger.warn("loadText error:" + e.getMessage());
			return null;
		}
		return workbook;
	}
}
