package cn.bc.ws.jetty;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import cn.bc.web.util.WebUtils;

public class ChatWebSocketServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
	protected static final Log logger = LogFactory
			.getLog(ChatWebSocketServlet.class);
	private ChatWebSocketService webSocketService = WebUtils
			.getBean(ChatWebSocketService.class);

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response)
			throws javax.servlet.ServletException, IOException {
		if (logger.isDebugEnabled())
			logger.debug("--doGet--");
		getServletContext().getNamedDispatcher("default").forward(request,
				response);
	};

	public WebSocket doWebSocketConnect(HttpServletRequest request,
			String protocol) {
		if (logger.isDebugEnabled())
			logger.debug("protocol=" + protocol + ",sid="
					+ request.getParameter("sid") + ",session id="
					+ request.getSession().getId());

		return this.webSocketService.createWebSocket(request);
	}
}