/**
 * 
 */
package cn.bc.websocket.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import cn.bc.identity.web.SystemContext;
import cn.bc.web.event.SessionDestroyedEvent;
import cn.bc.websocket.jetty.ChatWebSocketService;

/**
 * Session销毁的事件监听处理：webSocket下线处理
 * 
 * @author dragon
 * 
 */
public class SessionDestroyedNotifier4WebSocket implements
		ApplicationListener<SessionDestroyedEvent> {
	private static Log logger = LogFactory
			.getLog(SessionDestroyedNotifier4WebSocket.class);
	private ChatWebSocketService webSocketService;

	@Autowired
	public void setWebSocketService(ChatWebSocketService webSocketService) {
		this.webSocketService = webSocketService;
	}

	public void onApplicationEvent(SessionDestroyedEvent event) {
		String sid = (String) event.getSession().getAttribute("sid");
		if (logger.isDebugEnabled()){
			logger.debug("session id=" + event.getSession().getId());
			logger.debug("sid=" + sid);
		}

		SystemContext context = (SystemContext) event.getContext();

		// 非登录状态的超时不做处理
		if (context == null)
			return;

		// 通知其他连接超时下线
		this.webSocketService.offline(sid);
	}
}
