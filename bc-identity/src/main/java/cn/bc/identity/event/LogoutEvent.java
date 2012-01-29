package cn.bc.identity.event;

import org.springframework.context.ApplicationEvent;

import cn.bc.identity.domain.ActorHistory;

/**
 * 用户退出系统、登录超时的事件
 * 
 * @author dragon
 * 
 */
public class LogoutEvent extends ApplicationEvent {
	private static final long serialVersionUID = -2066005399640273807L;
	private final ActorHistory userHistory;
	private final Integer loginType;

	/**
	 * @param source
	 *            事件源
	 * @param userHistory
	 *            用户
	 * @param type
	 *            类型，参考Syslog.TYPE_XXX常数的定义
	 */
	public LogoutEvent(Object source, ActorHistory userHistory, Integer type) {
		super(source);
		this.userHistory = userHistory;
		this.loginType = type;
	}

	/**
	 * 获取登录用户
	 * 
	 * @return
	 */
	public ActorHistory getUserHistory() {
		return userHistory;
	}

	/**
	 * 获取退出类型
	 * 
	 * @return
	 */
	public Integer getLoginType() {
		return loginType;
	}
}
