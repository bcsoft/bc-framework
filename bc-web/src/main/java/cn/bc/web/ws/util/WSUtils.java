/**
 * 
 */
package cn.bc.web.ws.util;

import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.CommonsHttpMessageSender;
import org.springframework.xml.transform.StringResult;

import cn.bc.web.ws.converter.WSConverter;

/**
 * @author dragon
 * 
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

		HttpClientParams params = new HttpClientParams();
		// 超时控制:milliseconds
		params.setSoTimeout(30000);

		HttpClient httpClient = new HttpClient();
		httpClient.setParams(params);

		WebServiceMessageSender messageSender = new CommonsHttpMessageSender(
				httpClient);

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
