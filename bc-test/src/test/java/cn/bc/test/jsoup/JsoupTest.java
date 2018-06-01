package cn.bc.test.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupTest {
  //@Test
  public void test01() throws Exception {
    Document doc = Jsoup
      .connect("http://61.144.39.126/middle/WSMiddle.asmx?op=GetDriverTaxiQY")
      // .data("query", "Java")
      .userAgent(
        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.91 Safari/534.30")
      // .cookie("auth", "token")
      // .timeout(3000)
      .get();
    System.out.println(doc);
  }
}
