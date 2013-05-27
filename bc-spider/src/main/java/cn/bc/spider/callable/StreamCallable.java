package cn.bc.spider.callable;

import java.io.InputStream;

/**
 * 解析响应为流的网络请求
 * 
 * @author dragon
 * 
 */
public class StreamCallable extends BaseCallable<InputStream> {
	@Override
	public InputStream parseResponse() throws Exception {
		return this.responseEntity.getContent();
	}
}