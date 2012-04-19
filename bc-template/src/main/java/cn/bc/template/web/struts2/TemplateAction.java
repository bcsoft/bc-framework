package cn.bc.template.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.template.domain.Template;
import cn.bc.template.service.TemplateService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 模板表单Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class TemplateAction extends FileEntityAction<Long, Template> {
	private static final long serialVersionUID = 1L;
	private TemplateService templateService;

	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.setCrudService(templateService);
		this.templateService = templateService;
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
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		if (!this.isReadonly()) {
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bc.templateForm.save").setId("templateSave"));
		}
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(560)
				.setMinHeight(200).setMinWidth(300);
	}

	@Override
	protected void afterCreate(Template entity) {
		super.afterCreate(entity);
		entity.setType(Template.TYPE_EXCEL);
		// 内置 默认为否
		entity.setInner(false);
	}

	public Integer type;
	public Long tid;
	public String code;

	// 检查编码是否唯一
	public String isUniqueCode() {
		Json json = new Json();
		boolean flag = this.templateService.isUnique(this.tid, code);
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
