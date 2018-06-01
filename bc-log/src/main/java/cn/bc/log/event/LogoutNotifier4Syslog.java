/**
 *
 */
package cn.bc.log.event;

import cn.bc.identity.domain.Actor;
import cn.bc.identity.event.LogoutEvent;
import cn.bc.log.domain.Syslog;
import cn.bc.log.service.SyslogService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import java.util.Calendar;

/**
 * 用户退出系统的事件监听处理：记录退出日志
 *
 * @author dragon
 */
public class LogoutNotifier4Syslog implements ApplicationListener<LogoutEvent> {
  private static Log logger = LogFactory.getLog(LogoutNotifier4Syslog.class);
  private SyslogService syslogService;

  @Autowired
  public void setSyslogService(SyslogService syslogService) {
    this.syslogService = syslogService;
  }

  public void onApplicationEvent(LogoutEvent event) {
    if (logger.isDebugEnabled()) {
      logger.debug("sid=" + event.getSid());
    }
    Actor user = event.getUser();

    // 记录登录日志
    Syslog log = new Syslog();
    log.setType(Syslog.TYPE_LOGOUT);
    log.setAuthor(event.getUserHistory());
    log.setFileDate(Calendar.getInstance());
    log.setSubject(user.getName() + "退出系统");
    log.setSid(event.getSid());

    log.setServerIp(event.getServerIp());
    log.setServerName(event.getServerName());
    log.setServerInfo(event.getServerInfo());

    log.setClientIp(event.getClientIp());
    log.setClientName(event.getClientName());
    log.setClientInfo(event.getClientInfo());
    log.setClientMac(event.getClientMac());

    syslogService.save(log);
  }
}
