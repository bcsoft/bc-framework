package cn.bc.placeorigin.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.service.CrudService;
import cn.bc.identity.web.SystemContext;
import cn.bc.placeorigin.domain.PlaceOrigin;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 籍贯表单Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class PlaceOriginAction extends EntityAction<Long, PlaceOrigin> {
	private static final long serialVersionUID = 1L;
	
	@Autowired
	public void setPlaceOriginService(
			@Qualifier(value = "placeOriginService") CrudService<PlaceOrigin> crudService){
			this.setCrudService(crudService);
	}
	
	@Override
	public boolean isReadonly() {
		// 系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.admin"));
	}
	
	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(600)
				.setMaxHeight(300);
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
				pageOption
				.addButton(new ButtonOption(getText("label.save"), "save"));
		}
	}
	
	
	
}
