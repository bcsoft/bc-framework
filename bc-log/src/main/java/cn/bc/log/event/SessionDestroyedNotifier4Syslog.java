/**
 * 
 */
package cn.bc.log.event;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import cn.bc.identity.event.LoginEvent;
import cn.bc.identity.web.SystemContext;
import cn.bc.log.domain.Syslog;
import cn.bc.log.service.SyslogService;
import cn.bc.web.event.SessionDestroyedEvent;

/**
 * Session销毁的事件监听处理：记录超时日志
 * 
 * @author dragon
 * 
 */
public class SessionDestroyedNotifier4Syslog implements
		ApplicationListener<SessionDestroyedEvent> {
	private static Log logger = LogFactory
			.getLog(SessionDestroyedNotifier4Syslog.class);
	private SyslogService syslogService;

	@Autowired
	public void setSyslogService(SyslogService syslogService) {
		this.syslogService = syslogService;
	}

	public void onApplicationEvent(SessionDestroyedEvent event) {
		SystemContext context = (SystemContext) event.getContext();
		if (context != null) {// 表明之前用户已经处于登录状态
			LoginEvent loginEvent = (LoginEvent) event.getSession()
					.getAttribute("loginEvent");
			if (logger.isDebugEnabled())
				logger.debug("session out with user="
						+ context.getUserHistory().getName() + ",sid="
						+ event.getSession().getId());
			// 记录超时日志
			Syslog log = new Syslog();
			log.setType(Syslog.TYPE_LOGIN_TIMEOUT);
			log.setAuthor(context.getUserHistory());
			log.setFileDate(Calendar.getInstance());
			log.setSubject(context.getUserHistory().getName() + "登录超时");
			log.setSid(event.getSid());

			log.setServerIp(loginEvent.getServerIp());
			log.setServerName(loginEvent.getServerName());
			log.setServerInfo(loginEvent.getServerInfo());

			log.setClientIp(loginEvent.getClientIp());
			log.setClientName(loginEvent.getClientName());
			log.setClientInfo(loginEvent.getClientInfo());
			log.setClientMac(loginEvent.getClientMac());

			this.syslogService.save(log);
		} else {
			if (logger.isDebugEnabled())
				logger.debug("session out without context. sid="
						+ event.getSession().getId());
		}
	}
}
