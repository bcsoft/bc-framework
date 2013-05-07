package cn.bc.spider.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.exception.CoreException;
import cn.bc.spider.CaptchaImageCallable;
import cn.bc.spider.Result;
import cn.bc.spider.callable.SpiderConfigCallable;
import cn.bc.spider.dao.SpiderConfigDao;
import cn.bc.spider.domain.SpiderConfig;

/**
 * 报表模板Service接口的实现
 * 
 * @author dragon
 * 
 */
public class SpiderServiceImpl implements SpiderService {
	private static Log logger = LogFactory.getLog(SpiderServiceImpl.class);
	private SpiderConfigDao spiderConfigDao;
	// private static Map<String, Parser> parsers;
	// private static Map<String, SpiderConfig> configs;

	static {
		// parsers = new HashMap<String, Parser>();
		// parsers.put("wscgs_unlogin", new WSCGSUnloginParser());
	}

	public SpiderServiceImpl() {
		init();
	}

	@Autowired
	public void setSpiderConfigDao(SpiderConfigDao spiderConfigDao) {
		this.spiderConfigDao = spiderConfigDao;
	}

	private void init() {
		// configs = new HashMap<String, SpiderConfig>();
		// SpiderConfig c = getWSCGSConfig();
		// configs.put(c.getCode(), c);
	}

	public SpiderConfig loadConfig(String code) {
		return this.spiderConfigDao.loadByCode(code);
		// return getWSCGSConfig();
	}

	public Result<Object> doSpide(SpiderConfig config,
			final Map<String, String> params) {
		Result<Object> r;
		if (config == null)
			throw new CoreException("config can't be null!");

		// 如果有前置抓取配置就先执行前置抓取: 如有的抓取需要先登录
		if (config.has("prev")) {
			// 获取前置配置
			Object prev = config.get("prev");
			SpiderConfig prevConfig;
			Map<String, String> prevParams = params;
			if (prev instanceof String) {
				prevConfig = this.loadConfig((String) prev);
			} else if (prev instanceof JSONObject) {
				try {
					prevConfig = this.loadConfig(((JSONObject) prev)
							.getString("code"));

					// 挑选指定的参数
					if (prevConfig.has("params") && params != null) {
						prevParams = new HashMap<String, String>();
						String[] chooseKeys = prevConfig.get("params",
								String.class).split(",");
						for (String key : chooseKeys) {
							if (params.containsKey(key)) {
								prevParams.put(key, params.get(key));
							}
						}
					}
				} catch (JSONException e) {
					throw new CoreException(
							"JSONException for prev config: code="
									+ config.getCode(), e);
				}
			} else {
				throw new CoreException("unsupport prev config type: prev="
						+ prev);
			}
			if (prevConfig == null) {// 找不到前置配置就抛异常
				throw new CoreException("can't find prev config: code="
						+ config.getCode() + ",prev=" + prev);
			}
			try {
				// 同一group保证session相同
				prevConfig.getConfigJson().put("group", config.get("group"));
			} catch (JSONException e) {
				throw new CoreException("JSONException", e);
			}

			// 执行前置配置
			r = this.doSpide(prevConfig, prevParams);

			// 如果前置配置抓取失败就直接返回失败的抓取结果
			if (!r.isSuccess()) {
				return r;
			}
		}

		// 执行主抓取
		Callable<Result<Object>> call = new SpiderConfigCallable(config, params);
		r = TaskExecutor.get(call);
		return r;
	}

	public String getCaptcha(String group, String url) {
		init();
		CaptchaImageCallable c = new CaptchaImageCallable();
		c.setGroup(group);
		c.setUrl(url);
		Result<String> r = TaskExecutor.get(c);
		if (r.isSuccess()) {
			logger.info("captcha file=" + r.getData());
			return r.getData();
		} else {
			return null;// Attach.DATA_REAL_PATH + "/spider/error.jpg";
		}
	}

	// TODO
	private SpiderConfig getWSCGSConfig() {
		SpiderConfig c = new SpiderConfig();
		c.setCode("wscgs-jtwf-unlogin");

		JSONObject json = new JSONObject();
		try {
			json.put("group", "wscgs_unlogin");
			json.put("method", "post");
			json.put("title", "网上车管所交通违法查询");// 对话框标题
			json.put("url",
					"http://www.gzjd.gov.cn/cgs/violation/getVisitorVioList.htm");
			json.put("captchaUrl", "http://www.gzjd.gov.cn/cgs/captcha.jpg");

			json.put("type", "map");// 响应的类型：json、html、stream:jpg、...
			json.put("successExpression", "#root.containsKey(\"returnCode\")");
			json.put("resultExpression", "#root.get(\"data\")");
			json.put("parser", "tpl:ui_wscgs_jtwf(unlogin)");

			// 表单要提交的参数定义
			JSONArray formData = new JSONArray();
			JSONObject field;
			field = new JSONObject();
			formData.put(field);
			field.put("name", "platenumtype");// 参数名称
			field.put("label", "号牌种类");// 参数标签
			field.put("value", "02");// 默认值
			field.put("option", true);// 是否可选

			field = new JSONObject();
			formData.put(field);
			field.put("name", "platenum");
			field.put("label", "车牌号");// 参数标签
			field.put("validate", "required");// 验证配置:对应表单的 data-validate配置格式
			field.put("title", "格式为:粤AS3H54");// 鼠标提示信息

			field = new JSONObject();
			formData.put(field);
			field.put("name", "engineno");
			field.put("option", true);

			field = new JSONObject();
			formData.put(field);
			field.put("name", "vehicleidnum");
			field.put("label", "日期测试");
			field.put("option", true);
			JSONObject validate = new JSONObject();
			validate.put("type", "date");
			validate.put("required", false);
			field.put("validate", validate);
			field.put("clazz", "bc-date");

			field = new JSONObject();
			formData.put(field);
			field.put("name", "captchaId");
			field.put("label", "验证码");
			field.put("captcha", true);
			field.put("option", false);

			json.put("formData", formData);

			JSONObject headers = new JSONObject();
			json.put("headers", headers);
			headers.put("Host", "www.gzjd.gov.cn");
			headers.put("Origin", "http://www.gzjd.gov.cn");
			headers.put("Referer",
					"http://www.gzjd.gov.cn/cgs/html/violation/visitor_violation.shtml");
			headers.put("X-Requested-With", "XMLHttpRequest");
			headers.put("Accept",
					"application/json, text/javascript, */*; q=0.01");
			headers.put(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");

			c.setConfig(json.toString());
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			throw new CoreException(e);
		}
		return c;
	}
}