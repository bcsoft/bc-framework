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
	/** 主动注销 */
	public static final Integer TYPE_LOGOUT = 1;
	/** 超时注销 */
	public static final Integer TYPE_LOGOUT2 = 2;

	private String userName;
	private Long departId;// 用户所在部门id
	private String departName;
	private Long unitId;// 用户所在单位id
	private String unitName;

	private String clientIp; // 用户机器的IP地址
	private String clientName; // 用户机器的名称
	private String clientInfo; // 用户浏览器的信息：User-Agent
	private String serverIp; // 服务器IP
	private String serverName; // 服务器机器名称
	private String serverInfo; // 服务器的信息

	@Column(name = "CREATER_NAME")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "DEPART_ID")
	public Long getDepartId() {
		return departId;
	}

	public void setDepartId(Long departId) {
		this.departId = departId;
	}

	@Column(name = "DEPART_NAME")
	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	@Column(name = "UNIT_ID")
	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	@Column(name = "UNIT_NAME")
	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
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