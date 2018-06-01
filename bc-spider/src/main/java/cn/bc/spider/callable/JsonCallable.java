package cn.bc.spider.callable;

import org.json.JSONObject;

/**
 * 解析响应为json对象的网络请求
 *
 * @author dragon
 */
public class JsonCallable extends BaseCallable<JSONObject> {
  @Override
  public JSONObject parseResponse() throws Exception {
    return new JSONObject(getResponseText());
  }
}