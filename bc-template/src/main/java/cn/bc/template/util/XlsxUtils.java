/**
 *
 */
package cn.bc.template.util;

import cn.bc.core.util.TemplateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Xlsx文件处理工具类
 *
 * @author dragon
 */
public class XlsxUtils {
  private static Logger logger = LoggerFactory.getLogger(XlsxUtils.class);

  private XlsxUtils() {
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
  public static String loadText(XSSFWorkbook workbook) {
    if (workbook == null)
      return null;
    XSSFExcelExtractor extractor = new XSSFExcelExtractor(workbook);
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
  public static XSSFWorkbook format(InputStream is,
                                    Map<String, Object> markerValues) {
    XSSFWorkbook document = loadDocument(is);
    return format(document, markerValues);
  }

  /**
   * 格式化文档
   *
   * @param workbook
   * @param markerValues 格式化参数
   * @return 返回格式化后的文档
   */
  public static XSSFWorkbook format(XSSFWorkbook workbook,
                                    Map<String, Object> markerValues) {
    if (workbook == null) {
      if (logger.isWarnEnabled())
        logger.warn("format error:document is null");
      return null;
    }

    String pattern = "";
    int i = 0;
    for (String k : markerValues.keySet()) {
      if (i > 0)
        pattern += "|";
      pattern += "(?<=\\$\\{)" + k + "(?=\\})";
      i++;
    }
    if (logger.isDebugEnabled())
      logger.debug("pattern=" + pattern);
    Pattern _pattern = Pattern.compile(pattern);

    // sheet的处理
    XSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
    formatSheet(sheet, markerValues, _pattern);

    return workbook;
  }

  // 加载文档
  public static XSSFWorkbook loadDocument(InputStream is) {
    XSSFWorkbook workbook = null;
    try {
      workbook = new XSSFWorkbook(is);
    } catch (IOException e) {
      if (logger.isWarnEnabled())
        logger.warn("loadText error:" + e.getMessage());
      return null;
    }
    return workbook;
  }

  // 格式化一个工作表
  public static void formatSheet(XSSFSheet sheet,
                                 Map<String, Object> markerValues, Pattern pattern) {
    Iterator<Row> rows = sheet.rowIterator();
    while (rows.hasNext()) {
      formatRow(rows.next(), markerValues, pattern);
    }
  }

  // 格式化一行
  private static void formatRow(Row row, Map<String, Object> markerValues,
                                Pattern pattern) {
    Iterator<Cell> cells = row.cellIterator();
    while (cells.hasNext()) {
      formatCell(cells.next(), markerValues, pattern);
    }
  }

  // 格式化一个单元格
  private static void formatCell(Cell cell, Map<String, Object> markerValues,
                                 Pattern pattern) {
    String s = cell.getStringCellValue();
    Matcher m;
    String k;
    // System.out.println("cell=" + s);
    m = pattern.matcher(s);
    if (m.find()) {
      k = m.group();
      if (logger.isDebugEnabled())
        logger.debug("k=" + k + ",s=" + s);
      cell.setCellValue(s.replaceAll("\\$\\{" + k + "\\}", markerValues
        .get(k) != null ? markerValues.get(k).toString() : ""));
    }
    if (m.find()) {
      logger.warn("formatCell TODO: more than one matcher.");
    }
  }
}
