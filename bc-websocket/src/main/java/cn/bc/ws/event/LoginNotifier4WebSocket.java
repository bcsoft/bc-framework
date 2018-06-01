/**
 *
 */
package cn.bc.ws.event;

import cn.bc.identity.event.LoginEvent;
import cn.bc.ws.jetty.ChatWebSocketService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

/**
 * 用户登录事件的监听器：记录登录日志及在线用户
 *
 * @author dragon
 */
public class LoginNotifier4WebSocket implements ApplicationListener<LoginEvent> {
  private static Log logger = LogFactory
    .getLog(LoginNotifier4WebSocket.class);
  private ChatWebSocketService webSocketService;

  @Autowired
  public void setWebSocketService(ChatWebSocketService webSocketService) {
    this.webSocketService = webSocketService;
  }

  public void onApplicationEvent(LoginEvent event) {
    String sid = (String) event.getSid();
    if (logger.isDebugEnabled()) {
      logger.debug("sid=" + sid + ",session id="
        + event.getRequest().getSession().getId());
    }

    // 通知websocket用户重新登录了
    this.webSocketService.memberLogin(sid,
      "true".equals(event.getRequest().getParameter("relogin")));
  }
}
