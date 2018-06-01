/**
 *
 */
package cn.bc.chat.event;

import cn.bc.chat.service.OnlineUserService;
import cn.bc.identity.event.LogoutEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

/**
 * 用户退出系统的事件监听处理：记录退出日志及移除在线用户
 *
 * @author dragon
 */
public class LogoutNotifier4OnlineUser implements ApplicationListener<LogoutEvent> {
  private static Log logger = LogFactory.getLog(LogoutNotifier4OnlineUser.class);
  private OnlineUserService onlineUserService;

  @Autowired
  public void setOnlineUserService(OnlineUserService onlineUserService) {
    this.onlineUserService = onlineUserService;
  }

  public void onApplicationEvent(LogoutEvent event) {
    if (logger.isDebugEnabled()) {
      logger.debug("sid=" + event.getSid());
    }

    // 移除下线用户
    this.onlineUserService.remove(event.getSid());
  }
}
