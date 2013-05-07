/**
 * 
 */
package cn.bc.subscribe.domain;

import java.io.Serializable;

import cn.bc.identity.domain.Actor;

/**
 * 复合主键类
 * 
 * @author lbj
 * 
 */

public class SubscribeActorPK implements Serializable {
	private static final long serialVersionUID = 1L;
	private Subscribe subscribe;// 订阅
	private Actor actor;// 订阅者
	

	public SubscribeActorPK(){
		
	}
	
	public SubscribeActorPK(Subscribe subscribe,Actor actor){
		this.subscribe=subscribe;
		this.actor=actor;
	}

	public Subscribe getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(Subscribe subscribe) {
		this.subscribe = subscribe;
	}

	public Actor getActor() {
		return actor;
	}

	public void setActor(Actor actor) {
		this.actor = actor;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (subscribe == null ? 0 : subscribe.hashCode());
		result = prime * result + (actor == null ? 0 : actor.hashCode());
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

		final SubscribeActorPK other = (SubscribeActorPK) obj;
		if (subscribe == null) {
			if (other.subscribe != null)
				return false;
		} else if (!subscribe.equals(other.subscribe)) {
			return false;
		}
		if (actor == null) {
			if (other.actor != null)
				return false;
		} else if (!actor.equals(other.actor)) {
			return false;
		}
		return true;
	}
}
