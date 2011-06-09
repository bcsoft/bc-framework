package cn.bc.orm.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 仅用于测试时方便构建测试对象
 * 
 * @author dragon
 * 
 */
@Entity
@Table(name = "ZTEST_EXAMPLE")
//@NamedQueries(value = { @NamedQuery(name = "Example.findAllUsers", query = "from Example") })
public class Domain implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id = null;
	@Column(name = "CODE")
	private String code;
	@Column(name = "NAME", nullable = false)
	private String name;

	public Domain() {
	}

	public Domain(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public boolean isNew(){
		return this.id == null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
