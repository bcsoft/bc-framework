package cn.bc.spider.callable;

import org.json.JSONArray;

/**
 * 解析响应为json数组的网络请求
 * 
 * @author dragon
 * 
 */
public class JsonArrayCallable extends BaseCallable<JSONArray> {
	@Override
	public JSONArray parseResponse() throws Exception {
		return new JSONArray(getResponseText());
	}
}