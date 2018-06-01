package cn.bc.remoting.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by dragon on 2015/1/22.
 */
@WebFilter(filterName = "cn.bc.remoting.demo.DemoFilter"
  // url匹配模式
  , value = "*"
  // 匹配指定的 Servlet
  //servletNames = {"cn.bc.remoting.demo.AsyncDemoServlet"}
  // 异步支持(需与 Servlet 的异步配置保持一致)
  , asyncSupported = true
  // 初始化参数
  , initParams = {@WebInitParam(name = "name", value = "hrj")}
)
public class DemoFilter implements Filter {
  private static Logger logger = LoggerFactory.getLogger(DemoFilter.class);

  @Override
  public void init(FilterConfig config) throws ServletException {
    logger.debug("init(...)");
    logger.debug("name={}", config.getInitParameter("name"));
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    logger.debug("doFilter(...): {}", ((HttpServletRequest) request).getRequestURI());
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    logger.debug("destroy()");
  }
}