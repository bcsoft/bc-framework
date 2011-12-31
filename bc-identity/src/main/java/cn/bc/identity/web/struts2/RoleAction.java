/**
 * 
 */
package cn.bc.identity.web.struts2;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.identity.domain.Resource;
import cn.bc.identity.domain.Role;
import cn.bc.identity.service.RoleService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 角色表单Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class RoleAction extends EntityAction<Long, Role> {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.actor"),
				getText("key.role.bc.admin"));// 超级管理员
	}

	@Autowired
	public void setRoleService(RoleService roleService) {
		this.setCrudService(roleService);
	}

	@Override
	protected void afterCreate(Role entity) {
		this.getE().setType(Role.TYPE_DEFAULT);
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(618);
	}

	@Override
	protected ButtonOption getDefaultSaveButtonOption() {
		return super.getDefaultSaveButtonOption().setAction(null)
				.setClick("bc.roleForm.save");
	}

	public String assignResourceIds;// 分派的模块id，多个id用逗号连接

	@Override
	protected void beforeSave(Role entity) {
		// 处理分配的模块
		Set<Resource> resources = null;
		if (this.assignResourceIds != null
				&& this.assignResourceIds.length() > 0) {
			resources = new HashSet<Resource>();
			String[] resourceIds = this.assignResourceIds.split(",");
			Resource resource;
			for (String resourceId : resourceIds) {
				resource = new Resource();
				resource.setId(new Long(resourceId));
				resources.add(resource);
			}
		}
		if (this.getE().getResources() != null) {
			this.getE().getResources().clear();
			this.getE().getResources().addAll(resources);
		} else {
			this.getE().setResources(resources);
		}
	}
}
