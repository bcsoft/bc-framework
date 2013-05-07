package cn.bc.spider.callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * 解析响应为json对象的网络请求
 * 
 * @author dragon
 * 
 */
public class JsonCallable extends BaseCallable<JSONObject> {
	private static Log logger = LogFactory.getLog(JsonCallable.class);

	@Override
	public JSONObject parseResponse() throws Exception {
		String responseText = EntityUtils.toString(this.responseEntity);
		if (logger.isDebugEnabled()) {
			logger.debug("responeText=" + responseText);
		}
		return new JSONObject(responseText);
	}
}