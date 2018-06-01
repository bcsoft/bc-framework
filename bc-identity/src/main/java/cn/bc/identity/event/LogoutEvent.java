package cn.bc.identity.event;

import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户退出系统、登录超时的事件
 *
 * @author dragon
 */
public class LogoutEvent extends LogBaseEvent {
  private static final long serialVersionUID = -2066005399640273807L;
  private static Logger logger = LoggerFactory.getLogger(LogoutEvent.class);

  /**
   * @param source      事件源
   * @param request     请求
   * @param user        用户
   * @param userHistory
   * @param sid
   */
  public LogoutEvent(Object source, HttpServletRequest request, Actor user,
                     ActorHistory userHistory, String sid) {
    super(source, request, user, userHistory, sid);

    if (logger.isDebugEnabled()) {
      String info = user.getName() + "退出系统";
      info += ",client=" + this.getClientIp();
      info += ",server=" + this.getServerIp();
      info += ",sid=" + this.getSid();
      logger.debug(info);
    }
  }
}
