/**
 * 
 */
package cn.bc.identity.domain;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Actor隶属信息的变动历史：记录用户的直接上级、所属单位的变动信息
 * <ul>
 * <li>
 * 对每一个隶属关系，应该只存在一条current值为true的ActorHistory记录。</li>
 * <li>
 * 每当Actor所隶属的单位、部门信息变动时，就应该生成新的历史记录(current属性设为true)，
 * 并且将对应的旧的记录的current属性设为false。</li>
 * <li>
 * 如果Actor同时隶属多个上级组织（矩阵式组织架构），则对应每个隶属关系都应该有独立的ActorHistory
 * 信息对应该隶属关系的变动历史；如果是删除多个隶属关系中的一个，则将该隶属关系对应的当前
 * ActorHistory记录的current设为false，endDate设为当前时间。</li>
 * <li>
 * upperId、upperName是Actor直接隶属的组织信息，可能是部门也可能是单位。unitId、unitName是Actor所隶属的单位信息，
 * 如果upper是部门
 * ，则其值是该部门所隶属的单位；如果upper直接就是单位，则unitId、unitName与upperId、upperName的值相同。
 * pcode、pname是upper的全编码和全名称，格式与actor的pcode、pname相同</li>
 * <li>
 * startDate、endDate记录隶属历史的时间范围。如果是首次新建的记录（没有相应的旧记录的情况）startDate为null。如果是当前记录(
 * current=true)， endDate的值为null。
 * 创建新记录时如果存在旧记录，须将对应的旧记录的endDate值设为新记录的创建时间，该新记录的startDate设为当前时间。</li>
 * </ul>
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_IDENTITY_ACTOR_HISTORY")
public class ActorHistory extends cn.bc.core.EntityImpl {
	private static final long serialVersionUID = 1L;
	private boolean current;// 是否为当前配置
	private int rank;// 多个当前配置间的首选次序，数值越小级别越高，值从0开始递增，只适用于隶属多个组织的情况
	private int actorType;// 对应Actor的type
	private Long actorId;// 对应Actor的id
	private Long pid;// 对应旧记录的id
	private String name;// 对应Actor的name
	private Long upperId;// 所属直接上级
	private String upperName;
	private Long unitId;// 所属单位
	private String unitName;
	private String pcode;// 隶属机构的全编码:如'[1]unitCode1/[2]departmentCode1,[1]unitCode2/[2]departmentCode2'
	private String pname;// 隶属机构的全名:如'unitName1/departmentName1,unitName2/departmentName2'
	private Calendar startDate;// 开始时间
	private Calendar endDate;// 结束时间
	private Calendar createDate;// 创建时间

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	public String getPcode() {
		return pcode;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	@Column(name = "ACTOR_TYPE")
	public int getActorType() {
		return actorType;
	}

	public void setActorType(int actorType) {
		this.actorType = actorType;
	}

	@Column(name = "ACTOR_ID")
	public Long getActorId() {
		return actorId;
	}

	public void setActorId(Long actorId) {
		this.actorId = actorId;
	}

	@Column(name = "ACTOR_NAME")
	public String getName() {
		return name;
	}

	public void setName(String actorName) {
		this.name = actorName;
	}

	@Column(name = "UPPER_ID")
	public Long getUpperId() {
		return upperId;
	}

	public void setUpperId(Long upperId) {
		this.upperId = upperId;
	}

	@Column(name = "UPPER_NAME")
	public String getUpperName() {
		return upperName;
	}

	public void setUpperName(String upperName) {
		this.upperName = upperName;
	}

	@Column(name = "UNIT_ID")
	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	@Column(name = "UNIT_NAME")
	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	@Column(name = "START_DATE")
	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	@Column(name = "END_DATE")
	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	@Column(name = "CREATE_DATE")
	public Calendar getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Calendar createDate) {
		this.createDate = createDate;
	}
}
