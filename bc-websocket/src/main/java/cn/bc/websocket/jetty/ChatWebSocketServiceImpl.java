package cn.bc.websocket.jetty;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import cn.bc.Context;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.json.Json;
import cn.bc.web.util.WebUtils;

/**
 * WebSocket服务接口的实现
 * 
 * @author dragon
 * 
 */
public class ChatWebSocketServiceImpl implements ChatWebSocketService,
		ApplicationContextAware {
	private final Log logger = LogFactory
			.getLog(ChatWebSocketServiceImpl.class);
	private ApplicationContext applicationContext;
	private final Set<ChatWebSocket> members = new CopyOnWriteArraySet<ChatWebSocket>();// 在线连接
	private final Set<ChatWebSocket> pools = new CopyOnWriteArraySet<ChatWebSocket>();// 离线连接

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * 向连接池增加一个连接缓存
	 * 
	 * @param webSocket
	 */
	private void addPoolsMember(ChatWebSocket webSocket) {
		this.pools.add(webSocket);
	}

	/**
	 * 从连接池取一个新的连接，如果连接池为空，则自动初始化一个新的连接
	 * 
	 * @param webSocket
	 */
	private ChatWebSocket getPoolsMember() {
		ChatWebSocket webSocket;
		if (this.pools.isEmpty()) {
			webSocket = this.applicationContext.getBean(ChatWebSocket.class);

			if (logger.isDebugEnabled())
				logger.debug("create webSocket");
		} else {
			// 取一个连接
			webSocket = this.pools.iterator().next();

			// 从连接池中剔除取出的连接
			this.pools.remove(webSocket);

			// 清空参数
			webSocket.setSid(null);
			webSocket.setClientIp(null);
			webSocket.setClientName(null);
			webSocket.setClientId(null);

			if (logger.isDebugEnabled())
				logger.debug("get webSocket from pools");
		}
		return webSocket;
	}

	public ChatWebSocket createWebSocket(HttpServletRequest request) {
		// 系统上下文
		SystemContext context = (SystemContext) request.getSession()
				.getAttribute(Context.KEY);

		// session id
		String sid = request.getParameter("sid");

		ChatWebSocket webSocket = this.getPoolsMember();
		webSocket.setSid(sid);
		webSocket.setClientIp(WebUtils.getClientIP(request));

		if (context == null) {// jetty8.0.4实际测试证明：context == null
			// throw new CoreException("用户未登录！");
			logger.fatal("session without context!");
			webSocket.setClientName(request.getParameter("userName"));
			webSocket.setClientId(request.getParameter("userUid"));
		} else {
			logger.debug("session with context!");
			webSocket.setClientName(context.getUser().getName());
			webSocket.setClientId(context.getUser().getUid());
		}
		return webSocket;
	}

	public void addMember(ChatWebSocket webSocket) {
		this.members.add(webSocket);
	}

	public void removeMember(ChatWebSocket webSocket) {
		this.members.remove(webSocket);

		// 将离线连接缓存给后续的新连接使用
		this.addPoolsMember(webSocket);
	}

	public boolean isExisting(String sid) {
		for (ChatWebSocket member : members) {
			if (member.getSid().equals(sid))
				return true;
		}
		return false;
	}

	public ChatWebSocket get(String sid) {
		for (ChatWebSocket member : members) {
			if (member.getSid().equals(sid))
				return member;
		}
		return null;
	}

	public void sendMessage(String fromSid, String toSid, int type, String msg) {
		if (toSid != null && toSid.length() > 0) {// 发给指定连接
			ChatWebSocket fromWebSocket = this.get(fromSid);
			Assert.notNull(fromWebSocket);
			ChatWebSocket toWebSocket = this.get(toSid);
			Assert.notNull(toWebSocket);
			String json = buildJson(fromWebSocket, type, msg, null);
			if (logger.isDebugEnabled())
				logger.debug("fromSid=" + fromSid + ",toSid=" + toSid + ",msg="
						+ json);

			try {
				toWebSocket.getConnection().sendMessage(json);
			} catch (IOException e) {
				logger.error(e);
			}
		} else {// 发给所有连接
			sendMessageToAllClient(fromSid, type, msg);
		}
	}

	public void sendMessageToOtherClient(String fromSid, int type, String msg) {
		ChatWebSocket fromWebSocket = this.get(fromSid);
		Assert.notNull(fromWebSocket);
		String json = buildJson(fromWebSocket, type, msg, null);
		if (logger.isDebugEnabled())
			logger.debug("sendMessageToOtherUser:fromSid=" + fromSid + ",msg="
					+ json);
		for (ChatWebSocket member : members) {
			if (!member.getSid().equals(fromSid)) {
				try {
					member.getConnection().sendMessage(json);
				} catch (IOException e) {
					logger.error("toSid=" + member.getSid());
					logger.error(e.getMessage(),e);
				}
			}
		}
	}

	public void sendMessageToAllClient(String fromSid, int type, String msg) {
		ChatWebSocket fromWebSocket = this.get(fromSid);
		Assert.notNull(fromWebSocket);
		String json = buildJson(fromWebSocket, type, msg, null);
		if (logger.isDebugEnabled())
			logger.debug("sendMessageToAllClient:fromSid=" + fromSid + ",msg="
					+ json);
		for (ChatWebSocket member : this.members) {
			try {
				member.getConnection().sendMessage(json);
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}

	private SimpleDateFormat formatter = new SimpleDateFormat("H:mm:ss");

	private String buildJson(ChatWebSocket member, int type, String msg,
			String originSid) {
		Json json = new Json();
		json.put("time", formatter.format(new Date()));// 发送的服务器时间
		json.put("type", type);
		json.put("sid", member.getSid());
		json.put("name", member.getClientName());
		json.put("uid", member.getClientId());
		json.put("ip", member.getClientIp());
		json.put("msg", msg);

		if (originSid != null)
			json.put("origin", originSid);
		return json.toString();
	}

	public void sendMessageBack(String fromSid, String toSid, String msg) {
		ChatWebSocket fromWebSocket = this.get(fromSid);
		Assert.notNull(fromWebSocket);
		String json = buildJson(fromWebSocket, ChatWebSocket.TYPE_SYSTIP, msg,
				toSid);
		if (logger.isDebugEnabled())
			logger.debug("sendMessageBack:fromSid=" + fromSid + ",toSid="
					+ toSid + ",msg=" + json);
		try {
			fromWebSocket.getConnection().sendMessage(json);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}

	public void offline(String sid) {
		if (logger.isDebugEnabled())
			logger.debug("offline:sid=" + sid);

		ChatWebSocket webSocket = this.get(sid);
		Assert.notNull(webSocket);
		String msg = webSocket.getClientName() + "下线了(登录超时)！";
		String json = buildJson(webSocket, ChatWebSocket.TYPE_OFFLINE, msg,
				null);

		// 向登录超时的连接发送消息，避免重复连接
		try {
			webSocket.getConnection().sendMessage(json);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}

		// 向其它连接发送消息
		this.sendMessageToOtherClient(sid, ChatWebSocket.TYPE_OFFLINE, msg);

		// 断开连接
		this.removeMember(webSocket);
	}
}