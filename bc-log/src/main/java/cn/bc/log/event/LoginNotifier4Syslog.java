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

import cn.bc.chat.OnlineUser;
import cn.bc.chat.service.OnlineUserService;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.event.LoginEvent;
import cn.bc.log.domain.Syslog;
import cn.bc.log.service.SyslogService;
import cn.bc.log.web.struts2.SyslogAction;
import cn.bc.web.util.WebUtils;

/**
 * 用户登录事件的监听器：记录登录日志及在线用户
 * 
 * @author dragon
 * 
 */
public class LoginNotifier4Syslog implements ApplicationListener<LoginEvent> {
	private static Log logger = LogFactory.getLog(LoginNotifier4Syslog.class);
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

	public void onApplicationEvent(LoginEvent event) {
		Actor user = event.getUser();
		HttpServletRequest request = ServletActionContext.getRequest();
		String clientIp = WebUtils.getClientIP(request);
		if (logger.isDebugEnabled()) {
			String serverIp;
			try {
				InetAddress localhost = InetAddress.getLocalHost();
				serverIp = localhost.getHostAddress();
			} catch (UnknownHostException e) {
				serverIp = "UnknownHost";
			}
			String info = user.getName() + "登录系统";
			info += ",client=" + clientIp;
			info += ",server=" + serverIp;
			info += ",sid=" + request.getSession().getId();
			logger.debug(info);
		}

		// 记录登陆日志
		request = ServletActionContext.getRequest();
		Syslog log = SyslogAction
				.buildSyslog(Calendar.getInstance(), Syslog.TYPE_LOGIN,
						event.getUserHistory(), user.getName() + "登录系统",
						this.trace, ServletActionContext.getRequest());
		syslogService.save(log);

		// 记录在线用户
		OnlineUser onlineUser = new OnlineUser();
		onlineUser.setLoginTime(log.getFileDate());
		onlineUser.setId(user.getId());
		onlineUser.setUid(user.getUid());
		onlineUser.setName(user.getName());
		onlineUser.setCode(user.getCode());
		onlineUser.setPname(user.getPname());
		onlineUser.setFullName(user.getFullName());
		onlineUser.setIp(clientIp);
		onlineUser.setSid(request.getSession().getId());
		onlineUser.setBrowser(WebUtils.getBrowser(request));
		try {
			onlineUser.setMac(trace ? WebUtils.getMac(clientIp) : "no trace");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		this.onlineUserService.add(onlineUser);
	}
}
