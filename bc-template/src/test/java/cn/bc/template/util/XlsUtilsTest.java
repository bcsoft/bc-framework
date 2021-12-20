/**
 *
 */
package cn.bc.template.util;

import cn.bc.core.util.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author dragon
 */
public class XlsUtilsTest {
  // 加载docx文档
  private InputStream getDocumentInputStream() throws IOException {
    return new ClassPathResource("cn/bc/template/excel/xlsTpl.xls").getInputStream();
  }

  @Test
  public void testLoadText() throws Exception {
    String content = XlsUtils.loadText(this.getDocumentInputStream());
    Assert.assertNotNull(content);
    Assert.assertTrue(content.length() > 0);
    // System.out.println(content);
  }

  @Test
  public void testFindMarkers() throws Exception {
    String content = XlsUtils.loadText(this.getDocumentInputStream());
    Assert.assertNotNull(content);
    Assert.assertTrue(content.length() > 0);

    List<String> markers = XlsUtils.findMarkers(this
      .getDocumentInputStream());
    Assert.assertNotNull(markers);
    Assert.assertTrue(markers.size() > 0);
    Collections.sort(markers);
    Assert.assertEquals(
      "[cellValue, columnName, columnNames, exportTime, rowData, rowDatas, title]",
      markers.toString());
  }

  @Test
  public void testFormat() throws Exception {
    Map<String, Object> markerValues = new HashMap<String, Object>();
    markerValues.put("title", "标题");
    markerValues.put("exportTime", "2012-01-01");
    markerValues.put("columnNames", new String[]{"年度", "数量"});
    markerValues.put("rowDatas", new Object[]{
      new String[]{"2001", "100"}, new String[]{"2002", "50"},
      new String[]{"2003", "150"}});

    // 读取模板
    InputStream in = getDocumentInputStream();

    // 格式化模板
    File fileOut = new File("target/excel/xlsTpl.xls");
    if (fileOut.exists()) fileOut.delete();
    XlsUtils.formatTo(
      in,
      new FileOutputStream(fileOut),
      markerValues
    );
    Assert.assertTrue(fileOut.exists());
    Assert.assertTrue(fileOut.length() > 0);
  }

  @Test
  public void testChartWithFixedSize() throws Exception {
    Map<String, Object> markerValues = new HashMap<String, Object>();
    markerValues.put("title",
      "固定行数数据表测试 " + DateUtils.formatDateTime(new Date()));

    List<Object[]> rowDatas = new ArrayList<Object[]>();
    rowDatas.add(new Object[]{"A", 100, 50});
    rowDatas.add(new Object[]{"B", 50, 40});
    rowDatas.add(new Object[]{"C", 70, 20});
    rowDatas.add(new Object[]{"D", 70, 20});
    markerValues.put("rowDatas", rowDatas);

    // 读取模板
    InputStream in = new ClassPathResource("cn/bc/template/excel/chart_fixedSize.xls").getInputStream();

    // 格式化模板
    File fileOut = new File("target/excel/chart_fixedSize.xls");
    if (fileOut.exists()) fileOut.delete();
    XlsUtils.formatTo(
      in,
      new FileOutputStream(fileOut),
      markerValues
    );
    Assert.assertTrue(fileOut.exists());
    Assert.assertTrue(fileOut.length() > 0);
  }

  @Test
  public void testChartWithDynamic() throws Exception {
    // 构建模板数据
    Map<String, Object> markerValues = new HashMap<String, Object>();
    markerValues.put("title",
      "动态数据表、图表测试 " + DateUtils.formatDateTime(new Date()));
    List<Object[]> rowDatas = new ArrayList<Object[]>();
    rowDatas.add(new Object[]{"标题A", 100, 50});
    rowDatas.add(new Object[]{"标题B", 50, 40});
    rowDatas.add(new Object[]{"标题C", 70, 20});
    rowDatas.add(new Object[]{"标题D", 10, 30});
    rowDatas.add(new Object[]{"标题E", 80, 45});
    rowDatas.add(new Object[]{"标题F", 66, 33});
    rowDatas.add(new Object[]{"标题G", 30, 80});
    markerValues.put("rowDatas", rowDatas);

    // 读取模板
    InputStream in = new ClassPathResource("cn/bc/template/excel/chart_dynamic.xls").getInputStream();

    // 格式化模板
    File fileOut = new File("target/excel/chart_dynamic.xls");
    if (fileOut.exists()) fileOut.delete();
    XlsUtils.formatTo(
      in,
      new FileOutputStream(fileOut),
      markerValues
    );
    Assert.assertTrue(fileOut.exists());
    Assert.assertTrue(fileOut.length() > 0);
  }
}
