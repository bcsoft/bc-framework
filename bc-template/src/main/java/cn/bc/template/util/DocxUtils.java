/**
 * 
 */
package cn.bc.template.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;

import cn.bc.BCConstants;
import cn.bc.core.util.TemplateUtils;

/**
 * Docx文件处理工具类
 * 
 * @author dragon
 * 
 */
public class DocxUtils {
	protected static Log logger = LogFactory.getLog(DocxUtils.class);
	private static boolean useFreeMarker = true;

	private DocxUtils() {
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
	 * @param document
	 * @return
	 */
	public static String loadText(XWPFDocument document) {
		if (document == null)
			return null;
		XWPFWordExtractor docxExtractor = new XWPFWordExtractor(document);
		String content = docxExtractor.getText();
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
	 * @param markerValues
	 *            格式化参数
	 * @return 返回格式化后的文档
	 */
	public static XWPFDocument format(InputStream is,
			Map<String, Object> markerValues) {
		XWPFDocument document = loadDocument(is);
		return format(document, markerValues);
	}

	/**
	 * 格式化文档
	 * 
	 * @param document
	 * @param markerValues
	 *            格式化参数
	 * @return 返回格式化后的文档
	 */
	public static XWPFDocument format(XWPFDocument document,
			Map<String, Object> markerValues) {
		if (document == null) {
			if (logger.isWarnEnabled())
				logger.warn("format error:document is null");
			return null;
		}

		String pattern;
		if (useFreeMarker) {
			pattern = BCConstants.PATTERN_FIND_SPEC_KEYS;
		} else {
			pattern = "";
			int i = 0;
			for (String k : markerValues.keySet()) {
				if (i > 0)
					pattern += "|";
				pattern += "(?<=\\$\\{)" + k;
				if (!useFreeMarker) {
					pattern += "(?=\\})";
				}
				i++;
			}
		}

		if (logger.isDebugEnabled())
			logger.debug("pattern=" + pattern);
		Pattern _pattern = Pattern.compile(pattern);

		// 页眉的处理
		List<XWPFHeader> headers = document.getHeaderList();
		for (XWPFHeader header : headers) {
			for (XWPFParagraph p : header.getListParagraph()) {
				formatParagraph(p, markerValues, _pattern);
			}
		}

		// 页脚的处理
		List<XWPFFooter> footers = document.getFooterList();
		for (XWPFFooter footer : footers) {
			for (XWPFParagraph p : footer.getListParagraph()) {
				formatParagraph(p, markerValues, _pattern);
			}
		}

		// 段落的处理
		Iterator<XWPFParagraph> ps = document.getParagraphsIterator();
		while (ps.hasNext()) {
			formatParagraph(ps.next(), markerValues, _pattern);
		}

		// 内嵌表格的处理
		formatTables(document.getTables(), markerValues, _pattern);

		return document;
	}

	/**
	 * @param document
	 * @param markerValues
	 * @param _pattern
	 */
	private static void formatTables(List<XWPFTable> tables,
			Map<String, Object> markerValues, Pattern _pattern) {
		if (tables == null || tables.isEmpty())
			return;

		for (XWPFTable table : tables) {
			// System.out.println("table=" + table.getText());
			for (XWPFTableRow row : table.getRows()) {
				// System.out.println("row=" + row.toString());
				for (XWPFTableCell cell : row.getTableCells()) {
					// System.out.println("cell=" + cell.getText());
					for (XWPFParagraph paragraph : cell.getParagraphs()) {
						// System.out.println("paragraph=" +
						// paragraph.getText());
						formatParagraph(paragraph, markerValues, _pattern);
					}

					// 单元格中再嵌套表格的处理
					List<XWPFTable> cellTables = cell.getTables();
					if (cellTables != null && !cellTables.isEmpty()) {
						formatTables(cellTables, markerValues, _pattern);
					}
				}
			}
		}
	}

	// 加载文档
	public static XWPFDocument loadDocument(InputStream is) {
		XWPFDocument docx = null;
		try {
			docx = new XWPFDocument(is);
		} catch (IOException e) {
			if (logger.isWarnEnabled())
				logger.warn("loadText error:" + e.getMessage());
			return null;
		}
		return docx;
	}

	// 格式化一个段落
	public static void formatParagraph(XWPFParagraph paragraph,
			Map<String, Object> markerValues, Pattern p) {
		Matcher m;
		String k;
		String target;
		for (XWPFRun run : paragraph.getRuns()) {
			// System.out.println("run=" + run.toString());
			for (CTText t : run.getCTR().getTList()) {
				// System.out.println("s=" + t.getStringValue());
				m = p.matcher(t.getStringValue());
				if (m.find()) {
					k = m.group();
					if (logger.isDebugEnabled())
						logger.debug("k=" + k + ",s=" + t.getStringValue());
					if (useFreeMarker) {
						target = FreeMarkerUtils.format(t.getStringValue(),
								markerValues);
					} else {
						target = t.getStringValue().replaceAll(
								"\\$\\{" + k + "\\}",
								markerValues.get(k) != null ? markerValues.get(
										k).toString() : "");
					}
					t.setStringValue(target);
				}
				if (m.find()) {
					logger.warn("formatParagraph TODO: more than one matcher.");
				}
			}
		}
	}
}
