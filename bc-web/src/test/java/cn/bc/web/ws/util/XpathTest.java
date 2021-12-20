/**
 *
 */
package cn.bc.web.ws.util;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public class XpathTest {
  @Test
  public void testParseXml() throws Exception {
    String xml = "<root><id name=\"_id\">1</id><中文 name=\"_中文\">中国</中文><strMsg name=\"msg\" xmlns=\"http://tempuri.org/\"/></root>";
    InputSource source = new InputSource(new StringReader(xml));
    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    //domFactory.setNamespaceAware(true); // never forget this!
    DocumentBuilder builder = domFactory.newDocumentBuilder();
    Document doc = builder.parse(source);

    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    XPathExpression expr = xpath.compile("/root");

    Node root = (Node) expr.evaluate(doc, XPathConstants.NODE);
    Assert.assertNotNull(root);
    Assert.assertEquals("root", root.getNodeName());

    NodeList nodes = root.getChildNodes();
    Assert.assertEquals(3, nodes.getLength());
    Node n;
    for (int i = 0; i < nodes.getLength(); i++) {
      n = nodes.item(i);
      Assert.assertEquals(1, n.getNodeType());
      //System.out.println("tagName=" + n.getNodeName());
      //System.out.println("attr name=" + n.getAttributes().getNamedItem("name").getTextContent());
      //System.out.println("content=" + n.getTextContent());// not n.getNodeValue()
    }

    expr = xpath.compile("//strMsg");
    Node msgNode = (Node) expr.evaluate(doc, XPathConstants.NODE);
    Assert.assertNotNull(msgNode);
  }

  @Test
  public void testParseAttrs() throws Exception {
    String xml = "<root><id name=\"_id\">1</id><中文 name=\"_中文\">中国</中文></root>";
    InputSource source = new InputSource(new StringReader(xml));
    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    domFactory.setNamespaceAware(true); // never forget this!
    DocumentBuilder builder = domFactory.newDocumentBuilder();
    Document doc = builder.parse(source);

    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    XPathExpression expr = xpath.compile("//*[@name]");

    NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
    Assert.assertNotNull(nodes);
    Assert.assertEquals(2, nodes.getLength());
  }
}
