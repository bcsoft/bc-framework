package cn.bc.spider.http;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 *
 * @author RJ
 */
public class ReencodeFormParamInterceptor implements HttpRequestInterceptor {
	@Override
	public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
		request.getParams();
	}
}