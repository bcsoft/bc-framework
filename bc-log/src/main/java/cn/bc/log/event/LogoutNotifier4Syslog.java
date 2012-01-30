/**
 * 
 */
package cn.bc.log.event;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import cn.bc.chat.service.OnlineUserService;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.event.LogoutEvent;
import cn.bc.log.domain.Syslog;
import cn.bc.log.service.SyslogService;
import cn.bc.log.web.struts2.SyslogAction;
import cn.bc.web.util.WebUtils;

/**
 * 用户退出系统的事件监听处理：记录退出日志及移除在线用户
 * 
 * @author dragon
 * 
 */
public class LogoutNotifier4Syslog implements ApplicationListener<LogoutEvent> {
	private static Log logger = LogFactory.getLog(LogoutNotifier4Syslog.class);
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

	public void onApplicationEvent(LogoutEvent event) {
		ActorHistory userHistory = event.getUserHistory();
		if (logger.isDebugEnabled()) {
			HttpServletRequest request = ServletActionContext.getRequest();
			String clientIp = WebUtils.getClientIP(request);
			String serverIp;
			try {
				InetAddress localhost = InetAddress.getLocalHost();
				serverIp = localhost.getHostAddress();
			} catch (UnknownHostException e) {
				serverIp = "UnknownHost";
			}
			String info = userHistory.getName() + "退出系统";
			info += ",client=" + clientIp;
			info += ",server=" + serverIp;
			logger.debug(info);
		}

		Calendar now = Calendar.getInstance();
		Syslog log = SyslogAction.buildSyslog(now, Syslog.TYPE_LOGOUT,
				userHistory, userHistory.getName() + "退出系统", this.trace,
				ServletActionContext.getRequest());
		syslogService.save(log);

		// 移除下线用户
		this.onlineUserService.remove(ServletActionContext.getRequest()
				.getSession().getId());
	}
}
