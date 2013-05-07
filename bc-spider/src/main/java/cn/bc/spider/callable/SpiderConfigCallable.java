package cn.bc.spider.callable;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.util.Assert;

import cn.bc.core.util.JsonUtils;
import cn.bc.core.util.SpringUtils;
import cn.bc.spider.Result;
import cn.bc.spider.domain.SpiderConfig;
import cn.bc.spider.parser.JSONConfigParser;
import cn.bc.spider.parser.Parser;
import cn.bc.spider.parser.TemplateParser;

/**
 * 根据配置的网络请求
 * 
 * @author dragon
 * 
 */
public class SpiderConfigCallable implements Callable<Result<Object>> {
	private static Log logger = LogFactory.getLog(SpiderConfigCallable.class);
	private SpiderConfig config;// 配置
	@SuppressWarnings("rawtypes")
	private BaseCallable inner;// 代理
	private Map<String, String> params;// 附加的请求参数

	public SpiderConfigCallable(SpiderConfig config) {
		Assert.notNull(config, "config can't be null.");
		this.config = config;
		this.init();
	}

	public SpiderConfigCallable(SpiderConfig config, Map<String, String> params) {
		Assert.notNull(config, "config can't be null.");
		this.config = config;
		this.params = params;
		this.init();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result<Object> call() throws Exception {
		try {
			Result<Object> r = (Result<Object>) inner.call();
			if (config.has("parser")) {// 对数据结果进行二次解析转换
				Object parser = config.get("parser");
				if (logger.isDebugEnabled())
					logger.debug("parser=" + parser);
				Parser p = null;
				if (parser instanceof String) {// 从spring上下文中取定义的bean
					if (((String) parser).startsWith("tpl:")) {// 基于模版编码的TemplateParser实例
						p = new TemplateParser(((String) parser).substring(4));
					} else {// 从spring上下文中取定义的bean
						p = SpringUtils.getBean((String) parser, Parser.class);
					}
				} else if (parser instanceof JSONObject) {// 根据json配置自动生成
					p = new JSONConfigParser((JSONObject) parser);
				} else if (parser instanceof Parser) {
					p = (Parser) parser;
				}
				if (p != null) {
					r.setData(p.parse(r.getData()));
				}
			}
			return r;
		} catch (Exception e) {
			return new Result<Object>(e);
		}
	}

	/**
	 * 根据配置初始化实际的 Callable
	 */
	@SuppressWarnings("unchecked")
	private void init() {
		String type = config.get("type", String.class);// 响应类型
		if ("collection".equalsIgnoreCase(type)
				|| "list".equalsIgnoreCase(type)) {// 集合
			inner = new CollectionCallable();
		} else if ("map".equalsIgnoreCase(type)) {
			inner = new MapCallable();
		} else if ("json".equalsIgnoreCase(type)) {
			inner = new JsonCallable();
		} else if ("dom".equalsIgnoreCase(type)) {
			inner = new DomCallable();
		} else if ("jsonArray".equalsIgnoreCase(type)
				|| "jsons".equalsIgnoreCase(type)) {
			inner = new JsonArrayCallable();
		} else if ("text".equalsIgnoreCase(type)
				|| "html".equalsIgnoreCase(type)
				|| "string".equalsIgnoreCase(type) || type == null) {
			inner = new TextCallable();
		} else if ("stream".equalsIgnoreCase(type)) {
			inner = new StreamCallable();
		} else if (type.startsWith("spring:")) {// 使用spring中配置的bean
			inner = SpringUtils.getBean(type.substring("spring:".length()),
					BaseCallable.class);
		} else {// 默认为抓取文本信息
			inner = new TextCallable();
		}

		// 设置相关参数
		if (config.has("group"))
			inner.setGroup(config.get("group", String.class));
		inner.setUrl(config.get("url", String.class));
		if (config.has("method"))
			inner.setMethod(config.get("method", String.class));
		if (config.has("encoding"))
			inner.setEncoding(config.get("encoding", String.class));
		if (config.has("userAgent"))
			inner.setUserAgent(config.get("userAgent", String.class));
		if (config.has("successExpression"))
			inner.setSuccessExpression(config.get("successExpression",
					String.class));
		if (config.has("resultExpression"))
			inner.setResultExpression(config.get("resultExpression",
					String.class));
		if (config.has("type"))
			inner.setType(type);

		// 附加请求参数
		inner.addFormData(params);

		// 附加请求头
		if (config.has("headers")) {
			Map<String, Object> m = JsonUtils.toMap(config.get("headers",
					JSONObject.class).toString());
			for (Entry<String, Object> e : m.entrySet()) {
				inner.addHeader(e.getKey(), String.valueOf(e.getValue()));
			}
		}
	}
}