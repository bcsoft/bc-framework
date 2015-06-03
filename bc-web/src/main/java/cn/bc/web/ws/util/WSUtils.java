/**
 *
 */
package cn.bc.web.ws.util;

import cn.bc.web.ws.converter.WSConverter;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.xml.transform.StringResult;

import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

/**
 * @author dragon
 */
public class WSUtils {
	public static <T> T sendAndReceive(String soapUrl, String soapAction,
	                                   String msgTpl, Object[] msgArgs, WSConverter<T> converter) {
		if (msgArgs != null && msgArgs.length > 0)
			msgTpl = java.text.MessageFormat.format(msgTpl, msgArgs);
		StreamSource source = new StreamSource(new StringReader(msgTpl));
		StringResult xmlResult = new StringResult();
		WebServiceTemplate webServiceTemplate = buildWebServiceTemplate();
		webServiceTemplate.sendSourceAndReceiveToResult(soapUrl, source,
				new SoapActionCallback(soapAction), xmlResult);

		return converter.convert(xmlResult.toString());
	}

	public static String sendAndReceive(String soapUrl, String soapAction,
	                                    String msgTpl, Object[] msgArgs) {
		if (msgArgs != null && msgArgs.length > 0)
			msgTpl = java.text.MessageFormat.format(msgTpl, msgArgs);
		StreamSource source = new StreamSource(new StringReader(msgTpl));
		StringResult xmlResult = new StringResult();
		WebServiceTemplate webServiceTemplate = buildWebServiceTemplate();
		webServiceTemplate.sendSourceAndReceiveToResult(soapUrl, source,
				new SoapActionCallback(soapAction), xmlResult);

		return xmlResult.toString();
	}

	protected static WebServiceTemplate buildWebServiceTemplate() {
		WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

//		HttpClient httpClient = new DefaultHttpClient();
//		// 超时控制:milliseconds
//		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);//连接时间
//		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);//数据传输时间

		// TODO
		WebServiceMessageSender messageSender = null;//new CommonsHttpMessageSender(null);
		webServiceTemplate.setMessageSender(messageSender);
		return webServiceTemplate;
	}

	public static String makeXMLNode(String key, String value) {
		return wrapXMLMark(key) + value + wrapXMLMark("/" + key);
	}

	public static String wrapXMLMark(String value) {
		return "<" + value + ">";
	}
}
