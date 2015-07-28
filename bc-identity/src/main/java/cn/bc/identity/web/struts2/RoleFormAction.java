/**
 *
 */
package cn.bc.identity.web.struts2;

import cn.bc.identity.domain.Resource;
import cn.bc.identity.domain.Role;
import cn.bc.identity.service.RoleService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.PageOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Set;

/**
 * 角色表单Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class RoleFormAction extends EntityAction<Long, Role> {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isReadonly() {
		// 角色管理或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole("BC_ROLE_MANAGE", "BC_ADMIN");
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
	protected PageOption buildPageOption(boolean editable) {
		return super.buildPageOption(editable).setWidth(618).setHeight(400).setMinWidth(450).setMinHeight(300);
	}

	@Override
	protected void beforeSave(Role entity) {
		// 避免保存时丢失与资源关系的处理
		if(!this.getE().isNew()) {
			Set<Resource> resources = this.getCrudService().load(this.getE().getId()).getResources();
			if (this.getE().getResources() != null) {
				this.getE().getResources().clear();
				this.getE().getResources().addAll(resources);
			} else {
				this.getE().setResources(resources);
			}
		}
	}

	@Override
	protected boolean isQuirksMode() {
		return false;
	}
}