/**
 *
 */
package cn.bc.template.util;

import cn.bc.core.util.TemplateUtils;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

  public static void formatTo(InputStream is, OutputStream out, Map<String, Object> args) {
    XlsxUtils.formatTo(is, out, args);
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
