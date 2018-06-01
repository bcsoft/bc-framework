/**
 *
 */
package cn.bc.web.ws.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Assert;
import org.junit.Test;

public class JsoupTest {
  @Test
  public void testParseXml() {
    String xml = "<root><id>1</id><中文>中国</中文></root>";
    Document doc = Jsoup.parse(xml, "", Parser.xmlParser());

    Assert.assertNotNull(doc);
    System.out.println(doc);
  }

  @Test
  public void testParseHtml() {
    String html = "<html><head><title>First parse</title></head>" +
      "<body><id>1</id><中文>中国</中文></body></html>";
    Document doc = Jsoup.parse(html);
    Assert.assertNotNull(doc);
    System.out.println(doc);
  }
}
