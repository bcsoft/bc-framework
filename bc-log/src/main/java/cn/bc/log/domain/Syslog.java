/**
 * 
 */
package cn.bc.log.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 系统日志
 * <p>
 * 主要用于记录用户登录、注销的信息及浏览器的信息
 * </p>
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_LOG_SYSTEM")
public class Syslog extends BaseLog {
	private static final long serialVersionUID = 1L;
	/** 登录 */
	public static final Integer TYPE_LOGIN = 0;
	/** 注销 */
	public static final Integer TYPE_LOGOUT = 1;
	/** 超时 */
	public static final Integer TYPE_LOGIN_TIMEOUT = 2;

	private String clientIp; // 用户机器的IP地址
	private String clientName; // 用户机器的名称
	private String clientInfo; // 用户浏览器的信息：User-Agent
	private String clientMac; // 客户端的mac地址
	private String serverIp; // 服务器IP
	private String serverName; // 服务器机器名称
	private String serverInfo; // 服务器的信息
	private String sid; // web会话id

	@Column(name = "C_MAC")
	public String getClientMac() {
		return clientMac;
	}

	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	@Column(name = "C_IP")
	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	@Column(name = "C_NAME")
	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	@Column(name = "C_INFO")
	public String getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(String clientBrowser) {
		this.clientInfo = clientBrowser;
	}

	@Column(name = "S_IP")
	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	@Column(name = "S_NAME")
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@Column(name = "S_INFO")
	public String getServerInfo() {
		return serverInfo;
	}

	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}
}