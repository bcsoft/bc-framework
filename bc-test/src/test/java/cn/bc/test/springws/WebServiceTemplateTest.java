package cn.bc.test.springws;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.xml.transform.StringResult;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class WebServiceTemplateTest {
  private static String qiYeID = "17E0FFF7-7816-46A5-83A7-23D5C9F762AB";// 企业ID
  private static String soapUrl = "http://61.144.39.126/middle/WSMiddle.asmx";

  private static final String MESSAGE = "<GetMasterWZ xmlns=\"http://tempuri.org/\">"
    + "<strMasterID>"
    + qiYeID
    + "</strMasterID>\r\n"
    + "<dWeiZhangKSRQ>2011-08-01</dWeiZhangKSRQ>"
    + "<dWeiZhangJZRQ>2011-08-31</dWeiZhangJZRQ>"
    + "<strMsg></strMsg>"
    + "</GetMasterWZ>";

  public final WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

  public void setDefaultUri(String defaultUri) {
    webServiceTemplate.setDefaultUri(defaultUri);
  }

  // send to the configured default URI
  public void simpleSendAndReceive() {
    StreamSource source = new StreamSource(new StringReader(MESSAGE));
    StreamResult result = new StreamResult(System.out);
    webServiceTemplate.sendSourceAndReceiveToResult(source,
      new SoapActionCallback("http://tempuri.org/GetMasterWZ"),
      result);
  }

  // send to an explicit URI
  @Test
  public void customSendAndReceive() {
    StreamSource source = new StreamSource(new StringReader(MESSAGE));
    // StreamResult result = new StreamResult(System.out);
    StringResult result = new StringResult();
    webServiceTemplate.sendSourceAndReceiveToResult(soapUrl, source,
      new SoapActionCallback("http://tempuri.org/GetMasterWZ"),
      result);

    List<Map<String, String>> maps = this.parseDataSet(result.toString());
    System.out.println("count=" + maps.size());
    System.out.println(this.debugInfo(maps));
  }

  public static void main(String[] args) throws Exception {
    WebServiceTemplateTest client = new WebServiceTemplateTest();
    client.setDefaultUri(soapUrl);
    client.simpleSendAndReceive();
  }

  private List<Map<String, String>> parseDataSet(String xml) {
    List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
    Map<String, String> map;
    Document doc = Jsoup.parse(xml);
    Elements rows = doc.getElementsByTag("Table");
    for (Element row : rows) {
      Elements cells = row.children();
      map = new HashMap<String, String>();
      for (Element cell : cells) {
        map.put(cell.tagName(), cell.html());
      }
      maps.add(map);
    }
    return maps;
  }

  private String debugInfo(List<Map<String, String>> maps) {
    StringBuffer t = new StringBuffer();
    for (Map<String, String> map : maps) {
      int i = 0;
      t.append("{");
      for (Entry<String, String> e : map.entrySet()) {
        //System.out.println(e.getKey() + "=" + e.getValue());
        t.append((i > 0 ? ", " : "") + e.getKey() + ": " + e.getValue());
        i++;
      }
      t.append("}\r\n");
    }
    return t.toString();
  }
}
