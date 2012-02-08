/**
 * 
 */
package cn.bc.websocket.web.struts2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.chat.OnlineUser;
import cn.bc.chat.service.OnlineUserService;
import cn.bc.core.util.DateUtils;
import cn.bc.websocket.jetty.ChatWebSocket;
import cn.bc.websocket.jetty.ChatWebSocketService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 在线用户列表Action
 * 
 * @author dragon
 * 
 */
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ChatWebSocketAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	// private static Log logger = LogFactory.getLog(OnlineUserAction.class);
	private ChatWebSocketService chatWebSocketService;
	private OnlineUserService onlineUserService;

	@Autowired
	public void setChatWebSocketService(
			ChatWebSocketService chatWebSocketService) {
		this.chatWebSocketService = chatWebSocketService;
	}

	@Autowired
	public void setOnlineUserService(OnlineUserService onlineUserService) {
		this.onlineUserService = onlineUserService;
	}

	public List<OnlineUser> users;// 在线用户

	public String execute() throws Exception {
		// 获取在线websocket列表
		chatWebSockets = this.chatWebSocketService.getAll();

		// 获取相应的在线用户列表
		users = new ArrayList<OnlineUser>();
		String mySid = (String) ServletActionContext.getRequest().getSession()
				.getAttribute("sid");
		for (ChatWebSocket u : chatWebSockets) {
			if (!u.getSid().equals(mySid)) {// 排除自己
				users.add(this.onlineUserService.get(u.getSid()));
			}
		}

		return SUCCESS;
	}

	public StringBuffer html;
	public Set<ChatWebSocket> chatWebSockets;// 在线websocket

	// 显示所有在线用户的信息
	public String show() {
		// 获取在线用户列表
		chatWebSockets = this.chatWebSocketService.getAll();
		html = new StringBuffer(
				"<table class='table' cellspacing='0' cellpadding='4' style='border-color:#ccc;border-style:solid;border-width:1px 1px 0 1px;'>");

		String firstTdStyle = " style='border-color:#ccc;border-style:solid;border-width:0 1px 1px 0;'";
		String middleTdStyle = " style='border-color:#ccc;border-style:solid;border-width:0 1px 1px 0;'";
		String lastTdStyle = " style='border-color:#ccc;border-style:solid;border-width:0 0 1px 0;'";

		// 列头
		html.append("\r\n<tr>");
		html.append("\r\n<td" + firstTdStyle + ">序号</td>");
		html.append("\r\n<td" + middleTdStyle + ">上线时间</td>");
		html.append("\r\n<td" + middleTdStyle + ">姓名</td>");
		html.append("\r\n<td" + middleTdStyle + ">IP</td>");
		html.append("\r\n<td" + middleTdStyle + ">SID</td>");
		html.append("\r\n<td" + middleTdStyle + ">ID</td>");
		html.append("\r\n<td" + lastTdStyle + ">Client</td>");
		html.append("\r\n</tr>");

		int i = 1;
		for (ChatWebSocket u : chatWebSockets) {
			html.append("\r\n<tr>");

			html.append("\r\n<td" + firstTdStyle + ">" + i + "</td>");
			html.append("\r\n<td" + middleTdStyle + ">"
					+ DateUtils.formatCalendar2Second(u.getCreateDate())
					+ "</td>");
			html.append("\r\n<td" + middleTdStyle + ">" + u.getClientName()
					+ "</td>");
			html.append("\r\n<td" + middleTdStyle + ">" + u.getClientIp()
					+ "</td>");
			html.append("\r\n<td" + middleTdStyle + ">" + u.getSid() + "</td>");
			html.append("\r\n<td" + middleTdStyle + ">" + u.getClientId()
					+ "</td>");
			html.append("\r\n<td" + lastTdStyle + ">" + u.getClient() + "</td>");

			html.append("\r\n</tr>");
			i++;
		}

		html.append("\r\n</table>");

		return "page";
	}
}