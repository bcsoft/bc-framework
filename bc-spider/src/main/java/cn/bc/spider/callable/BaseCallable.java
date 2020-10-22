package cn.bc.spider.callable;

import cn.bc.core.exception.CoreException;
import cn.bc.spider.Result;
import cn.bc.spider.http.HttpClientFactory;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
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
  private static Logger logger = LoggerFactory.getLogger("cn.bc.spider.callable.BaseCallable");
  private static ExpressionParser parser = new SpelExpressionParser();
  protected HttpEntity responseEntity;// 响应的实体信息
  private String method;// 请求方法：get|post
  private String type;// 响应的类型：json、html、stream:jpg、...
  private String url;// 请求的地址
  private String group;// 该次请求所属的分组：同组的请求使用相同的session
  private String encoding = "UTF-8";// 请求的编码
  private String forceResponseEncoding;// 解析响应流时强制使用的编码，默认不设置，根据响应的 Content-Type 头自动设置
  private String successExpression;// 用于判断请求是否成功的spel表达式：表达式上下文中含有document、html、this对象可以使用
  private String resultExpression;// 用于从文档中获取数据的spel表达式：表达式上下文中含有document、html、this对象可以使用
  private String userAgent;// 请求时使用的用户代理
  private Map<String, String> formData;// 表单参数
  private Map<String, String> httpParams;// http参数
  private HashMap<String, String> headers;
  private boolean hadParseResponse;
  private V response;
  private Header[] httpResponseHeaders;
  private String responseText;
  private Object payload;
  private int timeout = 0;// 超时(ms)，默认不设置
  private JSONObject httpOptions; // 特殊的 httpClient 配置：{"beforeRequestInterceptors": [springBeanName1, ...]}

  /**
   * 设置超时时间，单位毫秒，设为0代表不设置，默认不设置
   */
  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public Result<V> call() {
    String url = getUrl();
    if (logger.isInfoEnabled()) {
      logger.info("method={}, type={}, group={}, encoding={}", method, type, group, encoding);
      logger.info("url={}", url);
      logger.info("userAgent={}", userAgent);
    }
    HttpUriRequest request = null;
    CloseableHttpResponse response = null;
    try {
      // 初始化请求构建器
      RequestBuilder requestBuilder = RequestBuilder.create(method.toUpperCase()).setUri(this.url);

      // 请求头
      for (Entry<String, String> e : getHeaders().entrySet()) {
        requestBuilder.addHeader(e.getKey(), e.getValue());
      }

      // http参数
      for (Entry<String, String> e : getHttpParams().entrySet()) {
        requestBuilder.addParameter(e.getKey(), e.getValue());
      }
      if (this.userAgent != null) {
        requestBuilder.addParameter("User-Agent", this.userAgent);
      }

      // 编码
      if (this.encoding != null) requestBuilder.setCharset(Charset.forName(this.encoding.toUpperCase()));

      // 超时
      if (this.timeout > 0) {
        requestBuilder.setConfig(RequestConfig.custom()
          .setSocketTimeout(timeout)
          .setConnectTimeout(timeout)
          .setConnectionRequestTimeout(timeout).build());
      }

      // 表单参数
      Map<String, String> kvs = getFormData();
      if (!kvs.isEmpty()) {
        List<NameValuePair> formData = new ArrayList<>();
        for (Entry<String, String> e : kvs.entrySet()) {
          formData.add(new BasicNameValuePair(e.getKey(), e.getValue()));
        }
        requestBuilder.setEntity(new UrlEncodedFormEntity(formData, getEncoding()));
      } else if (payload != null) {
        logger.debug("payload={}", payload);
        if (payload instanceof String) {
          StringEntity en = new StringEntity((String) payload, this.getEncoding());
          requestBuilder.setEntity(en);
        } else {
          throw new RuntimeException("unsupport payload type: " + payload.getClass());
        }
      }

      // 构建请求
      request = requestBuilder.build();

      // 提交请求
      response = getHttpClient().execute(request);
      httpResponseHeaders = response.getAllHeaders();

      // 解析请求
      this.responseEntity = response.getEntity();
      if (getSuccessStatusCode() == response.getStatusLine().getStatusCode()) {// 请求成功
        // 解析响应返回结果
        return getResult();
      } else {// 请求失败
        return defaultBadResult(response);
      }
    } catch (SocketTimeoutException e) {
      logger.info("连接超时, url={}", request.getURI());
      return new Result<>(e);
    } catch (Exception e) {
      logger.info("throw call Exception: " + e.getMessage());
      return new Result<>(e);
    } finally {
      if (request != null) request.abort();
      if (response != null) {
        try {
          response.close();
        } catch (IOException e) {
          logger.warn(e.getMessage());
        }
      }
    }
  }

  /**
   * 判断请求是否成功的响应代码
   *
   * @return 默认为 200
   */
  public String getResponseHeader(String name) {
    String value = null;
    if (httpResponseHeaders != null && httpResponseHeaders.length > 0) {
      for (Header h : httpResponseHeaders) {
        if (h.getName().equalsIgnoreCase(name)) return h.getValue();
      }
    }
    return value;
  }

  /**
   * 判断请求是否成功的响应代码
   *
   * @return 默认为 200
   */
  protected int getSuccessStatusCode() {
    return HttpStatus.SC_OK;
  }

  /**
   * 获取请求头
   */
  protected Map<String, String> getHeaders() {
    if (this.headers == null)
      this.headers = new HashMap<>();
    return this.headers;
  }

  /**
   * 解析响应
   */
  public abstract V parseResponse() throws Exception;

  public V getResponse() throws Exception {
    if (!hadParseResponse) {
      response = parseResponse();
      hadParseResponse = true;
    }
    return response;
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

  public CloseableHttpClient getHttpClient() {
    CloseableHttpClient c;
    if (this.group == null) {
      c = HttpClientFactory.create(this.httpOptions);
    } else {
      c = HttpClientFactory.get(this.group, this.httpOptions);
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
   */
  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public String getForceResponseEncoding() {
    return forceResponseEncoding;
  }

  public void setForceResponseEncoding(String forceResponseEncoding) {
    this.forceResponseEncoding = forceResponseEncoding;
  }

  /**
   * 获取POST请求需提交的表单参数
   */
  protected Map<String, String> getFormData() {
    if (this.formData == null) this.formData = new HashMap<>();
    return this.formData;
  }

  /**
   * 获取请求需提交的http参数
   */
  public Map<String, String> getHttpParams() {
    if (this.httpParams == null) this.httpParams = new HashMap<>();
    return httpParams;
  }

  /**
   * 添加一个表单参数
   *
   * @param key   键
   * @param value 值
   */
  public void addFormData(String key, String value) {
    if (this.formData == null) this.formData = new HashMap<>();
    this.formData.put(key, value);
  }

  /**
   * 添加表单参数
   */
  public void addFormData(Map<String, String> params) {
    if (params == null) return;
    if (this.formData == null) this.formData = new HashMap<>();
    this.formData.putAll(params);
  }

  /**
   * 添加请求参数
   *
   * @param key   键
   * @param value 值
   */
  public void addHttpParam(String key, String value) {
    if (this.httpParams == null) this.httpParams = new HashMap<>();
    this.httpParams.put(key, value);
  }

  /**
   * 添加请求参数
   */
  public void addHttpParam(Map<String, String> params) {
    if (params == null) return;
    if (this.httpParams == null) this.httpParams = new HashMap<>();
    this.httpParams.putAll(params);
  }

  /**
   * 添加请求头
   *
   * @param key   键
   * @param value 值
   */
  public void addHeader(String key, String value) {
    if (this.headers == null) this.headers = new HashMap<>();
    this.headers.put(key, value);
  }

  /**
   * 添加请求头
   */
  public void addHeader(Map<String, String> params) {
    if (params == null) return;
    if (this.headers == null) this.headers = new HashMap<>();
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
    StandardEvaluationContext context = new StandardEvaluationContext(getResponse());
    context.setVariable("httpParams", this.httpParams);
    context.setVariable("formData", this.formData);
    return context;
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
        r = new Result<>(false, (V) getFailedData());
      } else {
        r = new Result<>(true, parseResultData());
      }
      return r;
    } catch (Exception e) {
      logger.warn(e.getMessage(), e);
      return new Result<>(e);
    }
  }

  /**
   * 设置默认请求失败返回值
   *
   * @return 返回失败的异常信息
   */
  protected Result<V> defaultBadResult(HttpResponse response) {
    return new Result<>(new RuntimeException(
      "response is not ok. StatusCode=" + response.getStatusLine().getStatusCode()
        + ",Reason=" + response.getStatusLine().getReasonPhrase()));
  }

  protected Object getFailedData() throws IOException {
    return this.getResponseText();
  }

  /**
   * 解析通过 spel 获取的数据
   */
  protected V parseResultData() throws Exception {
    if (resultExpression != null) {
      return parseResultData(getExpressionValue(this.resultExpression, Object.class));
    } else {
      // 默认返回getResponse()的结果
      return parseResultData(getExpressionValue("#root", Object.class));
    }
  }

  /**
   * 获取响应的文本信息
   */
  protected String getResponseText() throws IOException {
    if (responseText == null) {
      responseText = entityToText(this.responseEntity);
      logger.debug("responseText={}", responseText);
      if (responseText == null) responseText = "";
    }
    return responseText;
  }

  /**
   * 解析响应实体为文本
   * <p>如果没有设置 forceResponseEncoding，使用响应体 Content-Type 头的编码设置进行解析，否则使用指定的 forceResponseEncoding 编码</p>
   *
   * @param entity the entity
   * @return the entity content as a String. or null if entity is null.
   */
  protected String entityToText(HttpEntity entity) throws IOException {
    if (entity == null) return null;
    if (this.getForceResponseEncoding() == null) {  // 默认编码进行解析
      return EntityUtils.toString(entity);
    } else {                                        // 特定编码进行解析
      InputStream in = this.responseEntity.getContent();
      if (in == null) return null;
      return FileCopyUtils.copyToString(new InputStreamReader(in, this.getForceResponseEncoding()));
    }
  }

  @SuppressWarnings("unchecked")
  protected V parseResultData(Object data) throws Exception {
    return (V) data;
  }

  /**
   * 判断请求是否成功
   */
  public Boolean isSuccess() {
    if (this.successExpression != null) return getExpressionValue(this.successExpression, Boolean.class);
    else return true;
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
   */
  public void export(OutputStream out) throws Exception {
    Object r = this.getResponse();
    if (r instanceof String) {
      FileCopyUtils.copy(r.toString(), new OutputStreamWriter(out));
    } else if (r instanceof InputStream) {
      FileCopyUtils.copy((InputStream) r, out);
    } else {
      throw new CoreException("unsupport type");
    }
  }

  public void setHttpOptions(JSONObject httpOptions) {
    this.httpOptions = httpOptions;
  }
}