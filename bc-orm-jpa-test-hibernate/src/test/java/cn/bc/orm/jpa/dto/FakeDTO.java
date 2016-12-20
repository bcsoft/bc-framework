package cn.bc.orm.jpa.dto;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 伪数据传输对象(Fake Data Transfer Object)
 * <p>Need @Entity and @Id</p>
 *
 * @author dragon 2016-12-20
 */
@Entity
public class FakeDTO implements Serializable {
	@Id
	public Long id;
	private String code;
	private String myName; // 驼峰字段

	public FakeDTO() {
	}

	public FakeDTO(String code, String myName) {
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
