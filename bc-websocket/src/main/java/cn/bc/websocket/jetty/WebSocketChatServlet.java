package cn.bc.websocket.jetty;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import cn.bc.Context;
import cn.bc.core.exception.CoreException;
import cn.bc.identity.web.SystemContext;

public class WebSocketChatServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
	protected static final Log logger = LogFactory
			.getLog(WebSocketChatServlet.class);
	private final Set<ChatWebSocket> members = new CopyOnWriteArraySet<ChatWebSocket>();

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response)
			throws javax.servlet.ServletException, IOException {
		logger.info("--doGet--");
		getServletContext().getNamedDispatcher("default").forward(request,
				response);
	};

	public WebSocket doWebSocketConnect(HttpServletRequest request,
			String protocol) {
		logger.info("--doWebSocketConnect--protocol=" + protocol);
		
		// 当前用户信息
		SystemContext context = (SystemContext)request.getSession().getAttribute(Context.KEY);
		if(context == null){
			throw new CoreException("用户未登录！");
		}
		
		return new ChatWebSocket(context,members);
	}
}