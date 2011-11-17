package cn.bc.websocket.jetty;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bc.web.ui.json.Json;

public class ChatWebSocket implements WebSocket.OnTextMessage {
	private final Log logger = LogFactory.getLog(ChatWebSocket.class);
	public final int TYPE_ONLINE = 0;// 用户上线信息
	public final int TYPE_OFFLINE = 1;// 用户下线信息
	public final int TYPE_USER = 2;// 用户发到用户的信息
	public final int TYPE_BROADCAST = 3;// 全局广播的信息
	public final int TYPE_SYSTIP = 4;// 用户发到用户出现异常时系统返回信息
	private String userUid;
	private String userName;
	private String userIp;
	private String sid;
	private Connection connection;
	private final Set<ChatWebSocket> members;

	// private OnlineUserService onlineService =
	// WebUtils.getBean(OnlineUserService.class);

	public ChatWebSocket(String userIp, String userUid, String userName,
			String sid, Set<ChatWebSocket> members) {
		this.userIp = userIp;
		this.userUid = userUid;
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
		sendMessageToOtherUser(TYPE_ONLINE, msg);
	}

	public void onClose(int code, String message) {
		members.remove(this);

		// 向所有用户发送下线信息
		String msg = this.userName + "下线了！";
		logger.info(msg);
		sendMessageToOtherUser(TYPE_OFFLINE, msg);
	}

	public void onMessage(String data) {
		// data的格式：{type:[TYPE_XXXX],toSid:[toSid],msg:[message]}
		try {
			JSONObject json = new JSONObject(data);
			int type = json.getInt("type");

			if (type == -1) {// 关闭连接
				connection.disconnect();
			} else {// 发送信息
				String toSid = json.getString("toSid");
				if (toSid != null && toSid.length() > 0) {
					if (!isActiveUser(toSid)) {
						// 提示用户不在线
						sendMessageBack(toSid, "用户不在线，无法收到你发送的消息！");
						return;
					}
				}
				this.sendMessage(type, toSid, json.getString("msg"));
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 检测用户是否在线
	 * 
	 * @param toSid
	 *            用户的会话id
	 * @return
	 */
	private boolean isActiveUser(String toSid) {
		for (ChatWebSocket member : members) {
			if (member.sid.equals(toSid))
				return true;
		}
		return false;
	}

	/**
	 * 系统回发信息
	 * 
	 * @param toSid
	 *            原接收人的会话id
	 * @param msg
	 *            要发送的信息内容
	 */
	private void sendMessageBack(String toSid, String msg) {
		for (ChatWebSocket member : members) {
			try {
				if (member.sid.equals(this.sid))
					member.connection.sendMessage(buildJson(TYPE_SYSTIP, msg,
							toSid));
			} catch (IOException e) {
				logger.warn(e);
			}
		}
	}

	/**
	 * @param type
	 *            信息类型，见TYPE_XXX常数的定义
	 * @param toSid
	 *            接收人的所在会话的id
	 * @param msg
	 *            要发送的信息内容
	 */
	private void sendMessage(int type, String toSid, String msg) {
		if (toSid != null && toSid.length() > 0) {// 发给指定用户
			if (logger.isDebugEnabled())
				logger.debug("toSid=" + toSid + ",msg="
						+ buildJson(type, msg, null));
			for (ChatWebSocket member : members) {
				try {
					if (member.sid.equals(toSid))
						member.connection
								.sendMessage(buildJson(type, msg, null));
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
					+ buildJson(type, msg, null));
		for (ChatWebSocket member : members) {
			try {
				member.connection.sendMessage(buildJson(type, msg, null));
			} catch (IOException e) {
				logger.warn(e);
			}
		}
	}

	/**
	 * 向其他在线用户发送信息
	 * 
	 * @param msg
	 */
	private void sendMessageToOtherUser(int type, String msg) {
		if (logger.isDebugEnabled())
			logger.debug("sendMessageToOtherUser:msg="
					+ buildJson(type, msg, null));
		for (ChatWebSocket member : members) {
			if (!member.sid.equals(this.sid)) {
				try {
					member.connection.sendMessage(buildJson(type, msg, null));
				} catch (IOException e) {
					logger.warn(e);
				}
			}
		}
	}

	SimpleDateFormat formatter = new SimpleDateFormat("H:mm:ss");

	private String buildJson(int type, String msg, String originSid) {
		Json json = new Json();
		json.put("time", formatter.format(new Date()));// 发送的服务器时间
		json.put("type", type);
		json.put("sid", this.sid);
		json.put("name", this.userName);
		json.put("uid", this.userUid);
		json.put("ip", this.userIp);
		json.put("msg", msg);

		if (originSid != null)
			json.put("origin", originSid);
		return json.toString();
	}
}