package cn.bc.spider.callable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

/**
 * 解析响应为Xml的网络请求
 * 
 * @author dragon
 * 
 */
public class XmlCallable extends BaseCallable<Document> {
	@Override
	public Document parseResponse() throws Exception {
		return Jsoup.parse(getResponseText(), "", Parser.xmlParser());
	}
}