package cn.bc.spider.web.struts2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import cn.bc.core.exception.CoreException;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.spider.Result;
import cn.bc.spider.domain.SpiderConfig;
import cn.bc.spider.http.HttpClientFactory;
import cn.bc.spider.service.SpiderService;
import cn.bc.web.ui.html.page.PageOption;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 网络抓取Action
 *
 * @author dragon
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SpiderAction extends ActionSupport {
    private static final long serialVersionUID = 1L;
    private static final String LINK_SYMBLE = "：";
    private static Log logger = LogFactory.getLog(SpiderAction.class);
    private SpiderService spiderService;
    public String title;// 对话框的标题
    public String code;// 配置的编码
    public String auto;// 是否自动开始抓取:true|fale|不配置
    public String params;// 初始化参数的json字符串
    public PageOption pageOption;// 页面参数配置
    public String config;// 配置参数

    public SpiderAction() {
        this.title = this.getText("bc.spider");
    }

    @Autowired
    public void setSpiderService(SpiderService spiderService) {
        this.spiderService = spiderService;
    }

    @Override
    // 显示抓取页面
    public String execute() throws Exception {
        // 加载配置
        SpiderConfig c = this.spiderService.loadConfig(code);
        if (logger.isDebugEnabled()) {
            logger.debug("code=" + code);
            logger.debug("params=" + params);
            logger.debug("config=" + c.getConfig());
        }

        // 标题
        String title = c.get("title", String.class);
        if (title != null) {
            this.title = title;
        }

        // 参数配置
        this.config = c.getConfig();

        // 设置页面参数
        this.pageOption = new PageOption();
        this.pageOption.setWidth(600).setHeight(400);
        if (c.getConfigJson().has("width"))
            this.pageOption.setWidth(c.getConfigJson().getInt("width"));
        if (c.getConfigJson().has("height"))
            this.pageOption.setHeight(c.getConfigJson().getInt("height"));

        return SUCCESS;
    }

    public String group;// 分组
    public String url;// 获取验证码图片的请求地址
    public String json;

    // 获取验证码
    public String captcha() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("url=" + url);
            logger.debug("group=" + group);
        }
        JSONObject json = new JSONObject();

        try {
            // 附加当前用户的编码到group中,避免不同用户之间的查询互相影响
            String userCode = SystemContextHolder.get().getUser().getCode();

            // 获取验证码图片的文件路径
            String path = this.spiderService.getCaptcha(group + LINK_SYMBLE + userCode, url);
            if (logger.isDebugEnabled()) {
				logger.debug("path=" + path);
			}
            if (path == null) {
				throw new CoreException("can't get captcha file from " + url);
			} else {
				json.put("success", true);
				json.put("path", path);

				// 尝试破解验证码
				String captcha = crackCaptcha(path);
				if (captcha != null) {
					json.put("captcha", captcha);
				}
			}
        } catch (Exception e) {
            json.put("success", false);
            json.put("msg", e.getMessage());
        }

        this.json = json.toString();
        return "json";
    }

    /**
     * 破解验证码
     *
     * @param path
     * @return
     */
    private String crackCaptcha(String path) {
        return null;
    }

    // 执行抓取
    public String run() throws Exception {
        JSONObject json = new JSONObject();
        // 加载配置
        SpiderConfig c = this.spiderService.loadConfig(code);
        if (logger.isDebugEnabled()) {
            logger.debug("code=" + code);
            logger.debug("params=" + params);
            logger.debug("config=" + c.getConfig());
        }

        // 附加当前用户的编码到group中,避免不同用户之间的查询互相影响
        String _g = SystemContextHolder.get().getUser().getCode();
        JSONObject j = c.getConfigJson();
        _g = j.get("group") + LINK_SYMBLE + _g;
        j.put("group", _g);

        try {
            // 构建抓取参数
            Map<String, String> map = null;
            if (params != null) {
                JSONObject ps = new JSONObject(params);
                @SuppressWarnings("unchecked")
                Iterator<String> itor = ps.keys();
                map = new HashMap<String, String>();
                String key;
                while (itor.hasNext()) {
                    key = itor.next();
                    map.put(key, ps.getString(key));
                }
            }

            // 获取抓取结果
            Result<Object> r;
            r = this.spiderService.doSpide(c, map);
            if (logger.isDebugEnabled()) {
                logger.debug("Result.data=" + r.getData());
            }
            if (r.isSuccess()) {
                json.put("success", true);
                json.put("html", getDataString(r.getData()));
            } else {
                json.put("success", false);
                json.put("error", r.getError() != null ? r.getError()
                        .getMessage() : r.getData() != null ? r.getData()
                        : "no message");
                logger.warn(r.getError());
            }
        } catch (Exception e) {
            json.put("success", false);
            json.put("error", e.getMessage());
            logger.warn(e.getMessage(), e);
        }
        this.json = json.toString();

        // 用完就清掉缓存: TODO-内部自动控制缓存的时间
        HttpClientFactory.remove(_g);

        return "json";
    }

    @SuppressWarnings("rawtypes")
    private String getDataString(Object data) {
        if (data == null) {
            return "null";
        } else if (data.getClass().isArray()) {
            return StringUtils.arrayToCommaDelimitedString((Object[]) data);
        } else if (data instanceof Collection) {
            return StringUtils
                    .collectionToCommaDelimitedString((Collection) data);
        }

        return String.valueOf(data);
    }
}