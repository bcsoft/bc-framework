/**
 * 
 */
package cn.bc.websocket.web.struts2;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

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

	@Autowired
	public void setChatWebSocketService(
			ChatWebSocketService chatWebSocketService) {
		this.chatWebSocketService = chatWebSocketService;
	}

	public String execute() throws Exception {
		return SUCCESS;
	}

	public StringBuffer html;
	public Set<ChatWebSocket> users;// 在线用户

	// 显示所有在线用户的信息
	public String show() {
		// 获取在线用户列表
		users = this.chatWebSocketService.getAll();
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
		html.append("\r\n<td" + lastTdStyle + ">ID</td>");
		html.append("\r\n</tr>");

		int i = 1;
		for (ChatWebSocket u : users) {
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
			html.append("\r\n<td" + lastTdStyle + ">" + u.getClientId()
					+ "</td>");

			html.append("\r\n</tr>");
			i++;
		}

		html.append("\r\n</table>");

		return "page";
	}
}