package cn.bc.report.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.report.domain.ReportTemplate;
import cn.bc.report.service.ReportTemplateService;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 报表模板表单Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ReportTemplateAction extends FileEntityAction<Long, ReportTemplate> {
	private static final long serialVersionUID = 1L;
	private ReportTemplateService reportTemplateService;

	
	
	@Autowired
	public void setReportTemplateService(ReportTemplateService reportTemplateService) {
		this.reportTemplateService = reportTemplateService;
	}

	@Override
	public boolean isReadonly() {

		// 模板管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：报表模板管理员
		return !context.hasAnyRole(getText("key.role.bc.report.template"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		if (!this.isReadonly()) {
		}
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(560)
				.setMinHeight(200).setMinWidth(300);
	}



}
