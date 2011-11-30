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
import cn.bc.identity.web.SystemContext;
import cn.bc.web.util.WebUtils;

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
			logger.debug("--doWebSocketConnect--protocol=" + protocol + ",sid="
					+ request.getSession().getId());

		// 当前用户信息
		SystemContext context = (SystemContext) request.getSession()
				.getAttribute(Context.KEY);
		String sid = request.getParameter("sid");
		if (context == null) {// jetty8.0.4实际测试证明：context == null
			// throw new CoreException("用户未登录！");
			logger.fatal("--doWebSocketConnect--session is changed!");
			String userUid = request.getParameter("userUid");
			String userName = request.getParameter("userName");
			return new ChatWebSocket(WebUtils.getClientIP(request), userUid,
					userName, sid, members);
		} else {
			logger.debug("--doWebSocketConnect--session is good!");
			return new ChatWebSocket(WebUtils.getClientIP(request), context
					.getUser().getUid(), context.getUser().getName(), sid,
					members);
		}
	}
}