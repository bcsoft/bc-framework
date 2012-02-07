package cn.bc.identity.event;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;

/**
 * 用户登录系统的事件
 * 
 * @author dragon
 * 
 */
public class LoginEvent extends LogBaseEvent {
	private static final long serialVersionUID = 6073857021656865036L;
	private static Log logger = LogFactory.getLog(LoginEvent.class);

	/**
	 * @param source
	 *            事件源
	 * @param request
	 *            请求
	 * @param user
	 *            用户
	 * @param userHistory
	 * @param sid
	 */
	public LoginEvent(Object source, HttpServletRequest request, Actor user,
			ActorHistory userHistory, String sid) {
		super(source, request, user, userHistory, sid);

		if (logger.isDebugEnabled()) {
			String info = user.getName() + "登录系统";
			info += ",client=" + this.getClientIp();
			info += ",server=" + this.getServerIp();
			info += ",sid=" + this.getSid();
			logger.debug(info);
		}
	}
}
