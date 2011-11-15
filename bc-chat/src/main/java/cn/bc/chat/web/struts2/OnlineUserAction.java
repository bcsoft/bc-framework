/**
 * 
 */
package cn.bc.chat.web.struts2;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.Context;
import cn.bc.chat.OnlineUser;
import cn.bc.chat.service.OnlineUserService;
import cn.bc.identity.web.SystemContext;

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
		users = new ArrayList<OnlineUser>();
		users.addAll(this.onlineService.getAll());

		// 剔除自己
		Long myId = ((SystemContext) ServletActionContext.getRequest()
				.getSession().getAttribute(Context.KEY)).getUser().getId();
		OnlineUser onlineUser = null;
		for (OnlineUser u : users) {
			if (u.getId().equals(myId)) {
				onlineUser = u;
				break;
			}
		}
		if (onlineUser != null)
			users.remove(onlineUser);

		return SUCCESS;
	}
}