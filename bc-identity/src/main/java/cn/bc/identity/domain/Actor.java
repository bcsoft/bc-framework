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

import org.springframework.util.StringUtils;

import cn.bc.core.util.PinYinUtils;

/**
 * 参与者
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_IDENTITY_ACTOR")
public class Actor implements cn.bc.core.RichEntity<Long> {
	private static final long serialVersionUID = 1L;
	/** 全路径分隔符 */
	public static final String PS = "/";
	/** 类型:未定义 */
	public static final int TYPE_UNDEFINED = 0;
	/** 类型:用户 */
	public static final int TYPE_USER = 4;
	/** 类型:单位 */
	public static final int TYPE_UNIT = 1;
	/** 类型:部门 */
	public static final int TYPE_DEPARTMENT = 2;
	/** 类型:岗位或团队 */
	public static final int TYPE_GROUP = 3;

	public Actor() {

	}

	private Long id;
	private String uid;
	private int status = cn.bc.core.RichEntity.STATUS_ENABLED;
	private boolean inner = false;

	private String name;
	private String code;
	private String pname;// 隶属机构的全名:如'unitName1/departmentName1,unitName2/departmentName2'
	private String pcode;// 隶属机构的全编码:如'[1]unitCode1/[2]departmentCode1,[1]unitCode2/[2]departmentCode2'
	private int type = Actor.TYPE_UNDEFINED;
	private String email;
	private String phone;
	private String orderNo;

	private ActorDetail detail;

	private Set<Role> roles;// 拥有的角色列表

	// 姓名的中文拼音
	@Column(name = "PY")
	public String getPinYin() {
		return PinYinUtils.getPinYin(this.getName());
	}

	public void setPinYin(String pinYin) {
		// do nothing
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "BC_IDENTITY_ROLE_ACTOR", joinColumns = @JoinColumn(name = "AID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "RID", referencedColumnName = "ID"))
	@OrderBy("orderNo asc")
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

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getPcode() {
		return pcode;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
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
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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

	@Transient
	public String getFullCode() {
		if (this.getPcode() != null && this.getPcode().length() > 0) {
			if (this.getPcode().indexOf(",") != -1) {// 隶属多个上级的情况
				String[] pcodes = this.getPcode().split(",");
				for (int i = 0; i < pcodes.length; i++) {
					pcodes[i] = pcodes[i] + PS + "[" + this.getType() + "]"
							+ this.getCode();
				}
				return StringUtils.arrayToCommaDelimitedString(pcodes);
			} else {
				return this.getPcode() + PS + "[" + this.getType() + "]"
						+ this.getCode();
			}
		} else {
			return "[" + this.getType() + "]" + this.getCode();
		}
	}

	@Transient
	public String getFullName() {
		if (this.getPname() != null && this.getPname().length() > 0) {
			if (this.getPname().indexOf(",") != -1) {// 隶属多个上级的情况
				String[] pnames = this.getPname().split(",");
				for (int i = 0; i < pnames.length; i++) {
					pnames[i] = pnames[i] + PS + this.getName();
				}
				return StringUtils.arrayToCommaDelimitedString(pnames);
			} else {
				return this.getPname() + PS + this.getName();
			}
		} else {
			return this.getName();
		}
	}
}
