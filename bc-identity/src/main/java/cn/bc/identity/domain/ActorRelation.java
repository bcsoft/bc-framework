/**
 * 
 */
package cn.bc.identity.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * Actor之间的关联关系 如：<p>1) 用户所隶属的单位或部门 </p><p>2) 用户所在岗位  </p><p>3) 组织的负责人、正职、副职、归档人等 </p> <p>4) 不同组织间的业务关系 </p>
 * @author  dragon
 */
@Entity
@Table(name = "BC_IDENTITY_ACTOR_RELATION")
@IdClass(ActorRelationPK.class)
public class ActorRelation implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 关联类型：默认的隶属或包含关系
	 * <ul>
	 * <li>部门下的下级部门</li>
	 * <li>部门下的岗位</li>
	 * <li>部门下的人员</li>
	 * </ul>
	 * <ul>
	 * <li>单位下的下级单位</li>
	 * <li>单位下的下级部门</li>
	 * <li>单位下的岗位</li>
	 * <li>单位下的人员</li>
	 * </ul>
	 * <ul>
	 * <li>岗位下的人员</li>
	 * </ul>
	 */
	public static final Integer TYPE_BELONG = 0;
	
	/**
	 * 关联类型：自定义的关系，其值请从10开始使用，0到9为底层保留的关系，方便日后平台底层的扩展
	 * <p>这里仅是举个例子说明自定义的用途，如部门下的主管领导、分管领导、文档管理员等</p>
	 */
	public static final Integer TYPE_CUSTOM = 10;

	private Actor master;
	private Actor follower;
	private Integer type;
	private String order;
	
	@Id
	@ManyToOne(targetEntity = Actor.class)
	@JoinColumn(name = "MASTER_ID", referencedColumnName = "ID")
	public Actor getMaster() {
		return master;
	}
	public void setMaster(Actor master) {
		this.master = master;
	}
	@Id
	@ManyToOne(targetEntity = Actor.class)
	@JoinColumn(name = "FOLLOWER_ID", referencedColumnName = "ID")
	public Actor getFollower() {
		return follower;
	}
	public void setFollower(Actor follower) {
		this.follower = follower;
	}
	@Id
	@Column(name = "TYPE_")
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	@Column(name = "ORDER_")
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
}
