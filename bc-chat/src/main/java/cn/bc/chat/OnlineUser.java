package cn.bc.chat;

import java.util.Calendar;

import cn.bc.core.util.DateUtils;
import cn.bc.web.ui.json.Json;

/**
 * 在线用户
 * 
 * @author dragon
 * 
 */
public class OnlineUser {
	private Calendar loginTime;
	private Long id;
	private String name;
	private String uid;
	private String pname;
	private String fullName;
	private String code;
	private String ip;
	private String mac;
	private String sid;
	private String browser;

	public Calendar getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Calendar loginTime) {
		this.loginTime = loginTime;
	}

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

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
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

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	@Override
	public String toString() {
		return getJson();
	}

	public String getJson() {
		Json json = new Json();
		json.put("loginTime",
				DateUtils.formatDateTime(this.getLoginTime().getTime()));
		json.put("id", this.getId());
		json.put("uid", this.getUid());
		json.put("code", this.getCode());
		json.put("name", this.getName());
		json.put("pname", this.getPname());
		json.put("fullName", this.getFullName());
		json.put("ip", this.getIp());
		json.put("mac", this.getMac());
		json.put("sid", this.getSid());
		json.put("browser", this.getBrowser());
		return json.toString();
	}
}
