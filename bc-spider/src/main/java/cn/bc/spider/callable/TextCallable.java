package cn.bc.spider.callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 解析响应为文本的网络请求
 * 
 * @author dragon
 * 
 */
public class TextCallable extends BaseCallable<String> {
	private static Log logger = LogFactory.getLog(TextCallable.class);

	@Override
	public String parseResponse() throws Exception {
		String responseText = getResponeText();
		if (logger.isDebugEnabled()) {
			logger.debug("responeText=" + responseText);
		}
		return responseText;
	}
}