package cn.bc.spider.callable;

/**
 * 解析响应为文本的网络请求
 * 
 * @author dragon
 * 
 */
public class TextCallable extends BaseCallable<String> {
	@Override
	public String parseResponse() throws Exception {
		return getResponseText();
	}
}