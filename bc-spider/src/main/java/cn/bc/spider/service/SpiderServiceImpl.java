package cn.bc.spider.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.exception.CoreException;
import cn.bc.spider.CaptchaImageCallable;
import cn.bc.spider.Result;
import cn.bc.spider.callable.SpiderConfigCallable;
import cn.bc.spider.dao.SpiderConfigDao;
import cn.bc.spider.domain.SpiderConfig;
import cn.bc.spider.http.TaskExecutor;

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
}