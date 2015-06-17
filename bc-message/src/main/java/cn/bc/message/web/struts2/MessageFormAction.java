/**
 *
 */
package cn.bc.message.web.struts2;

import cn.bc.identity.web.SystemContext;
import cn.bc.message.domain.Message;
import cn.bc.message.service.MessageService;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.PageOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * 消息管理表单
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class MessageFormAction extends EntityAction<Long, Message> {
	private static final long serialVersionUID = 1L;

	@Autowired
	public void setMessageService(MessageService messageService) {
		this.setCrudService(messageService);
	}

	@Override
	public boolean isReadonly() {
		// 系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole("BC_ADMIN");
	}

	@Override
	public String getPageNamespace() {
		return this.getContextPath() + "/bc/message";
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(415).setMinWidth(400).setMinHeight(300);
	}
}