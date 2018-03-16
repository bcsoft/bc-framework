/**
 *
 */
package cn.bc.spider.http;

import cn.bc.core.util.SpringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dragon
 */
public class HttpClientFactory {
	private static Logger logger = LoggerFactory.getLogger(HttpClientFactory.class);
	private static Map<String, CloseableHttpClient> cache = new HashMap<>();
	public static Map<String, String> userAgents = new HashMap<>();
	private static HttpHost proxy;// 全局代理
	private static int timeout = 0;// 全局超时(ms)，默认不设置
	/**
	 * 默认支持的重定向次数
	 */
	public static final int DEFAULT_MAX_REDIRECTS = 1;

	public static HttpHost getProxy() {
		return proxy;
	}

	/**
	 * 设置代理
	 */
	public static void setProxy(HttpHost proxy) {
		HttpClientFactory.proxy = proxy;
	}

	public static int getTimeout() {
		return timeout;
	}

	/**
	 * 设置超时时间，单位毫秒，设为0代表不设置，默认不设置
	 */
	public static void setTimeout(int timeout) {
		HttpClientFactory.timeout = timeout;
	}

	static {
		// 可用的user-agent列表
		userAgents.put("Win7Chrome26",
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
		userAgents.put("Win7IE10", "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)");
		userAgents.put("Win7IE9", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0)");
		userAgents.put("Win7IE8", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0)");

		// 允许通过环境变量设置代理
		String proxyPort = System.getenv("BC_PROXY_PORT");
		if (proxyPort != null) {
			String proxyHost = System.getenv("BC_PROXY_HOST");
			if (proxyHost == null) proxyHost = "127.0.0.1";
			proxy = new HttpHost(proxyHost, Integer.parseInt(proxyPort));
			logger.warn("根据环境变量 BC_PROXY_PORT、BC_PROXY_HOST 的值设置 HttpClient 全局代理为 {}:{}", proxyHost, proxyPort);
		}
	}

	private HttpClientFactory() {
	}

	/**
	 * 初始化一个全新的默认的HttpClient实例
	 */
	public static CloseableHttpClient create() {
		return create(null);
	}

	/**
	 * 初始化一个全新的带特殊配置的HttpClient实例
	 *
	 * @param options 特殊配置
	 */
	public static CloseableHttpClient create(JSONObject options) {
		return createThreadSafeHttpClient(options);
	}

	private static CloseableHttpClient createThreadSafeHttpClient(JSONObject options) {
		return createThreadSafeHttpClientBuilder(options).build();
	}

	private static HttpClientBuilder createThreadSafeHttpClientBuilder(JSONObject options) {
		if (options == null) options = new JSONObject();

		// Multithreaded request execution: http://hc.apache.org/httpcomponents-client-4.4.x/tutorial/html/connmgmt.html#d5e405
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

		SocketConfig socketConfig = SocketConfig.custom()
			.setTcpNoDelay(true)
			.build();
		cm.setDefaultSocketConfig(socketConfig);
		//cm.setSocketConfig(proxy, socketConfig);

		// 最大连接数
		cm.setMaxTotal(200);

		// 每个路由(不同的请求地址不同的route)最大连接数：http://blog.csdn.net/shootyou/article/details/6415248
		cm.setDefaultMaxPerRoute(20);

		// 默认编码
		ConnectionConfig connectionConfig = ConnectionConfig.custom()
			.setCharset(Consts.UTF_8)
			.build();
		cm.setDefaultConnectionConfig(connectionConfig);

		// 全局请求配置
		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setMaxRedirects(DEFAULT_MAX_REDIRECTS);

		// 超时设置
		int timeout_;
		if (options.has("timeout")) timeout_ = options.getInt("timeout");//  自定义 timeout
		else timeout_ = timeout; // 全局 timeout
		if (timeout_ > 0) {
			requestConfigBuilder.setSocketTimeout(timeout_)
				.setConnectTimeout(timeout_)
				.setConnectionRequestTimeout(timeout_);
		}

		// 代理设置
		if (options.has("proxy")) { //  自定义 proxy
			String[] proxy = options.getString("proxy").split(":");
			requestConfigBuilder.setProxy(new HttpHost(proxy[0], Integer.parseInt(proxy[1])));
		} else {
			if (proxy != null) requestConfigBuilder.setProxy(proxy); // 系统全局 proxy
		}

		HttpClientBuilder builder = HttpClients.custom()
			.setConnectionManager(cm)
			.setDefaultRequestConfig(requestConfigBuilder.build());

		// User-Agent
		if (options.has("userAgent")) builder.setUserAgent(options.getString("userAgent")); // 自定义 userAgent
		else builder.setUserAgent(userAgents.get("Win7IE9")); // 全局 userAgent

		// Enabled auto redirect post/delete method if autoRedirectAll=true
		if (options.has("autoRedirectAll") && options.getBoolean("autoRedirectAll"))
			builder.setRedirectStrategy(new LaxRedirectStrategy());

		// addInterceptorFirst
		if (options.has("beforeRequestInterceptors")) {
			JSONArray interceptors = options.getJSONArray("beforeRequestInterceptors");
			logger.debug("beforeRequestInterceptors={}", interceptors);
			for (int i = 0; i < interceptors.length(); i++)
				builder.addInterceptorFirst(SpringUtils.getBean(interceptors.getString(i), HttpRequestInterceptor.class));
		}

		// addInterceptorLast
		if (options.has("afterRequestInterceptors")) {
			JSONArray interceptors = options.getJSONArray("afterRequestInterceptors");
			logger.debug("afterRequestInterceptors={}", interceptors);
			for (int i = 0; i < interceptors.length(); i++)
				builder.addInterceptorLast(SpringUtils.getBean(interceptors.getString(i), HttpRequestInterceptor.class));
		}

		return builder;
	}

	/**
	 * 获取指定标识的一个HttpClient实例，如果没有就创建一个新的并缓存起来
	 */
	public static synchronized CloseableHttpClient get(String id) {
		return get(id, null);
	}

	/**
	 * 获取指定标识的一个HttpClient实例，如果没有就创建一个新的并缓存起来
	 *
	 * @param id             group
	 * @param defaultOptions 如果不存在则使用 defaultOptions 创建一个ID
	 */
	public static synchronized CloseableHttpClient get(String id, JSONObject defaultOptions) {
		if (cache.containsKey(id)) {
			return cache.get(id);
		} else {
			CloseableHttpClient httpClient = create(defaultOptions);
			cache.put(id, httpClient);
			logger.warn("创建 HttpClient 缓存: id=" + id);
			return httpClient;
		}
	}

	/**
	 * 为WebService获取一个HttpClient实例，此示例做了特殊处理，避免如下异常的发生
	 * org.apache.http.ProtocolException: Content-Length header already present
	 *
	 * @ref http://forum.spring.io/forum/spring-projects/web-services/118857-spring-ws-2-1-4-0-httpclient-proxy-content-length-header-already-present
	 */
	public static synchronized CloseableHttpClient get4ws(String id) {
		if (cache.containsKey(id)) {
			return cache.get(id);
		} else {
			//proxy = new HttpHost("127.0.0.1", 8888);
			JSONObject options = new JSONObject();
			options.put("autoRedirectAll", false);
			HttpClientBuilder httpClientBuilder = createThreadSafeHttpClientBuilder(options);
			httpClientBuilder.addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor());
			CloseableHttpClient httpClient = httpClientBuilder.build();
			cache.put(id, httpClient);
			logger.warn("创建 HttpClient 缓存: id={}", id);
			return httpClient;
		}
	}

	/**
	 * 移除对指定标识的HttpClient实例的缓存
	 */
	public static synchronized CloseableHttpClient remove(String id) {
		if (id == null) return null;

		if (cache.containsKey(id)) {
			logger.warn("移除 HttpClient 缓存: id={}", id);
			return cache.remove(id);
		} else return null;
	}

	public static int size() {
		return cache.size();
	}
}