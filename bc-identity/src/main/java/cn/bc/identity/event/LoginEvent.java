package cn.bc.identity.event;

import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户登录系统的事件
 *
 * @author dragon
 */
public class LoginEvent extends LogBaseEvent {
  private static final long serialVersionUID = 6073857021656865036L;
  private static Logger logger = LoggerFactory.getLogger(LoginEvent.class);
  private boolean relogin;// 是否是重登陆

  public boolean isRelogin() {
    return relogin;
  }

  public void setRelogin(boolean relogin) {
    this.relogin = relogin;
  }

  /**
   * @param source      事件源
   * @param request     请求
   * @param user        用户
   * @param userHistory
   * @param sid
   */
  public LoginEvent(Object source, HttpServletRequest request, Actor user,
                    ActorHistory userHistory, String sid, boolean relogin) {
    super(source, request, user, userHistory, sid);
    this.relogin = relogin;

    if (logger.isDebugEnabled()) {
      String info = user.getName() + "登录系统";
      info += ",client=" + this.getClientIp();
      info += ",server=" + this.getServerIp();
      info += ",sid=" + this.getSid();
      logger.debug(info);
    }
  }
}
