package cn.bc.spider;

import java.util.Map;

/**
 * 简易帐号密码登录
 * 
 * @author dragon
 * 
 */
public class SimpleLoginCallable extends HttpClientCallable<Boolean> {
	private String key4userName;// 帐号key
	private String key4password;// 密码key
	private String userName;// 帐号
	private String password;// 密码

	public SimpleLoginCallable() {
		// 默认登录使用post
		this.setMethod("post");
	}

	@Override
	protected Map<String, String> getFormData() {
		Map<String, String> formData = super.getFormData();

		// 添加帐号和密码参数
		if (userName != null)
			formData.put(key4userName, userName);
		if (password != null)
			formData.put(key4password, password);

		return formData;
	}

	public String getKey4userName() {
		return key4userName;
	}

	public void setKey4userName(String key4userName) {
		this.key4userName = key4userName;
	}

	public String getKey4password() {
		return key4password;
	}

	public void setKey4password(String key4password) {
		this.key4password = key4password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}