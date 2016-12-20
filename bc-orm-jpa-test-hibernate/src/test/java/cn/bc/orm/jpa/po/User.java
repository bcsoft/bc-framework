package cn.bc.orm.jpa.po;

import cn.bc.orm.jpa.dto.PureDTO;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 测试用 PO
 *
 * @author dragon 2016-12-20
 */
@Entity
@Table(name = "t_jpa_user")
@SqlResultSetMapping(
		name = "PureDTOMapperByConstructor",
		classes = @ConstructorResult(
				targetClass = PureDTO.class,
				columns = {
						@ColumnResult(name = "code", type = String.class),
						@ColumnResult(name = "name", type = String.class)
				}
		)
)
public class User extends IdEntity<Long> implements Serializable {
	private String code; // no get/set
	private String name;

	public User() {
	}

	public User(String code, String name) {
		this.code = code;
		this.name = name;
	}

	@Column(length = 10, unique = true)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "{id: " + getId() + ", code: " + code + ", name: " + getName() + "}";
	}
}