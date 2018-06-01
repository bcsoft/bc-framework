package cn.bc.remoting.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by dragon on 2015/1/22.
 *
 * @ref http://www.ibm.com/developerworks/cn/java/j-lo-servlet30/
 */
@WebServlet(value = {"/demo"}
  // 不指定默认为类的全名
  //, name = "cn.bc.remoting.demo.AsyncDemoServlet"
  // 异步支持
  , asyncSupported = false
  // 初始化参数
  , initParams = {@WebInitParam(name = "author", value = "dragon")}
)
public class DemoServlet extends HttpServlet {
  private static Logger logger = LoggerFactory.getLogger(DemoServlet.class);

  @Override
  public void init(ServletConfig config) throws ServletException {
    logger.debug("author={}", config.getInitParameter("author"));
    super.init(config);
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    logger.debug("doGet start {}", new Date());
    logger.debug("session id={}", req.getSession().getId());
    resp.setContentType("text/html;charset=UTF-8");
    PrintWriter out = resp.getWriter();
    out.println("进入Servlet的时间：" + new Date() + ".");

    out.println("结束Servlet的时间：" + new Date() + ".");

    req.getSession().invalidate();
    //logger.debug("session invalidate");

    out.flush();
    logger.debug("doGet end {}", new Date());

  }
}