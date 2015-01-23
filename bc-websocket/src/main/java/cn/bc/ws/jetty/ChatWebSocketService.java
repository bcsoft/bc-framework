package cn.bc.ws.jetty;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * WebSocket服务接口
 * 
 * @author dragon
 * 
 */
public interface ChatWebSocketService {
	/**
	 * 根据请求信息创建一个WebSocket实例
	 * 
	 * @param request
	 * @return
	 */
	ChatWebSocket createWebSocket(HttpServletRequest request);

	/**
	 * 添加一个WebSocket连接记录
	 * 
	 * @param webSocket
	 */
	void addMember(ChatWebSocket webSocket);

	/**
	 * 添加一个WebSocket连接
	 * 
	 * @param webSocket
	 */
	void removeMember(ChatWebSocket webSocket);

	/**
	 * 判断一个WebSocket连接是否存在
	 * 
	 * @param sid
	 *            WebSocket的标识id
	 */
	boolean isExisting(String sid);

	/**
	 * 获取指定sid的WebSocket连接
	 * 
	 * @param sid
	 * @return
	 */
	ChatWebSocket get(String sid);

	/**
	 * 发送信息给指定的连接
	 * 
	 * @param fromSid
	 *            发送者的sid
	 * @param toSid
	 *            接收者的sid
	 * @param type
	 *            消息的类型，见ChatWebSocket.TYPE_XXX常数的定义
	 * @param msg
	 *            要发送的信息
	 */
	void sendMessage(String fromSid, String toSid, int type, String msg);

	/**
	 * 发送信息给所有的连接
	 * 
	 * @param fromSid
	 *            发送者的sid
	 * @param type
	 *            消息的类型
	 * @param msg
	 *            要发送的信息
	 */
	void sendMessageToAllClient(String fromSid, int type, String msg);

	/**
	 * 发送信息给其它连接
	 * 
	 * @param fromSid
	 *            发送者的sid
	 * @param type
	 *            消息的类型
	 * @param msg
	 *            要发送的信息
	 */
	void sendMessageToOtherClient(String fromSid, int type, String msg);

	/**
	 * 系统回发信息给自己
	 * 
	 * @param fromSid
	 *            原发送者的sid
	 * @param toSid
	 *            原接收者的sid
	 * @param msg
	 *            要发送的信息
	 */
	void sendMessageBack(String fromSid, String toSid, String msg);

	/**
	 * 通知其它用户指定的连接断开不再在线了
	 * 
	 * @param sid
	 *            断开连接的sid
	 */
	void offline(String sid);

	/**
	 * 指定的客户端登录了
	 * 
	 * @param sid
	 *            客户端的sid
	 * @param relogin
	 *            是否是重新登录
	 */
	void memberLogin(String sid, boolean relogin);

	/**
	 * 获取所有在线聊天客户端
	 * 
	 * @return
	 */
	Set<ChatWebSocket> getAll();
}