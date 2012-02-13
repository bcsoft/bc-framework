/**
 * 
 */
package cn.bc.log.event;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.event.LoginEvent;
import cn.bc.log.domain.Syslog;
import cn.bc.log.service.SyslogService;

/**
 * 用户登录事件的监听器：记录登录日志
 * 
 * @author dragon
 * 
 */
public class LoginNotifier4Syslog implements ApplicationListener<LoginEvent> {
	private static Log logger = LogFactory.getLog(LoginNotifier4Syslog.class);
	private SyslogService syslogService;

	@Autowired
	public void setSyslogService(SyslogService syslogService) {
		this.syslogService = syslogService;
	}

	public void onApplicationEvent(LoginEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug("sid=" + event.getSid());
		}
		ActorHistory user = event.getUserHistory();

		// 记录登录日志
		Syslog log = new Syslog();
		log.setType(event.isRelogin() ? Syslog.TYPE_RELOGIN : Syslog.TYPE_LOGIN);
		log.setAuthor(event.getUserHistory());
		log.setFileDate(Calendar.getInstance());
		log.setSubject(user.getName() + "登录系统");
		log.setSid(event.getSid());

		log.setServerIp(event.getServerIp());
		log.setServerName(event.getServerName());
		log.setServerInfo(event.getServerInfo());

		log.setClientIp(event.getClientIp());
		log.setClientName(event.getClientName());
		log.setClientInfo(event.getClientInfo());
		log.setClientMac(event.getClientMac());

		syslogService.save(log);
	}
}
