/**
 *
 */
package cn.bc.template.util;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dragon
 */
public class DocxUtilsTest {
  // 加载docx文档
  private InputStream getDocumentInputStream() throws IOException,
    InvalidFormatException {
    return new ClassPathResource("cn/bc/template/word/docxTpl.docx")
      .getInputStream();
  }

  @Test
  public void testLoadText() throws Exception {
    String content = DocxUtils.loadText(this.getDocumentInputStream());
    Assert.assertNotNull(content);
    Assert.assertTrue(content.length() > 0);
  }

  @Test
  public void testFindMarkers() throws Exception {
    String content = DocxUtils.loadText(this.getDocumentInputStream());
    Assert.assertNotNull(content);
    Assert.assertTrue(content.length() > 0);

    List<String> markers = DocxUtils.findMarkers(this
      .getDocumentInputStream());
    Assert.assertNotNull(markers);
    Assert.assertTrue(markers.size() > 0);
    Collections.sort(markers);
    Assert.assertEquals("[age, name]", markers.toString());
  }

  @Test
  public void testFormat() throws Exception {
    Map<String, Object> markerValues = new HashMap<String, Object>();
    markerValues.put("name", "小明");
    markerValues.put("age", "30岁");

    XWPFDocument document = DocxUtils.format(getDocumentInputStream(),
      markerValues);
    Assert.assertNotNull(document);

    // 输出格式化后的文本内容
    // System.out.println(DocxUtils.loadText(document));
  }

  // 获取匹配的键值
  // @Test
  public void testFindKeies() {
    String pattern = "(?<=\\$\\{)([\\w\u4e00-\u9fa5]+)(?=\\})";
    String source = "AA${name},{},${a编号},${编号1},${编_号a}BB,${code_01}CC,name,{name}}";
    Pattern p = Pattern.compile(pattern);
    Matcher m = p.matcher(source);
    // m.find();
    // Assert.assertEquals("name", m.group());
    // m.find();
    // Assert.assertEquals("编号", m.group());
    // m.find();
    // Assert.assertEquals("编号", m.group());
    while (m.find()) {
      System.out.println(m.group());
    }
  }
}
