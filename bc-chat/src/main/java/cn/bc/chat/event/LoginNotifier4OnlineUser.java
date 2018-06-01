/**
 *
 */
package cn.bc.chat.event;

import cn.bc.chat.OnlineUser;
import cn.bc.chat.service.OnlineUserService;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.event.LoginEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import java.util.Calendar;

/**
 * 用户登录事件的监听器：记录在线用户
 *
 * @author dragon
 */
public class LoginNotifier4OnlineUser implements
  ApplicationListener<LoginEvent> {
  private static Log logger = LogFactory
    .getLog(LoginNotifier4OnlineUser.class);
  private OnlineUserService onlineUserService;

  @Autowired
  public void setOnlineUserService(OnlineUserService onlineUserService) {
    this.onlineUserService = onlineUserService;
  }

  public void onApplicationEvent(LoginEvent event) {
    if (logger.isDebugEnabled()) {
      logger.debug("sid=" + event.getSid());
    }
    Actor user = event.getUser();
    // 记录在线用户
    OnlineUser onlineUser = new OnlineUser();
    onlineUser.setLoginTime(Calendar.getInstance());
    onlineUser.setId(user.getId());
    onlineUser.setUid(user.getUid());
    onlineUser.setName(user.getName());
    onlineUser.setCode(user.getCode());
    onlineUser.setPname(user.getPname());
    onlineUser.setFullName(user.getFullName());
    onlineUser.setIp(event.getClientIp());
    onlineUser.setSid(event.getSid());
    onlineUser.setBrowser(event.getBrowser());
    onlineUser.setMac(event.getClientMac());

    this.onlineUserService.add(onlineUser);
  }
}
