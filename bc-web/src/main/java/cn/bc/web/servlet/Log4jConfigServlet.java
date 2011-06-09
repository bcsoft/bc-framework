package cn.bc.web.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Log4j初始化加载的Servlet 
 * 
 * @author dragon
 */
public class Log4jConfigServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(Log4jConfigServlet.class);

	public void init(ServletConfig servletConfig) throws ServletException {
		logger.warn("log4j configuration loading...");
		
		// 初始化log4j配置
		String fileName = servletConfig.getInitParameter("log4jConfigLocation");
		fileName = servletConfig.getServletContext().getRealPath(fileName);		
		DOMConfigurator.configureAndWatch(fileName);
		logger.fatal("finished loaded log4j configuration: " + servletConfig.getInitParameter("log4jConfigLocation"));
		
		super.init(servletConfig);
	}
}