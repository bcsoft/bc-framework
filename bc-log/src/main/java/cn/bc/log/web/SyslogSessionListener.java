/**
 * 
 */
package cn.bc.log.web;

import java.util.Calendar;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.chat.service.OnlineUserService;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.web.SystemContext;
import cn.bc.log.domain.Syslog;
import cn.bc.log.service.SyslogService;
import cn.bc.log.web.struts2.SyslogAction;
import cn.bc.web.util.WebUtils;

/**
 * session监听器,记录用户的超时日志
 * 
 * @author dragon
 * 
 */
public class SyslogSessionListener implements HttpSessionListener {
	protected Log logger = LogFactory.getLog(getClass());

	public void sessionCreated(HttpSessionEvent se) {
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		ActorHistory user = (ActorHistory) session
				.getAttribute(SystemContext.KEY_USER_HISTORY);
		if (user != null) {// 表明之前用户已经处于登录状态
			logger.info("session out with user:" + user.getName());
			// 记录超时日志
			Calendar now = Calendar.getInstance();
			Syslog log = SyslogAction.buildSyslog(now, Syslog.TYPE_LOGOUT2,
					user, user.getName() + "超时", false, null);
			WebUtils.getBean(SyslogService.class).save(log);

			// 移除下线用户
			WebUtils.getBean(OnlineUserService.class).remove(user.getId());
		}
	}
}
