package cn.bc.spider.callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 解析响应为DOM的网络请求
 * 
 * @author dragon
 * 
 */
public class DomCallable extends BaseCallable<Document> {
	private static Log logger = LogFactory.getLog(DomCallable.class);

	@Override
	public Document parseResponse() throws Exception {
		String responseText = EntityUtils.toString(this.responseEntity);
		if (logger.isDebugEnabled()) {
			logger.debug("responeText=" + responseText);
		}
		Document doc = Jsoup.parse(responseText);
		return doc;
	}
}