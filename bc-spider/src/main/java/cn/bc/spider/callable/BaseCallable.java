package cn.bc.spider.callable;

import cn.bc.core.exception.CoreException;
import cn.bc.spider.Result;
import cn.bc.spider.http.HttpClientFactory;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

/**
 * 基本的网络请求Callable，需要子类解析响应的结果
 *
 * @author dragon
 */
public abstract class BaseCallable<V> implements Callable<Result<V>> {
	private static Log logger = LogFactory.getLog("cn.bc.spider.callable.BaseCallable");
	private static ExpressionParser parser = new SpelExpressionParser();
	private HttpEntity responseEntity;// 响应的实体信息
	private String method;// 请求方法：get|post
	private String type;// 响应的类型：json、html、stream:jpg、...
	private String url;// 请求的地址
	private String group;// 该次请求所属的分组：同组的请求使用相同的session
	private String encoding = "UTF-8";// 请求的编码
	private String successExpression;// 用于判断请求是否成功的spel表达式：表达式上下文中含有document、html、this对象可以使用
	private String resultExpression;// 用于从文档中获取数据的spel表达式：表达式上下文中含有document、html、this对象可以使用
	private String userAgent;// 请求时使用的用户代理
	private Map<String, String> formData;// 表单参数
	private Map<String, String> httpParams;// http参数
	private HashMap<String, String> headers;
	private boolean hadParseRespone;
	private V respone;
	private String responseText;
	private Object payload;
	private int timeout = 0;// 超时(ms)，默认不设置

	/**
	 * 设置超时时间，单位毫秒，设为0代表不设置，默认不设置
	 *
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public Result<V> call() {
		String url = getUrl();
		if (logger.isInfoEnabled()) {
			logger.info("url=" + url);
			logger.info("method=" + method);
			logger.info("type=" + type);
			logger.info("group=" + group);
			logger.info("encoding=" + encoding);
			logger.info("userAgent=" + userAgent);
		}
		Map<String, String> kvs;
		HttpUriRequest request = null;
		try {
			// 创建请求
			if ("post".equalsIgnoreCase(method)) {
				HttpPost post = new HttpPost(url);
				// 设置表单参数
				kvs = getFormData();
				if (logger.isInfoEnabled())
					logger.info("formData=" + kvs);
				if (kvs != null && !kvs.isEmpty()) {
					List<NameValuePair> formData = new ArrayList<NameValuePair>();
					for (Entry<String, String> e : kvs.entrySet()) {
						formData.add(new BasicNameValuePair(e.getKey(), e
								.getValue()));
					}
					HttpEntity entity = new UrlEncodedFormEntity(formData,
							getEncoding());

					post.setEntity(entity);
				} else if (payload != null) {
					if (logger.isInfoEnabled())
						logger.info("payload=" + payload);
					if (payload instanceof String) {
						post.setEntity(new StringEntity((String) payload));
					}
				}
				request = post;
			} else {// 默认为get
				HttpGet get = new HttpGet(url);
				request = get;
			}

			// 设置http参数
			kvs = getHttpParams();
			if (logger.isInfoEnabled())
				logger.info("httpParams=" + kvs);
			if (kvs != null && !kvs.isEmpty()) {
				HttpParams httpParams = request.getParams();
				for (Entry<String, String> e : kvs.entrySet()) {
					httpParams.setParameter(e.getKey(), e.getValue());
				}
				request.setParams(httpParams);
			}

			// 设置请求的头
			kvs = getHeaders();
			if (logger.isInfoEnabled())
				logger.info("headers=" + kvs);
			if (kvs != null && !kvs.isEmpty()) {
				for (Entry<String, String> e : kvs.entrySet()) {
					request.addHeader(e.getKey(), e.getValue());
				}
			}

			// 提交请求
			HttpClient httpClient = getHttpClient();
			if (logger.isDebugEnabled()) {
				debug(request);
			}
			HttpResponse response;
			response = httpClient.execute(request);
			this.responseEntity = response.getEntity();
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {// 请求成功
				// 解析响应的结果
				// parseResponse();

				// 返回结果
				return getResult();
			} else {// 请求失败
				return new Result<V>(new RuntimeException(
						"request's respone is not ok. StatusCode="
								+ response.getStatusLine().getStatusCode()
								+ ",Reason="
								+ response.getStatusLine().getReasonPhrase()));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (request != null) request.abort();
		}
	}

	private void debug(HttpUriRequest request) {
		logger.debug("request:");
		logger.debug("  requestLine=" + request.getRequestLine());
		logger.debug("  headers:");
		for (Header h : request.getAllHeaders()) {
			logger.debug("      " + h.getName() + "=" + h.getValue());
		}
		logger.debug("  params=" + request.getParams());
	}

	/**
	 * 设置请求的头
	 *
	 * @return
	 */
	protected Map<String, String> getHeaders() {
		if (this.headers == null)
			this.headers = new HashMap<String, String>();
		return this.headers;
	}

	/**
	 * 解析响应
	 *
	 * @throws IOException
	 */
	public abstract V parseResponse() throws Exception;

	/**
	 * @return
	 */
	public V getResponse() throws Exception {
		if (!hadParseRespone) {
			respone = parseResponse();
			hadParseRespone = true;
		}
		return respone;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public String getGroup() {
		return group;
	}

	public BaseCallable<V> setGroup(String group) {
		this.group = group;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public BaseCallable<V> setUrl(String url) {
		this.url = url;
		return this;
	}

	public HttpClient getHttpClient() {
		HttpClient c;
		if (this.group == null) {
			c = HttpClientFactory.create();
		} else {
			c = HttpClientFactory.get(this.group);
		}
		if (this.userAgent != null) {
			c.getParams().setParameter(HttpMethodParams.USER_AGENT, this.userAgent);
		}
		if (this.timeout > 0) {
			c.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);//连接时间
			c.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);//数据传输时间
		}
		return c;
	}

