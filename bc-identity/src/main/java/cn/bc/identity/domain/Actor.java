/**
 * 
 */
package cn.bc.identity.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.bc.security.domain.Role;

/**
 * 参与者
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_IDENTITY_ACTOR")
public class Actor implements cn.bc.core.Entity<Long> {
	private static final long serialVersionUID = 1L;
	/** 类型:未定义 */
	public static final int TYPE_UNDEFINED = 0;
	/** 类型:用户 */
	public static final int TYPE_USER = 1;
	/** 类型:单位 */
	public static final int TYPE_UNIT = 2;
	/** 类型:部门 */
	public static final int TYPE_DEPARTMENT = 3;
	/** 类型:岗位或团队 */
	public static final int TYPE_GROUP = 4;

	public Actor(){
		
	}
	
	private Long id;
	private String uid;
	private int status = cn.bc.core.Entity.STATUS_ENABLED;
	private boolean inner = false;

	private String name;
	private String code;
	private int type = Actor.TYPE_UNDEFINED;
	private String email;
	private String phone;
	private String order;

	private ActorDetail detail;
	
	private Set<Role> roles;//拥有的角色列表

	@ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="BC_SECURITY_ROLE_ACTOR",
        joinColumns=
            @JoinColumn(name="AID", referencedColumnName="ID"),
        inverseJoinColumns=
            @JoinColumn(name="RID", referencedColumnName="ID")
        )
    @OrderBy("code asc")
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "UID_")
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "STATUS_")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@OneToOne(targetEntity = ActorDetail.class, cascade = CascadeType.ALL, optional = true)
	@JoinColumn(name = "DETAIL_ID", referencedColumnName = "ID")
	public ActorDetail getDetail() {
		return detail;
	}

	@Column(name = "ORDER_")
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public void setDetail(ActorDetail detail) {
		this.detail = detail;
	}

	@Transient
	public boolean isNew() {
		return getId() == null || getId() <= 0;
	}

	@Column(name = "INNER_")
	public boolean isInner() {
		return inner;
	}

	public void setInner(boolean inner) {
		this.inner = inner;
	}
}
