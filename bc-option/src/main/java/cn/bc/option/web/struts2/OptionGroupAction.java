/**
 * 
 */
package cn.bc.option.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.service.CrudService;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionGroup;
import cn.bc.web.struts2.EntityAction;

/**
 * 选项分组表单Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class OptionGroupAction extends EntityAction<Long, OptionGroup> {
	private static final long serialVersionUID = 1L;

	@Autowired
	public void setOptionGroupService(
			@Qualifier(value = "optionGroupService") CrudService<OptionGroup> crudService) {
		this.setCrudService(crudService);
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.option"),
				getText("key.role.bc.admin"));// 选项管理或超级管理角色
	}
}
