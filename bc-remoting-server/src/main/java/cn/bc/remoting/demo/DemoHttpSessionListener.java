package cn.bc.remoting.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Created by dragon on 2015/1/22.
 */
@WebListener("DemoHttpSessionListener")
public class DemoHttpSessionListener implements HttpSessionListener {
  private static Logger logger = LoggerFactory.getLogger(DemoHttpSessionListener.class);

  @Override
  public void sessionCreated(HttpSessionEvent se) {
    // 对异步 Servlet 该监听器测试结果为没有调用
    logger.debug("sessionCreated: {}", se.getSession().getId());
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent se) {
    logger.debug("sessionDestroyed: {}", se.getSession().getId());
  }
}
