/**
 * 
 */
package cn.bc.chat.web.struts2;

import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.chat.OnlineUser;
import cn.bc.chat.service.OnlineUserService;
import cn.bc.core.util.DateUtils;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 在线用户列表Action
 * 
 * @author dragon
 * 
 */
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class OnlineUserAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	// private static Log logger = LogFactory.getLog(OnlineUserAction.class);
	private OnlineUserService onlineUserService;

	@Autowired
	public void setOnlineUserService(OnlineUserService onlineUserService) {
		this.onlineUserService = onlineUserService;
	}

	public List<OnlineUser> users;// 在线用户

	public String execute() throws Exception {
		// 获取在线用户列表
		users = this.onlineUserService.getAll();

		// 剔除自己
		String sid = (String) ServletActionContext.getRequest().getSession()
				.getAttribute("sid");
		OnlineUser onlineUser = null;
		for (OnlineUser u : users) {
			if (u.getSid().equals(sid)) {
				onlineUser = u;
				break;
			}
		}
		if (onlineUser != null)
			users.remove(onlineUser);

		return SUCCESS;
	}

	public StringBuffer html;

	// 显示所有在线用户的信息
	public String show() {
		// 获取在线用户列表
		users = this.onlineUserService.getAll();
		html = new StringBuffer(
				"<table class='table' cellspacing='0' cellpadding='4' style='border-color:#ccc;border-style:solid;border-width:1px 1px 0 1px;'>");

		String firstTdStyle = " style='border-color:#ccc;border-style:solid;border-width:0 1px 1px 0;'";
		String middleTdStyle = " style='border-color:#ccc;border-style:solid;border-width:0 1px 1px 0;'";
		String lastTdStyle = " style='border-color:#ccc;border-style:solid;border-width:0 0 1px 0;'";
		// 列头
		html.append("\r\n<tr>");
		html.append("\r\n<td" + firstTdStyle + ">序号</td>");
		html.append("\r\n<td" + middleTdStyle + ">上线时间</td>");
		html.append("\r\n<td" + middleTdStyle + ">账号</td>");
		html.append("\r\n<td" + middleTdStyle + ">姓名</td>");
		html.append("\r\n<td" + middleTdStyle + ">上级</td>");
		html.append("\r\n<td" + middleTdStyle + ">IP</td>");
		html.append("\r\n<td" + middleTdStyle + ">SID</td>");
		html.append("\r\n<td" + middleTdStyle + ">MAC</td>");
		html.append("\r\n<td" + middleTdStyle + ">ID</td>");
		html.append("\r\n<td" + lastTdStyle + ">User Agent</td>");
		html.append("\r\n</tr>");

		int i = 1;
		OnlineUser u;
		for (int j = users.size() - 1; j >= 0; j--) {
			u = users.get(j);
			html.append("\r\n<tr>");

			html.append("\r\n<td" + firstTdStyle + ">" + i + "</td>");
			html.append("\r\n<td" + middleTdStyle + ">"
					+ DateUtils.formatCalendar2Second(u.getLoginTime())
					+ "</td>");
			html.append("\r\n<td" + middleTdStyle + ">" + u.getCode() + "</td>");
			html.append("\r\n<td" + middleTdStyle + ">" + u.getName() + "</td>");
			html.append("\r\n<td" + middleTdStyle + ">" + u.getPname()
					+ "</td>");
			html.append("\r\n<td" + middleTdStyle + ">" + u.getIp() + "</td>");
			html.append("\r\n<td" + middleTdStyle + ">" + u.getSid() + "</td>");
			html.append("\r\n<td" + middleTdStyle + ">" + u.getMac() + "</td>");
			html.append("\r\n<td" + middleTdStyle + ">" + u.getId() + "</td>");
			html.append("\r\n<td" + lastTdStyle + ">" + u.getBrowser() + "</td>");

			html.append("\r\n</tr>");
			i++;
		}

		html.append("\r\n</table>");

		return "page";
	}
}