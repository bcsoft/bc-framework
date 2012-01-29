package cn.bc.identity.event;

import org.springframework.context.ApplicationEvent;

import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;

/**
 * 用户登录系统的事件
 * 
 * @author dragon
 * 
 */
public class LoginEvent extends ApplicationEvent {
	private static final long serialVersionUID = 6073857021656865036L;
	private final Actor user;
	private final ActorHistory userHistory;
	private final Integer loginType;

	/**
	 * @param source
	 *            事件源
	 * @param user
	 *            用户
	 * @param type
	 *            类型，参考Syslog.TYPE_XXX常数的定义
	 */
	public LoginEvent(Object source, Actor user, ActorHistory userHistory,
			Integer type) {
		super(source);
		this.user = user;
		this.userHistory = userHistory;
		this.loginType = type;
	}

	/**
	 * 获取登录用户
	 * 
	 * @return
	 */
	public Actor getUser() {
		return user;
	}

	public ActorHistory getUserHistory() {
		return userHistory;
	}

	/**
	 * 获取登录类型
	 * 
	 * @return
	 */
	public Integer getLoginType() {
		return loginType;
	}
}
