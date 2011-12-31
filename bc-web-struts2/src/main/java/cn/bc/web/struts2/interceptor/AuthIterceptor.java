/**
 * 
 */
package cn.bc.web.struts2.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsStatics;

import cn.bc.Context;
import cn.bc.ContextHolder;
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
		if (authKey == null)
			authKey = Context.KEY;
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
			HttpSession session = request.getSession();
			Object auth = session.getAttribute(authKey);
			if (auth == null) {
				context.put("bcauth", "false");
				return Action.LOGIN;
			} else {
				// 将系统上下文设置到线程变量
				this.initContextHolder(request.getSession());
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

	/**
	 * 初始化线程变量保持器
	 * 
	 * @param session
	 */
	protected void initContextHolder(HttpSession session) {
		Context curContext = (Context) session.getAttribute(Context.KEY);
		if (curContext != null) {
			Context holderContext = ContextHolder.get();
			if (holderContext == null || !curContext.equals(holderContext)) {// 如果还没有设置过才设置
				ContextHolder.set(curContext);
			}
		}
	}
}
