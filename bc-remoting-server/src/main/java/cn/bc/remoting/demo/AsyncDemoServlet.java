package cn.bc.remoting.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
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
@WebServlet(value = {"/asyncdemo"}
  // 不指定默认为类的全名
  //, name = "cn.bc.remoting.demo.AsyncDemoServlet"
  // 异步支持
  , asyncSupported = true
  // 初始化参数
  , initParams = {@WebInitParam(name = "author", value = "dragon")}
)
public class AsyncDemoServlet extends HttpServlet {
  private static Logger logger = LoggerFactory.getLogger(AsyncDemoServlet.class);

  @Override
  public void init(ServletConfig config) throws ServletException {
    logger.debug("author={}", config.getInitParameter("author"));
    super.init(config);
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    logger.debug("doGet start {}", new Date());
    resp.setContentType("text/html;charset=UTF-8");
    PrintWriter out = resp.getWriter();
    out.println("进入Servlet的时间：" + new Date() + ".");
    out.flush();

    // 在子线程中执行业务调用，并由其负责输出响应，主线程退出
    AsyncContext ctx = req.startAsync();
    ctx.addListener(new DemoAsyncListener());// 监听异步处理的进展
    new Thread(new DemoRunable(ctx)).start();

    out.println("结束Servlet的时间：" + new Date() + ".");
    out.flush();
    logger.debug("doGet end {}", new Date());

    req.getSession().invalidate();
    logger.debug("session invalidate");
  }

  public class DemoRunable implements Runnable {
    private AsyncContext ctx = null;

    public DemoRunable(AsyncContext ctx) {
      this.ctx = ctx;
    }

    public void run() {
      logger.debug("DemoRunable start {}", new Date());
      try {
        //等待十秒钟，以模拟业务方法的执行
        Thread.sleep(1000);
        PrintWriter out = ctx.getResponse().getWriter();
        out.println("业务处理完毕的时间：" + new Date() + ".");
        out.flush();
        ctx.complete();
      } catch (Exception e) {
        e.printStackTrace();
      }

      logger.debug("DemoRunable end {}", new Date());
    }
  }

  public class DemoAsyncListener implements AsyncListener {
    @Override
    public void onComplete(AsyncEvent event) throws IOException {
      logger.debug("DemoAsyncListener onComplete {}", new Date());
    }

    @Override
    public void onTimeout(AsyncEvent event) throws IOException {

    }

    @Override
    public void onError(AsyncEvent event) throws IOException {

    }

    @Override
    public void onStartAsync(AsyncEvent event) throws IOException {
      // ? 测试结果：没有执行
      logger.debug("DemoAsyncListener onStartAsync {}", new Date());
    }
  }
}