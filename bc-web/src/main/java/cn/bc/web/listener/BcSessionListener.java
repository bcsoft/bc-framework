/**
 *
 */
package cn.bc.web.listener;

import cn.bc.Context;
import cn.bc.web.event.SessionDestroyedEvent;
import cn.bc.web.util.WebUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Enumeration;

/**
 * session监听器,抛出session事件，方便事件处理
 *
 * @author dragon
 */
public class BcSessionListener implements HttpSessionListener {
  protected Log logger = LogFactory.getLog(getClass());

  public void sessionCreated(HttpSessionEvent se) {
    if (logger.isDebugEnabled()) {
      logger.debug("sessionCreated:sid="
        + se.getSession().getAttribute("sid") + ",session id="
        + se.getSession().getId());
    }
  }

  public void sessionDestroyed(HttpSessionEvent se) {
    HttpSession session = se.getSession();
    Context context = (Context) session.getAttribute(Context.KEY);
    String sid = (String) session.getAttribute("sid");
    if (logger.isDebugEnabled()) {
      logger.debug("sessionDestroyed:sid=" + sid + ",session id="
        + session.getId());
      String key;
      for (@SuppressWarnings("unchecked")
           Enumeration<String> e = session.getAttributeNames(); e
             .hasMoreElements(); ) {
        key = e.nextElement();
        logger.debug("	" + key + "=" + session.getAttribute(key));
      }
    }

    // 发布session销毁事件
    WebUtils.getWac().publishEvent(
      new SessionDestroyedEvent(session, context, sid));
  }
}
