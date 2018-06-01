/**
 *
 */
package cn.bc.template.util;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dragon
 */
public class XlsxUtilsTest {
  // 加载docx文档
  private InputStream getDocumentInputStream() throws IOException,
    InvalidFormatException {
    return new ClassPathResource("cn/bc/template/excel/xlsxTpl.xlsx")
      .getInputStream();
  }

  @Test
  public void testLoadText() throws Exception {
    String content = XlsxUtils.loadText(this.getDocumentInputStream());
    Assert.assertNotNull(content);
    Assert.assertTrue(content.length() > 0);
    // System.out.println(content);
  }

  @Test
  public void testFindMarkers() throws Exception {
    String content = XlsxUtils.loadText(this.getDocumentInputStream());
    Assert.assertNotNull(content);
    Assert.assertTrue(content.length() > 0);

    List<String> markers = XlsxUtils.findMarkers(this
      .getDocumentInputStream());
    Assert.assertNotNull(markers);
    Assert.assertTrue(markers.size() > 0);
    Collections.sort(markers);
    Assert.assertEquals("[title]", markers.toString());
  }

  @Test
  public void testFormat() throws Exception {
    Map<String, Object> markerValues = new HashMap<String, Object>();
    markerValues.put("title", "标题");

    XSSFWorkbook workbook = XlsxUtils.format(getDocumentInputStream(),
      markerValues);
    Assert.assertNotNull(workbook);

    workbook.write(new FileOutputStream(new File("/t/xlsxTpl.xlsx")));

    // 输出格式化后的文本内容
    // System.out.println(XlsxUtils.loadText(workbook));
  }
}
