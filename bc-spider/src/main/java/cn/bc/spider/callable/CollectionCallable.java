package cn.bc.spider.callable;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.EntityUtils;

import cn.bc.core.util.JsonUtils;

/**
 * 解析响应为集合的网络请求(JsonArray转Collection)
 * 
 * @author dragon
 * 
 */
public class CollectionCallable extends BaseCallable<Collection<Object>> {
	private static Log logger = LogFactory.getLog(CollectionCallable.class);

	@Override
	public Collection<Object> parseResponse() throws Exception {
		String responseText = EntityUtils.toString(this.responseEntity);
		if (logger.isDebugEnabled()) {
			logger.debug("responeText=" + responseText);
		}
		return JsonUtils.toCollection(responseText);
	}
}