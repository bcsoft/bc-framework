package cn.bc.test.springws;

import java.io.StringReader;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.ws.client.core.WebServiceTemplate;

public class WebServiceClient {

	private static final String MESSAGE = "<GetDriverTaxiQY xmlns=\"http://tempuri.org/\">"
			+ "<strQYID>string</strQYID>"
			+ "<strDriverNO>string</strDriverNO>"
			+ "<strCarNO>string</strCarNO>"
			+ "<strMsg>string</strMsg>"
			+ "</GetDriverTaxiQY>";

	public final WebServiceTemplate webServiceTemplate = new WebServiceTemplate();

	public void setDefaultUri(String defaultUri) {
		webServiceTemplate.setDefaultUri(defaultUri);
	}

	// send to the configured default URI
	public void simpleSendAndReceive() {
		StreamSource source = new StreamSource(new StringReader(MESSAGE));
		StreamResult result = new StreamResult(System.out);
		webServiceTemplate.sendSourceAndReceiveToResult(source, result);
	}

	// send to an explicit URI
	public void customSendAndReceive() {
		StreamSource source = new StreamSource(new StringReader(MESSAGE));
		StreamResult result = new StreamResult(System.out);
		webServiceTemplate.sendSourceAndReceiveToResult(
				"http://localhost:8080/AnotherWebService", source, result);
	}

	public static void main(String[] args) throws Exception {
		String url = "http://tempuri.org/GetDriverTaxiQY";
		WebServiceClient client = new WebServiceClient();
		client.setDefaultUri(url);
		client.simpleSendAndReceive();
	}
}
