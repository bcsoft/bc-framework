package cn.bc.websocket.jetty;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bc.identity.web.SystemContext;

public class ChatWebSocket implements WebSocket.OnTextMessage {
	private final Log logger = LogFactory.getLog(ChatWebSocket.class);
	private Long userId;
	private SystemContext context;
	private Connection connection;
	private final Set<ChatWebSocket> members;

	public ChatWebSocket(SystemContext context, Set<ChatWebSocket> members) {
		this.context = context;
		this.userId = context.getUser().getId();
		this.members = members;
	}

	public void onOpen(Connection connection) {
		this.connection = connection;
		members.add(this);

		// 向所有用户发送上线信息
		String msg = this.context.getUser().getName() + "上线了！";
		logger.info(msg);
		sendMessageToAllUser(msg);
	}

	public void onClose(int code, String message) {
		members.remove(this);

		// 向所有用户发送下线信息
		String msg = this.context.getUser().getName() + "下线了！";
		logger.info(msg);
		sendMessageToAllUser(msg);
	}

	public void onMessage(String data) {
		// data的格式：{type:"send|close,to:[actorId],msg:[message]}
		try {
			JSONObject json = new JSONObject(data);
			String type = json.getString("type");

			if ("close".equalsIgnoreCase(type)) {// 关闭连接
				connection.disconnect();
			} else {// 发送信息
				long to = json.getLong("to");
				if (to > 0) {
					if (!isActiveUser(to)) {
						// 提示用户不在线
						sendMessage(this.userId, "用户不在线，无法收到你发送的消息！");
					}
				}
				this.sendMessage(to, json.getString("msg"));
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 检测用户是否在线
	 * 
	 * @param to
	 *            用户的id
	 * @return
	 */
	private boolean isActiveUser(long to) {
		for (ChatWebSocket member : members) {
			if (member.userId == to)
				return true;
		}
		return false;
	}

	/**
	 * @param to
	 *            接收人的id，0代表所有用户
	 * @param msg
	 */
	private void sendMessage(long to, String msg) {
		if (to > 0) {// 发给指定用户
			logger.debug("to=" + to + ",msg=" + msg);
			for (ChatWebSocket member : members) {
				try {
					if (member.userId == to)
						member.connection.sendMessage(msg);
				} catch (IOException e) {
					logger.warn(e);
				}
			}
		} else {// 发给所有在线用户
			sendMessageToAllUser(msg);
		}
	}

	/**
	 * 向所有在线用户发送信息
	 * 
	 * @param msg
	 */
	private void sendMessageToAllUser(String msg) {
		logger.debug("sendMessageToAllUser:msg=" + msg);
		for (ChatWebSocket member : members) {
			try {
				member.connection.sendMessage(msg);
			} catch (IOException e) {
				logger.warn(e);
			}
		}
	}
}