package cn.bc.test.springws;

import javax.xml.soap.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class HelloWebServiceClient {

  public static final String NAMESPACE_URI = "http://tempuri.org/";

  public static final String PREFIX = "";

  private SOAPConnectionFactory connectionFactory;

  private MessageFactory messageFactory;

  private URL url;

  public HelloWebServiceClient(String url) throws SOAPException,
    MalformedURLException {
    connectionFactory = SOAPConnectionFactory.newInstance();
    messageFactory = MessageFactory.newInstance();
    this.url = new URL(url);
  }

  private SOAPMessage createHelloRequest() throws SOAPException, IOException {
    //
//		MimeHeaders headers = new MimeHeaders();
//        headers.setHeader("POST", "/middle/WSMiddle.asmx HTTP/1.1");
//        headers.setHeader("Content-Type", "text/xml; charset=utf-8");
//        headers.setHeader("SOAPAction", "http://tempuri.org/GetMasterWZ");

    SOAPMessage message = messageFactory.createMessage();
    //message.setProperty("SOAPAction", "http://tempuri.org/GetMasterWZ");
    SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
    Name helloRequestName = envelope.createName("GetMasterWZ", PREFIX,
      NAMESPACE_URI);
    SOAPBodyElement helloRequestElement = message.getSOAPBody()
      .addBodyElement(helloRequestName);
    // helloRequestElement.setValue("Rondy.F");

    // 参数
    Name n = envelope.createName("strMasterID");
    helloRequestElement.addChildElement(n).setValue(
      "17E0FFF7-7816-46A5-83A7-23D5C9F762AB");
    helloRequestElement.addChildElement("dWeiZhangKSRQ").setValue(
      "2011-08-01");
    SOAPElement e = helloRequestElement.addChildElement("dWeiZhangJZRQ");
    e.setValue("2011-08-31");
    helloRequestElement.addChildElement("strMsg").setValue("");

    System.out.println(helloRequestElement.toString());
    System.out.println(e.toString());

    return message;
  }

  public void callWebService() throws SOAPException, IOException {
    SOAPMessage request = createHelloRequest();
    SOAPConnection connection = connectionFactory.createConnection();
    SOAPMessage response = connection.call(request, url);
    if (!response.getSOAPBody().hasFault()) {
      writeHelloResponse(response);
    } else {
      SOAPFault fault = response.getSOAPBody().getFault();
      System.err.println("Received SOAP Fault");
      System.err.println("SOAP Fault Code :" + fault.getFaultCode());
      System.err.println("SOAP Fault String :" + fault.getFaultString());
    }
  }

  private void writeHelloResponse(SOAPMessage message) throws SOAPException {
    SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
    Name helloResponseName = envelope.createName(
      "GetMasterWZResponse", PREFIX, NAMESPACE_URI);
    Iterator<?> childElements = message.getSOAPBody().getChildElements(
      helloResponseName);
    while (childElements.hasNext()) {
      SOAPBodyElement helloResponseElement = (SOAPBodyElement) childElements
        .next();
      System.out.println(helloResponseElement.getTextContent());
      System.out.println("===================================");
    }
  }

  public static void main(String[] args) throws Exception {
    String url = "http://61.144.39.126/middle/WSMiddle.asmx";
    HelloWebServiceClient helloClient = new HelloWebServiceClient(url);
    helloClient.callWebService();
  }
}
