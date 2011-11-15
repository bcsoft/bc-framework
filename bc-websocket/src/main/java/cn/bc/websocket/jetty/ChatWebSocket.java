package cn.bc.websocket.jetty;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bc.web.ui.json.Json;

public class ChatWebSocket implements WebSocket.OnTextMessage {
	private final Log logger = LogFactory.getLog(ChatWebSocket.class);
	public final int TYPE_UPDOWN = 0;// 用户上线下线信息
	public final int TYPE_BROADCAST = 1;// 全局广播的信息
	public final int TYPE_USER = 2;// 用户发到用户的信息
	private Long userId;
	private String userName;
	private String sid;
	private Connection connection;
	private final Set<ChatWebSocket> members;

	public ChatWebSocket(Long userId, String userName, String sid,
			Set<ChatWebSocket> members) {
		this.userId = userId;
		this.userName = userName;
		this.sid = sid;
		this.members = members;
	}

	public void onOpen(Connection connection) {
		this.connection = connection;
		members.add(this);

		// 向所有用户发送上线信息
		String msg = this.userName + "上线了！";
		logger.info(msg);
		sendMessageToAllUser(TYPE_UPDOWN, msg);
	}

	public void onClose(int code, String message) {
		members.remove(this);

		// 向所有用户发送下线信息
		String msg = this.userName + "下线了！";
		logger.info(msg);
		sendMessageToAllUser(TYPE_UPDOWN, msg);
	}

	public void onMessage(String data) {
		// data的格式：{type:"send|close,to:[actorId],msg:[message]}
		try {
			JSONObject json = new JSONObject(data);
			int type = json.getInt("type");

			if (type == -1) {// 关闭连接
				connection.disconnect();
			} else {// 发送信息
				String to_sid = json.getString("to_sid");
				if (to_sid != null && to_sid.length() > 0) {
					if (!isActiveUser(to_sid)) {
						// 提示用户不在线
						sendMessage(TYPE_USER, this.sid, "用户不在线，无法收到你发送的消息！");
						return;
					}
				}
				this.sendMessage(type, to_sid, json.getString("msg"));
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 检测用户是否在线
	 * 
	 * @param to_sid
	 *            用户的会话id
	 * @return
	 */
	private boolean isActiveUser(String to_sid) {
		for (ChatWebSocket member : members) {
			if (member.sid.equals(to_sid))
				return true;
		}
		return false;
	}

	/**
	 * @param type
	 *            信息类型，见TYPE_XXX常数的定义
	 * @param to_sid
	 *            接收人的所在会话的id
	 * @param msg
	 *            要发送的信息内容
	 */
	private void sendMessage(int type, String to_sid, String msg) {
		if (to_sid != null && to_sid.length() > 0) {// 发给指定用户
			if (logger.isDebugEnabled())
				logger.debug("to_sid=" + to_sid + ",msg="
						+ buildJson(type, msg));
			for (ChatWebSocket member : members) {
				try {
					if (member.sid.equals(to_sid))
						member.connection.sendMessage(buildJson(type, msg));
				} catch (IOException e) {
					logger.warn(e);
				}
			}
		} else {// 发给所有在线用户
			sendMessageToAllUser(type, msg);
		}
	}

	/**
	 * 向所有在线用户发送信息
	 * 
	 * @param msg
	 */
	private void sendMessageToAllUser(int type, String msg) {
		if (logger.isDebugEnabled())
			logger.debug("sendMessageToAllUser:msg="
					+ buildJson(TYPE_UPDOWN, msg));
		for (ChatWebSocket member : members) {
			try {
				member.connection.sendMessage(buildJson(type, msg));
			} catch (IOException e) {
				logger.warn(e);
			}
		}
	}

	private String buildJson(int type, String msg) {
		Json json = new Json();
		json.put("type", type);
		json.put("from_sid", this.sid);
		json.put("from_userName", this.userName);
		json.put("from_userId", this.userId);
		json.put("msg", msg);
		return json.toString();
	}
}