	public String getSuccessExpression() {
		return successExpression;
	}

	public void setSuccessExpression(String successExpression) {
		this.successExpression = successExpression;
	}

	public String getResultExpression() {
		return resultExpression;
	}

	public void setResultExpression(String resultExpression) {
		this.resultExpression = resultExpression;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 * 获取请求的编码
	 *
	 * @return
	 */
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * 获取POST请求需提交的表单参数
	 *
	 * @return
	 */
	protected Map<String, String> getFormData() {
		if (this.formData == null)
			this.formData = new HashMap<String, String>();
		return this.formData;
	}

	/**
	 * 获取请求需提交的http参数
	 *
	 * @return
	 */
	public Map<String, String> getHttpParams() {
		if (this.httpParams == null)
			this.httpParams = new HashMap<String, String>();
		return httpParams;
	}

	/**
	 * 添加一个POST提交参数
	 *
	 * @param key   键
	 * @param value 值
	 */
	public void addFormData(String key, String value) {
		if (this.formData == null)
			this.formData = new HashMap<String, String>();
		this.formData.put(key, value);
	}

	/**
	 * 添加一堆POST提交参数
	 *
	 * @param params
	 */
	public void addFormData(Map<String, String> params) {
		if (params == null)
			return;
		if (this.formData == null)
			this.formData = new HashMap<String, String>();
		this.formData.putAll(params);
	}

	/**
	 * 添加一个请求参数
	 *
	 * @param key   键
	 * @param value 值
	 */
	public void addHttpParam(String key, String value) {
		if (this.httpParams == null)
			this.httpParams = new HashMap<String, String>();
		this.httpParams.put(key, value);
	}

	/**
	 * 添加一堆POST提交参数
	 *
	 * @param params
	 */
	public void addHttpParam(Map<String, String> params) {
		if (params == null)
			return;
		if (this.httpParams == null)
			this.httpParams = new HashMap<String, String>();
		this.httpParams.putAll(params);
	}

	/**
	 * 添加一个请求头参数
	 *
	 * @param key   键
	 * @param value 值
	 */
	public void addHeader(String key, String value) {
		if (this.headers == null)
			this.headers = new HashMap<String, String>();
		this.headers.put(key, value);
	}

	/**
	 * 添加一堆请求头参数
	 *
	 * @param params
	 */
	public void addHeader(Map<String, String> params) {
		if (params == null)
			return;
		if (this.headers == null)
			this.headers = new HashMap<String, String>();
		this.headers.putAll(params);
	}

	private <T> T getExpressionValue(String expression, Class<T> clazz) {
		Expression exp = parser.parseExpression(expression);
		try {
			return exp.getValue(getExpressionContextObject(), clazz);
		} catch (Exception e) {
			throw new CoreException(e.getMessage(), e);
		}
	}

	protected EvaluationContext getExpressionContextObject() throws Exception {
		StandardEvaluationContext context = new StandardEvaluationContext(
				getResponse());
		context.setVariable("httpParams", this.httpParams);
		context.setVariable("formData", this.formData);
		return context;
		// return this;
		// return new Object() {
		// public Object getR() throws Exception {
		// return BaseCallable.this.getResponse();
		// }
		// };
	}

	/**
	 * 获取返回值
	 *
	 * @return 如果设置了resultExpression则返回此表达式计算的结果，否则返回isSuccess()用于判断请求是否成功
	 */
	@SuppressWarnings("unchecked")
	public Result<V> getResult() throws Exception {
		try {
			boolean success = isSuccess();
			Result<V> r;
			if (!success) {
				r = new Result<V>(success, (V) getFailedData());
			} else {
				r = new Result<V>(success, parseResultData());
			}
			return r;
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return new Result<V>(e);
		}
	}

	protected Object getFailedData() throws IOException {
		return this.getResponseText();
	}

	/**
	 * 解析通过spel获取的数据
	 *
	 * @return
	 */
	protected V parseResultData() throws Exception {
		if (resultExpression != null) {
			return parseResultData(getExpressionValue(this.resultExpression,
					Object.class));
		} else {
			// 默认返回getRespone()的结果
			return parseResultData(getExpressionValue("#root", Object.class));
			// return (V) getResponeText();
		}
	}

	/**
	 * 获取响应的文本信息
	 *
	 * @return
	 * @throws IOException
	 */
	protected String getResponseText() throws IOException {
		if (responseText == null) {
			responseText = EntityUtils.toString(this.responseEntity);
			if (logger.isDebugEnabled()) {
				logger.debug("responeText=" + responseText);
			}
			if (responseText == null) {
				responseText = "";
			}
		}
		return responseText;
	}

	@SuppressWarnings("unchecked")
	protected V parseResultData(Object data) throws Exception {
		return (V) data;
	}

	/**
	 * 判断请求是否成功
	 *
	 * @return
	 */
	public Boolean isSuccess() {
		if (this.successExpression != null)
			return getExpressionValue(this.successExpression, Boolean.class);
		else
			return true;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 导出请求结果到文件
	 *
	 * @param out
	 * @throws Exception
	 */
	public void export(OutputStream out) throws Exception {
		Object r = this.getResponse();
		if (r instanceof String) {
			FileCopyUtils.copy(r.toString(), new OutputStreamWriter(out));
		} else if (r instanceof InputStream) {
			FileCopyUtils.copy((InputStream) r, out);
		} else {
			// do nothing
		}
	}
}