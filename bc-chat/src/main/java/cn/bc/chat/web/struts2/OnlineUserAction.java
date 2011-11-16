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
	private OnlineUserService onlineService;

	@Autowired
	public void setOnlineService(OnlineUserService onlineService) {
		this.onlineService = onlineService;
	}

	public List<OnlineUser> users;// 在线用户

	public String execute() throws Exception {
		// 获取在线用户列表
		users = this.onlineService.getAll();

		// 剔除自己
		String sid = ServletActionContext.getRequest().getSession().getId();
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
}