package cn.bc.spider.callable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 解析响应为DOM的网络请求
 *
 * @author dragon
 */
public class DomCallable extends BaseCallable<Document> {
  @Override
  public Document parseResponse() throws Exception {
    return Jsoup.parse(getResponseText());
  }
}