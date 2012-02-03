/**
 * 
 */
package cn.bc.chat.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import cn.bc.chat.service.OnlineUserService;
import cn.bc.web.event.SessionDestroyedEvent;

/**
 * Session销毁的事件监听处理：移除在线用户
 * 
 * @author dragon
 * 
 */
public class SessionDestroyedNotifier4OnlineUser implements
		ApplicationListener<SessionDestroyedEvent> {
	private static Log logger = LogFactory
			.getLog(SessionDestroyedNotifier4OnlineUser.class);
	private OnlineUserService onlineUserService;

	@Autowired
	public void setOnlineUserService(OnlineUserService onlineUserService) {
		this.onlineUserService = onlineUserService;
	}

	public void onApplicationEvent(SessionDestroyedEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug("sid=" + event.getSession().getId());
		}
		// 移除下线用户
		this.onlineUserService.remove(event.getSession().getId());
	}
}
