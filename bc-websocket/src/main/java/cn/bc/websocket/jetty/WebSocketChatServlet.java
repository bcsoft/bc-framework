package cn.bc.websocket.jetty;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import cn.bc.Context;
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
		if (logger.isDebugEnabled())
			logger.debug("--doGet--");
		getServletContext().getNamedDispatcher("default").forward(request,
				response);
	};

	public WebSocket doWebSocketConnect(HttpServletRequest request,
			String protocol) {
		if (logger.isDebugEnabled())
			logger.debug("--doWebSocketConnect--protocol=" + protocol);

		// 当前用户信息
		SystemContext context = (SystemContext) request.getSession()
				.getAttribute(Context.KEY);
		String sid = request.getParameter("sid");
		if (context == null) {// jetty8.0.4实际测试证明：context == null
			// throw new CoreException("用户未登录！");
			Long userId = new Long(request.getParameter("userId"));
			String name;
			try {
				name = URLDecoder.decode(request.getParameter("userName"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
				name = "UnsupportedEncodingException";
			}
			return new ChatWebSocket(userId, name, sid, members);
		} else {
			logger.fatal("--doWebSocketConnect--session is good!");
			return new ChatWebSocket(context.getUser().getId(), context
					.getUser().getName(), sid, members);
		}
	}
}