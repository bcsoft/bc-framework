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

import cn.bc.identity.domain.Actor;
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
		Actor user = (Actor) session.getAttribute("user");
		if (user != null) {// 表明之前用户已经处于登录状态
			Actor belong = (Actor) session.getAttribute("belong");
			Actor unit = (Actor) session.getAttribute("unit");
			Calendar now = Calendar.getInstance();
			Syslog log = SyslogAction.buildSyslog(now, Syslog.TYPE_LOGOUT2,
					user, belong, unit, user.getName() + "超时注销", false, null);
			WebUtils.getSpringBean(SyslogService.class).save(log);
		}
	}
}
