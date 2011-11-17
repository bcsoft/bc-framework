/**
 * 
 */
package cn.bc.chat.web.struts2;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 在线聊天Action
 * 
 * @author dragon
 * 
 */
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class OnlineMessageAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	// private static Log logger = LogFactory.getLog(OnlineMessageAction.class);

	public String title;
	public String toSid;
	public String toName;
	public String toIp;

	public String execute() throws Exception {
		title = toName + (toIp != null ? " (" + toIp + ")" : "");
		return SUCCESS;
	}
}