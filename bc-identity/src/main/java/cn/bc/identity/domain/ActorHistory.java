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
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_IDENTITY_ACTOR_HISTORY")
public class ActorHistory extends cn.bc.core.EntityImpl {
	private static final long serialVersionUID = 1L;
	private int actorType;
	private Long actorId;
	private String name;
	private Long upperId;// 所属直接上级
	private String upperName;
	private Long unitId;// 所属单位
	private String unitName;
	private String pcode;//上级的全编码
	private String pname;//上级的全名
	private Calendar startDate;//开始时间
	private Calendar endDate;//结束时间
	private Calendar createDate;//创建时间

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
