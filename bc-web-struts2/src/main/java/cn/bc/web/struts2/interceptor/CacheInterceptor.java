/**
 * 
 */
package cn.bc.web.struts2.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsStatics;

import cn.bc.web.util.WebUtils;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

/**
 * 无缓存过滤器
 * 
 * @author dragon
 * 
 */
public class CacheInterceptor extends CanExcludeInterceptor {
	private static final long serialVersionUID = 1L;
	static Log logger = LogFactory.getLog(CacheInterceptor.class);

	/**
	 * 缓存时间配置，适合http1.0以上的缓存参数
	 * <p>
	 * 正数单位为分钟，负数单位为月
	 * </p>
	 * <ul>
	 * <li>为0代表禁用缓存</li>
	 * <li>为正数代表缓存多少分钟</li>
	 * <li>为负数代表缓存多少个月（按平均每月30.5天机算）</li>
	 * </ul>
	 */
	private long expires;

	public long getExpires() {
		return expires;
	}

	public void setExpires(long expires) {
		this.expires = expires;

		// 初始化缓存配置：分钟转换为毫秒
		if (expires < 0) {// 缓存多少个月（按平均每月30.5天机算）
			expires = -1 * 2635200000l;// 30.5 * 24 * 60 * 60 * 1000
		} else {// 缓存多少分钟
			expires = expires * 60000l;
		}
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		if(logger.isDebugEnabled())
			logger.debug("expires=" + getExpires());
		ActionContext context = invocation.getInvocationContext();
		HttpServletResponse response = (HttpServletResponse) context
				.get(StrutsStatics.HTTP_RESPONSE);
		HttpServletRequest request = (HttpServletRequest) context
				.get(StrutsStatics.HTTP_REQUEST);

		// 排除处理
		if (!isExcludePath(request)) {
			if(logger.isInfoEnabled())
				logger.info("set Cache-Control for path=" + WebUtils.getResourcePath(request) + ",expires=" + getExpires());
			// 缓存控制
			if (expires != 0) {
				long date = new java.util.Date().getTime();
				response.setDateHeader("Last-Modified", date);
				response.setDateHeader("Expires", date + expires);
				response.setHeader("Cache-Control", "public");
				response.setHeader("Pragma", "Pragma");
			} else {// 不允许浏览器端缓存当前页面信息
				response.setHeader("Pragma", "No-cache");
				response.setHeader("Cache-Control", "no-cache");
				response.setDateHeader("Expires", 0);
			}
		}else{
			if(logger.isDebugEnabled())
				logger.debug("isExcludePath=true in path=" + WebUtils.getResourcePath(request));
		}

		return invocation.invoke();
	}
}
