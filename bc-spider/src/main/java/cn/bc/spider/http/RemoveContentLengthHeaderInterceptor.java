package cn.bc.spider.http;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * 避免重定向 POST 请求时出现 "org.apache.http.ProtocolException: Content-Length header already present" 异常的拦截器，
 * 在处理前先移除 Content-Length 头。
 * <p>
 * Idea from <a href="https://stackoverflow.com/questions/3332370/content-length-header-already-present#answer-24089650">Content-Length header already present</a>
 *
 * @author RJ
 */
public class RemoveContentLengthHeaderInterceptor implements HttpRequestInterceptor {
	@Override
	public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
		if (request.containsHeader(HTTP.CONTENT_LEN))
			request.removeHeaders(HTTP.CONTENT_LEN);
	}
}