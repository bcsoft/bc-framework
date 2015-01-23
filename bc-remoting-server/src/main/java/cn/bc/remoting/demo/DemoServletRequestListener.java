package cn.bc.remoting.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by dragon on 2015/1/22.
 */
@WebListener
public class DemoServletRequestListener implements ServletRequestListener {
	private static Logger logger = LoggerFactory.getLogger(DemoServletRequestListener.class);

	@Override
	public void requestDestroyed(ServletRequestEvent sre) {
		logger.debug("requestDestroyed: {}", ((HttpServletRequest) sre.getServletRequest()).getRequestURI());
	}

	@Override
	public void requestInitialized(ServletRequestEvent sre) {
		logger.debug("requestInitialized: {}", ((HttpServletRequest) sre.getServletRequest()).getRequestURI());
	}
}
