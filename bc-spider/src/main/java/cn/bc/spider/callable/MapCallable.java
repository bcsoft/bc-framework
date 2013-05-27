package cn.bc.spider.callable;

import java.util.Map;

import cn.bc.core.util.JsonUtils;

/**
 * 解析响应为Map的网络请求(Json转Map)
 * 
 * @author dragon
 * 
 */
public class MapCallable extends BaseCallable<Map<String, Object>> {
	@Override
	public Map<String, Object> parseResponse() throws Exception {
		return JsonUtils.toMap(getResponseText());
	}
}