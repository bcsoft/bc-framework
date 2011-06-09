/**
 * 
 */
package cn.bc.identity.domain;

/**
 * 参与者的代理者
 * <p>
 * 当一个参与者拥有代理者后，所有与参与者相关的信息将由代理者代为处理
 * </p>
 * 
 * @author dragon
 */
public class ActorProxy extends Actor {
	private static final long serialVersionUID = 1L;
	private Actor actor;
	
	/**
	 * @return 返回所代理的参与者
	 */
	Actor getActor(){
		return actor;
	}

	/**
	 * 设置所要代理的参与者
	 * 
	 * @param actor
	 */
	void setActor(Actor actor){
		this.actor = actor;
	}
}
