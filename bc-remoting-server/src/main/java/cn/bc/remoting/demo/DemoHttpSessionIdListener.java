package cn.bc.remoting.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionIdListener;

/**
 * Created by dragon on 2015/1/22.
 */
@WebListener("DemoHttpSessionIdListener")
public class DemoHttpSessionIdListener implements HttpSessionIdListener {
	private static Logger logger = LoggerFactory.getLogger(DemoHttpSessionIdListener.class);

	@Override
	public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
		logger.debug("sessionIdChanged: oldSessionId={}", oldSessionId);
	}
}