package cn.bc.identity.event;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationEvent;

import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.web.util.WebUtils;

/**
 * 用户登录、注销系统的事件基类
 * 
 * @author dragon
 * 
 */
public class LogBaseEvent extends ApplicationEvent {
	private static final long serialVersionUID = 4588278294816284230L;
	private final Actor user;
	private final ActorHistory userHistory;
	private final String clientIp;
	private final String clientName;
	private final String clientInfo;
	private final String clientMac;
	private final String serverIp;
	private final String serverName;
	private final String serverInfo;
	private final String sid;
	private final String browser;

	private final HttpServletRequest request;

	/**
	 * @param source
	 *            事件源
	 * @param HttpRequest
	 *            请求
	 * @param user
	 *            用户
	 * @param userHistory
	 * @param sid
	 *            SID
	 */
	public LogBaseEvent(Object source, HttpServletRequest request, Actor user,
			ActorHistory userHistory, String sid) {
		super(source);

		this.request = request;
		String[] t = WebUtils.getClient(request);
		this.clientIp = t[0];
		this.clientName = t[1];
		this.clientInfo = t[2];
		this.clientMac = t[3];

		t = WebUtils.getServer(request);
		this.serverIp = t[0];
		this.serverName = t[1];
		this.serverInfo = t[2];
		this.sid = sid;
		this.browser = WebUtils.getBrowser(request);

		this.user = user;
		this.userHistory = userHistory;
	}

	public String getClientInfo() {
		return clientInfo;
	}

	public String getServerInfo() {
		return serverInfo;
	}

	public String getClientName() {
		return clientName;
	}

	public String getServerName() {
		return serverName;
	}

	public String getClientMac() {
		return clientMac;
	}

	/**
	 * 获取用户登录系统所用的浏览器信息
	 * 
	 * @return
	 */
	public String getBrowser() {
		return browser;
	}

	/**
	 * 获取会话的id
	 * 
	 * @return
	 */
	public String getSid() {
		return sid;
	}

	/**
	 * 获取登录客户端的ip地址信息
	 * 
	 * @return
	 */
	public String getClientIp() {
		return clientIp;
	}

	/**
	 * 获取登录到的服务器ip地址信息
	 * 
	 * @return
	 */
	public String getServerIp() {
		return serverIp;
	}

	/**
	 * 获取登录用户
	 * 
	 * @return
	 */
	public Actor getUser() {
		return user;
	}

	public ActorHistory getUserHistory() {
		return userHistory;
	}

	/**
	 * 获取请求信息
	 * 
	 * @return
	 */
	public HttpServletRequest getRequest() {
		return request;
	}
}
