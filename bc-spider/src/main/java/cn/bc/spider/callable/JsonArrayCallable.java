package cn.bc.spider.callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

/**
 * 解析响应为json数组的网络请求
 * 
 * @author dragon
 * 
 */
public class JsonArrayCallable extends BaseCallable<JSONArray> {
	private static Log logger = LogFactory.getLog(JsonArrayCallable.class);

	@Override
	public JSONArray parseResponse() throws Exception {
		String responseText = EntityUtils.toString(this.responseEntity);
		if (logger.isDebugEnabled()) {
			logger.debug("responeText=" + responseText);
		}
		return new JSONArray(responseText);
	}
}