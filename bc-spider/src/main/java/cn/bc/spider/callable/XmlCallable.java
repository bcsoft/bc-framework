package cn.bc.spider.callable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 解析响应为Xml的网络请求
 * 
 * @author dragon
 * 
 */
public class XmlCallable extends BaseCallable<Document> {
	private final static Logger logger = LoggerFactory.getLogger(XmlCallable.class);
	@Override
	public Document parseResponse() throws Exception {
		String xml = getResponseText();
		logger.debug("response xml={}", xml);
		return Jsoup.parse(xml, "", Parser.xmlParser());
	}
}