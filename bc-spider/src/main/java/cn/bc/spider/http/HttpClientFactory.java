/**
 * 
 */
package cn.bc.spider.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/**
 * @author dragon
 * 
 */
public class HttpClientFactory {
	private static Log logger = LogFactory.getLog(HttpClientFactory.class);
	private static Map<String, HttpClient> cache = new HashMap<String, HttpClient>();
	public static Map<String, String> userAgents = new HashMap<String, String>();

	static {
		// 可用的user-agent列表
		userAgents
				.put("Win7Chrome26",
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
		userAgents
				.put("Win7IE10",
						"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)");
		userAgents
				.put("Win7IE9",
						"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0)");
		userAgents
				.put("Win7IE8",
						"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0)");
	}

	private HttpClientFactory() {
	}

	/**
	 * 初始化一个全新的默认的HttpClient实例
	 * 
	 * @return
	 */
	public static HttpClient create() {
		// HttpClient httpClient = createSimpleHttpClient();
		HttpClient httpClient = createThreadSafeHttpClient();
		httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT,
				userAgents.get("Win7IE9"));
		return httpClient;
	}

	private static HttpClient createThreadSafeHttpClient() {
		// Create and initialize HTTP parameters
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params, 100);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

		// Create and initialize scheme registry
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));

		// Create an HttpClient with the ThreadSafeClientConnManager.
		// This connection manager must be used if more than one thread will
		// be using the HttpClient.
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
				schemeRegistry);
		HttpClient httpClient = new DefaultHttpClient(cm, params);
		return httpClient;
	}

	// private static HttpClient createSimpleHttpClient() {
	// return new DefaultHttpClient();
	// }

	/**
	 * 获取指定标识的一个HttpClient实例，如果没有就创建一个新的并缓存起来
	 * 
	 * @param id
	 * @return
	 */
	public static synchronized HttpClient get(String id) {
		if (cache.containsKey(id)) {
			return cache.get(id);
		} else {
			HttpClient httpClient = create();
			cache.put(id, httpClient);
			logger.warn("创建 HttpClient 缓存: id=" + id);
			return httpClient;
		}
	}

	/**
	 * 移除对指定标识的HttpClient实例的缓存
	 * 
	 * @param id
	 * @return
	 */
	public static synchronized HttpClient remove(String id) {
		if (id == null)
			return null;

		if (cache.containsKey(id)) {
			logger.warn("移除 HttpClient 缓存: id=" + id);
			return cache.remove(id);
		} else {
			return null;
		}
	}

	public static int size() {
		return cache.size();
	}

}
