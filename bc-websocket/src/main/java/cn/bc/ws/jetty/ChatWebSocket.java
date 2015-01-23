package cn.bc.ws.jetty;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bc.web.util.WebUtils;

public class ChatWebSocket implements WebSocket.OnTextMessage {
	private final Log logger = LogFactory.getLog(ChatWebSocket.class);
	public final static int TYPE_ONLINE = 0;// 用户上线信息
	public final static int TYPE_OFFLINE = 1;// 用户下线信息
	public final static int TYPE_USER = 2;// 用户发到用户的信息
	public final static int TYPE_BROADCAST = 3;// 全局广播的信息
	public final static int TYPE_SYSTIP = 4;// 用户发到用户出现异常时系统返回信息
	private Calendar createDate;// 创建时间
	private String clientId;// 客户端用户的id
	private String clientName;// 客户端用户的名称
	private String clientIp;// 客户端的ip地址
	private String sid;// 客户端的会话session id
	private Object client;// 客户端的详细对象信息
	private List<String> ignoreOnlineRemindUserIds;// 忽略上下线提醒的帐号的ID列表
	private Connection connection;
	private ChatWebSocketService webSocketService = WebUtils
			.getBean(ChatWebSocketService.class);

	public ChatWebSocket() {
		this.createDate = Calendar.getInstance();
	}

	public ChatWebSocket(String sid, String clientIp, String clientId,
			String clientName) {
		this.clientIp = clientIp;
		this.clientId = clientId;
		this.clientName = clientName;
		this.sid = sid;
		this.createDate = Calendar.getInstance();
	}

	public Calendar getCreateDate() {
		return createDate;
	}

	public Object getClient() {
		return client;
	}

	public void setClient(Object client) {
		this.client = client;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getClientId() {
		return clientId;
	}

	public List<String> getIgnoreOnlineRemindUserIds() {
		return ignoreOnlineRemindUserIds;
	}

	public void setIgnoreOnlineRemindUserIds(
			List<String> ignoreOnlineRemindUserIds) {
		this.ignoreOnlineRemindUserIds = ignoreOnlineRemindUserIds;
	}

	public String getClientName() {
		return clientName;
	}

	public String getClientIp() {
		return clientIp;
	}

	public String getSid() {
		return sid;
	}

	public Connection getConnection() {
		return connection;
	}

	public void onOpen(Connection connection) {
		if (logger.isDebugEnabled())
			logger.debug("onOpen");
		this.connection = connection;
		this.webSocketService.addMember(this);

		// 向其它用户发送上线信息
		if (ignoreOnlineRemindUserIds == null
				|| !ignoreOnlineRemindUserIds.contains(this.clientId)) {
			String msg = this.clientName + "上线了！";
			this.webSocketService.sendMessageToOtherClient(this.sid,
					TYPE_ONLINE, msg);
		}
	}

	public void onClose(int code, String message) {
		if (logger.isDebugEnabled())
			logger.debug("onClose:code=" + code + ",message=" + message);

		// 向其它用户发送下线信息
		String msg = this.clientName + "下线了！";
		try {
			if (ignoreOnlineRemindUserIds == null
					|| !ignoreOnlineRemindUserIds.contains(this.clientId)) {
				this.webSocketService.sendMessageToOtherClient(this.sid,
						TYPE_OFFLINE, msg);
			}
			if (logger.isDebugEnabled())
				logger.debug("onClose1");

			// 删除当前连接
			this.webSocketService.removeMember(this);

			this.connection = null;
		} catch (Exception e) {
			// 这个异常是在jetty的websocket在session超时关闭时自动创建并销毁一个临时的session导致的
			logger.info("sid=" + this.sid + ":" + e.getMessage(), e);
		}
	}

	public void onMessage(String data) {
		if (logger.isDebugEnabled())
			logger.debug("onMessage:data=" + data);

		// data的格式：{type:[TYPE_XXXX],toSid:[toSid],msg:[message]}
		try {
			JSONObject json = new JSONObject(data);
			int type = json.getInt("type");

			if (type == -1) {// 关闭连接
				connection.disconnect();
			} else {// 发送信息
				String toSid = json.getString("toSid");
				if (toSid != null && toSid.length() > 0) {
					if (!this.webSocketService.isExisting(toSid)) {
						// 提示用户不在线
						this.webSocketService.sendMessageBack(this.sid, toSid,
								"用户不在线，无法收到你发送的消息！");
						return;
					}
				}
				this.webSocketService.sendMessage(this.sid, toSid, type,
						json.getString("msg"));
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}
	}
}