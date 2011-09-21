/**
 * 
 */
package cn.bc.web.struts2.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsStatics;

import cn.bc.web.util.WebUtils;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

/**
 * 认证访问过滤器
 * 
 * @author dragon
 * 
 */
public class AuthIterceptor extends CanExcludeInterceptor {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(AuthIterceptor.class);
	private String authKey;

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		if (logger.isDebugEnabled())
			logger.debug("authKey=" + getAuthKey());
		ActionContext context = invocation.getInvocationContext();
		HttpServletRequest request = (HttpServletRequest) context
				.get(StrutsStatics.HTTP_REQUEST);

		// 排除处理
		if (!isExcludePath(request)) {
			Map<String, Object> session = context.getSession();
			Object auth = session.get(getAuthKey());
			if (auth == null) {
				context.put("bcauth", "false");
				return Action.LOGIN;
			} else {
				return invocation.invoke();
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("isExcludePath=true in path="
						+ WebUtils.getResourcePath(request));
			}
		}

		return invocation.invoke();
	}
}
