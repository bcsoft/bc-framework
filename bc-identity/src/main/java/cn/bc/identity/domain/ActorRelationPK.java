/**
 * 
 */
package cn.bc.identity.domain;

import java.io.Serializable;

/**
 * ActorRelation复合主键类
 * 
 * @author dragon
 */
public class ActorRelationPK implements Serializable {
	private static final long serialVersionUID = 1L;
	private Actor master;
	private Actor follower;
	private Integer type;

	public ActorRelationPK() {
	}
	public ActorRelationPK(Actor master, Actor follower, Integer type) {
		this.master = master;
		this.follower = follower;
		this.type = type;
	}

	public Actor getMaster() {
		return master;
	}

	public void setMaster(Actor master) {
		this.master = master;
	}

	public Actor getFollower() {
		return follower;
	}

	public void setFollower(Actor follower) {
		this.follower = follower;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (master == null ? 0 : master.hashCode());
		result = prime * result + (follower == null ? 0 : follower.hashCode());
		result = prime * result + (type == null ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final ActorRelationPK other = (ActorRelationPK) obj;
		if (master == null) {
			if (other.master != null)
				return false;
		} else if (!master.equals(other.master)) {
			return false;
		}
		if (follower == null) {
			if (other.follower != null)
				return false;
		} else if (!follower.equals(other.follower)) {
			return false;
		}
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}
}
