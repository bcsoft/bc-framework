package cn.bc.template.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
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
			pageOption.addButton(new ButtonOption(getText("template.show.history.version"), null,
					"bc.templateForm.showVersion").setId("templateShowVersion"));
			pageOption.addButton(new ButtonOption(getText("label.preview.inline"), null,
					"bc.templateList.inline").setId("templateInline"));
			if(editable)
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
		//状态正常
		entity.setStatus(BCConstants.STATUS_ENABLED);
	}
	
	

	@Override
	public String save() throws Exception {
		Template template=this.getE();
		//状态非正常
		if(template.getStatus()!=BCConstants.STATUS_ENABLED){
			this.beforeSave(template);
			this.templateService.getCrudDao().save(template);
			this.afterSave(template);
			return "saveSuccess";
		}
		
		//状态为正常
		this.beforeSave(template);
	    this.templateService.saveTpl(template);
	    this.afterSave(template);
	    
	    return "saveSuccess";
	}



	public Integer type;
	public Long tid;
	public String code;
	public String version;

	// 检查编码与版本号唯一
	public String isUniqueCodeAndVersion() {
		Json json = new Json();
		boolean flag = this.templateService.isUniqueCodeAndVersion(this.tid, code,version);
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
