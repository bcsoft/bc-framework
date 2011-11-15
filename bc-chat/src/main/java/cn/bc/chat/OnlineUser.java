package cn.bc.chat;

import cn.bc.web.ui.json.Json;

/**
 * 在线用户
 * 
 * @author dragon
 * 
 */
public class OnlineUser {
	private Long id;
	private String name;
	private String uid;
	private String fullName;
	private String code;
	private String ip;
	private String mac;
	private String session;
	private String browser;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	@Override
	public String toString() {
		return getJson();
	}

	public String getJson() {
		Json json = new Json();
		json.put("id", this.getId());
		json.put("uid", this.getUid());
		json.put("code", this.getCode());
		json.put("name", this.getName());
		json.put("fullName", this.getFullName());
		json.put("ip", this.getIp());
		json.put("mac", this.getMac());
		json.put("session", this.getSession());
		json.put("browser", this.getBrowser());
		return json.toString();
	}
}
