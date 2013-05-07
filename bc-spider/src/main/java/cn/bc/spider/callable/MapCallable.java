package cn.bc.spider.callable;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.EntityUtils;

import cn.bc.core.util.JsonUtils;

/**
 * 解析响应为Map的网络请求(Json转Map)
 * 
 * @author dragon
 * 
 */
public class MapCallable extends BaseCallable<Map<String, Object>> {
	private static Log logger = LogFactory.getLog(MapCallable.class);

	@Override
	public Map<String, Object> parseResponse() throws Exception {
		String responseText = EntityUtils.toString(this.responseEntity);
		if (logger.isDebugEnabled()) {
			logger.debug("responeText=" + responseText);
		}
		return JsonUtils.toMap(responseText);
	}
}