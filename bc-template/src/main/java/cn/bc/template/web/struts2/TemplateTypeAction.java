package cn.bc.template.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.template.domain.TemplateType;
import cn.bc.template.service.TemplateTypeService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 模板类型表单Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class TemplateTypeAction extends FileEntityAction<Long, TemplateType> {
	private static final long serialVersionUID = 1L;
	private TemplateTypeService templateTypeService;

	@Autowired
	public void setTemplateTypeService(TemplateTypeService templateTypeService) {
		this.setCrudService(templateTypeService);
		this.templateTypeService = templateTypeService;
	}

	@Override
	public boolean isReadonly() {
		// 模板管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：模板管理员
		return !context.hasAnyRole(getText("key.role.bc.template"),
				getText("key.role.bc.admin"));
	}


	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(510)
				.setMinHeight(200).setMinWidth(300);
	}
	
	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		if (!this.isReadonly()) {
			if (editable)
				pageOption.addButton(new ButtonOption(getText("label.save"),
						null, "bc.templateTypeForm.save").setId("templateTypeSave"));
		}
	}

	@Override
	protected void afterCreate(TemplateType entity) {
		super.afterCreate(entity);
		// 状态正常
		entity.setStatus(BCConstants.STATUS_ENABLED);
		//关联附件
		entity.setPath(true);
		//纯文本
		entity.setPureText(false);
	}

	public Long tid;// 模板类型id
	public String code;// 编码

	// 检查编码唯一
	public String isUniqueCode() {
		Json json = new Json();
		boolean flag = this.templateTypeService.isUniqueCode(this.tid,
				code);
		if (flag) {
			json.put("result", getText("template.save.code"));
			this.json = json.toString();
			return "json";
		} else {
			json.put("result", "save");
			this.json = json.toString();
			return "json";
		}
	}

	
}
