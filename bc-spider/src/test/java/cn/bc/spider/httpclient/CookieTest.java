package cn.bc.spider.httpclient;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by dragon on 2015/4/14.
 */
public class CookieTest {
  private static Logger logger = LoggerFactory.getLogger(CookieTest.class);
  private static String URL = "http://nan.so/";        // 网址
  private static String DOMAIN = "nan.so";            // cookie的domain
  private static HttpHost PROXY = new HttpHost("127.0.0.1", 8888);// 代理

  @Test
  public void byHttpContext() throws Exception {
    BasicCookieStore cookieStore = new BasicCookieStore();
    BasicClientCookie cookie = new BasicClientCookie("by", "dragon");
    cookie.setDomain(DOMAIN);    // 这个必须设置否则此cookie不会添加到请求中
    cookieStore.addCookie(cookie);
    HttpContext localContext = new BasicHttpContext();
    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

    HttpGet request = new HttpGet(URL);
    HttpClient httpClient = getHttpClient();
    HttpResponse response = executeRequest(httpClient, request, localContext);

    logger.debug("StatusCode={}", response.getStatusLine());
    logger.debug("html={}", EntityUtils.toString(response.getEntity()));
  }

  @Test
  public void byHttpClient() throws Exception {
    BasicCookieStore cookieStore = new BasicCookieStore();
    BasicClientCookie cookie = new BasicClientCookie("by", "dragon");
    cookie.setDomain(DOMAIN);    // 这个必须设置否则此cookie不会添加到请求中
    cookieStore.addCookie(cookie);
    DefaultHttpClient httpClient = getHttpClient();
    httpClient.setCookieStore(cookieStore);

    HttpGet request = new HttpGet(URL);
    HttpResponse response = executeRequest(httpClient, request, null);

    logger.debug("StatusCode={}", response.getStatusLine());
    logger.debug("html={}", EntityUtils.toString(response.getEntity()));
  }

  @Test
  public void complex() throws Exception {
    // 1
    BasicCookieStore cookieStore = new BasicCookieStore();
    BasicClientCookie cookie = new BasicClientCookie("c1", "1111");
    cookie.setDomain(DOMAIN);    // 这个必须设置否则此cookie不会添加到请求中
    cookieStore.addCookie(cookie);
    DefaultHttpClient httpClient = getHttpClient();
    httpClient.setCookieStore(cookieStore);
    HttpGet request = new HttpGet(URL);
    HttpResponse response = executeRequest(httpClient, request, null);
    logger.debug("StatusCode={}", response.getStatusLine());
    logger.debug("html={}", EntityUtils.toString(response.getEntity()));

    // 2：第二次请求时添加一个新的自定义cookie
    cookie = new BasicClientCookie("c2", "2222");
    cookie.setDomain(DOMAIN);    // 这个必须设置否则此cookie不会添加到请求中
    cookieStore.addCookie(cookie);
    request = new HttpGet(URL);
    response = executeRequest(httpClient, request, null);
    logger.debug("StatusCode={}", response.getStatusLine());
    logger.debug("html={}", EntityUtils.toString(response.getEntity()));
  }

  @Test
  public void byHeader() throws Exception {
    HttpClient httpClient = getHttpClient();
    HttpGet request = new HttpGet(URL);
    request.setHeader("Cookie", "by=dragon");
    HttpResponse response = executeRequest(httpClient, request, null);
    logger.debug("StatusCode={}", response.getStatusLine());
    logger.debug("html={}", EntityUtils.toString(response.getEntity()));
  }

  private DefaultHttpClient getHttpClient() {
    DefaultHttpClient httpClient = new DefaultHttpClient();
    if (PROXY != null) {
      httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, PROXY);
    }
    return httpClient;
  }

  private HttpResponse executeRequest(HttpClient httpClient, HttpGet request, HttpContext localContext) throws IOException {
    if (localContext != null) {
      return httpClient.execute(request, localContext);
    } else {
      return httpClient.execute(request);
    }
  }
}
