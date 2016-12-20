package cn.bc.orm.jpa.dto;

import java.io.Serializable;

/**
 * 纯数据传输对象(Pure Data Transfer Object)
 * <p>No @Entity and @Id</p>
 *
 * @author dragon 2016-12-20
 */
public class PureDTO implements Serializable {
	private String code;
	private String myName; // 驼峰字段

	public PureDTO() {
	}

	public PureDTO(String code, String myName) {
		this.code = code;
		this.myName = myName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMyName() {
		return myName;
	}

	public void setMyName(String myName) {
		this.myName = myName;
	}
}
