package cn.bc.spider.service;

import cn.bc.core.exception.CoreException;
import cn.bc.core.util.JsonUtils;
import cn.bc.spider.CaptchaImageCallable;
import cn.bc.spider.Result;
import cn.bc.spider.callable.SpiderConfigCallable;
import cn.bc.spider.dao.SpiderConfigDao;
import cn.bc.spider.domain.SpiderConfig;
import cn.bc.spider.http.TaskExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 报表模板Service接口的实现
 *
 * @author dragon
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

    public Result<Object> doSpide(SpiderConfig config, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<String, String>();
        }
        Result<Object> r;
        if (config == null)
            throw new CoreException("config can't be null!");

        // 如果有前置抓取配置就先执行前置抓取: 如有的抓取需要先登录
        if (config.has("prev")) {
            // 获取前置配置
            Object prev = config.get("prev");
            SpiderConfig prevConfig;
            Map<String, String> prevParams = params;
            boolean parsePrevRespone = false;
            if (prev instanceof String) {
                prevConfig = this.loadConfig((String) prev);
            } else if (prev instanceof JSONObject) {
                try {
                    JSONObject prevJson = (JSONObject) prev;
                    prevConfig = this.loadConfig(prevJson.getString("code"));

                    // 挑选转换指定的参数，格式：oldKey[上一抓取的参数Key]:newKey[本次抓取使用的Key]:urlencode[参数值使用的URL编码，如UTF-8]
                    if (prevJson.has("transformParams") && params != null) {
                        prevParams = new HashMap<String, String>();
                        String[] transformParams = prevJson.getString("transformParams").split(",");
                        String[] onu;
                        String oldKey, newKey, encode = null;
                        for (String key : transformParams) {
                            onu = key.split(":");// oldKey:newKey:encode
                            oldKey = onu[0];
                            if (onu.length > 1) {// 有newKey
                                newKey = onu[1];
                                if (onu.length > 2) {// 有urlencode
                                    encode = onu[2];
                                }
                            } else {
                                newKey = oldKey;
                            }
                            if (params.containsKey(oldKey)) {
                                try {
                                    String newValue = encode == null ? params.get(oldKey) : URLEncoder.encode(params.get(oldKey), encode);
                                    prevParams.put(newKey, newValue);
                                } catch (UnsupportedEncodingException e) {
                                    throw new CoreException(
                                            "UnsupportedEncodingException for prev config: code="
                                                    + config.getCode() + ",encode=" + encode, e);
                                }
                            }
                        }
                    }
                    if (prevJson.has("parse") && prevJson.getBoolean("parse")) {// 需要解析prev的结果形成新的参数传递下去
                        parsePrevRespone = true;
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

            // 解析前置执行结果作为参数继续传递
            if (parsePrevRespone) {
                Object prevDate = r.getData();
                if (prevDate instanceof String) {
                    Map<String, Object> addParams = JsonUtils.toMap((String) prevDate);
                    for (Map.Entry<String, Object> e : addParams.entrySet()) {
                        params.put(e.getKey(), String.valueOf(e.getValue()));
                    }
                }
            }
        }

        // 按配置的需要对参数值进行预编码
        encodeParamsValue(config, params);

        // 执行主抓取
        Callable<Result<Object>> call = new SpiderConfigCallable(config, params);
        r = TaskExecutor.get(call);
        return r;
    }

    // 按配置的需要对参数值进行预编码
    private void encodeParamsValue(SpiderConfig config, Map<String, String> params) {
        if (!config.has("formData") || params.isEmpty())
            return;
        JSONArray formData = (JSONArray) config.get("formData");
        JSONObject json;
        try {
            for (int i = 0; i < formData.length(); i++) {
                json = formData.getJSONObject(i);
                if (json.has("encode")) {
                    for (Map.Entry<String, String> e : params.entrySet()) {
                        if (e.getKey().equals(json.getString("name"))) {
                            try {
                                // 将值进行指定编码
                                params.put(e.getKey(), URLEncoder.encode(e.getValue(), json.getString("encode")));
                            } catch (UnsupportedEncodingException e1) {
                                throw new CoreException("UnsupportedEncodingException:encode=" + json.getString("encode"), e1);
                            }
                            break;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            throw new CoreException("JSONException", e);
        }
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