/**
 * 
 */
package cn.bc.identity.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.bc.web.ui.json.Json;

/**
 * 认证信息的封装
 * 
 * @author dragon
 * 
 */
@Entity
@Table(name = "BC_IDENTITY_AUTH")
public class AuthData {
	private static final long serialVersionUID = 1L;
	private Long id;// 与Actor的id对应
	private String password;// 登录密码的加密信息

	@Id
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String toString() {
		return new Json().put("id", getId()).put("password", getPassword())
				.toString();
	}
}
