/**
 * 
 */
package cn.bc.log.event;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import cn.bc.chat.service.OnlineUserService;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.web.SystemContext;
import cn.bc.log.domain.Syslog;
import cn.bc.log.service.SyslogService;
import cn.bc.log.web.struts2.SyslogAction;
import cn.bc.web.event.SessionDestroyedEvent;

/**
 * Session销毁的事件监听处理：记录超时日志及移除在线用户
 * 
 * @author dragon
 * 
 */
public class SessionDestroyedNotifier4Syslog implements
		ApplicationListener<SessionDestroyedEvent> {
	private static Log logger = LogFactory
			.getLog(SessionDestroyedNotifier4Syslog.class);
	private SyslogService syslogService;
	private OnlineUserService onlineUserService;
	private boolean trace;

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	@Autowired
	public void setSyslogService(SyslogService syslogService) {
		this.syslogService = syslogService;
	}

	@Autowired
	public void setOnlineUserService(OnlineUserService onlineUserService) {
		this.onlineUserService = onlineUserService;
	}

	public void onApplicationEvent(SessionDestroyedEvent event) {
		SystemContext context = (SystemContext) event.getContext();
		if (context != null) {// 表明之前用户已经处于登录状态
			ActorHistory userHistory = context.getUserHistory();
			if (logger.isDebugEnabled())
				logger.debug("session out with user=" + userHistory.getName()
						+ ",sid=" + event.getSession().getId());
			// 记录超时日志
			Calendar now = Calendar.getInstance();
			Syslog log = SyslogAction.buildSyslog(now,
					Syslog.TYPE_LOGIN_TIMEOUT, userHistory,
					userHistory.getName() + "登录超时", this.trace, null);
			this.syslogService.save(log);

			// 移除下线用户
			this.onlineUserService.remove(event.getSession().getId());
		} else {
			if (logger.isInfoEnabled())
				logger.info("session out without context. sid="
						+ event.getSession().getId());
		}
	}
}
