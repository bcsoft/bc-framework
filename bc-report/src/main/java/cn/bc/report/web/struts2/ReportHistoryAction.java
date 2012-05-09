package cn.bc.report.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.service.CrudService;
import cn.bc.identity.web.SystemContext;
import cn.bc.report.domain.ReportHistory;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 历史报表表单Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ReportHistoryAction extends EntityAction<Long, ReportHistory> {
	private static final long serialVersionUID = 1L;
	
	@Autowired
	public void setReportHistoryService(
			@Qualifier(value = "reportHistoryService") CrudService<ReportHistory> crudService) {
		this.setCrudService(crudService);
	}
	
	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：报表管理员，历史报表管理员、超级管理员
		return !context.hasAnyRole(getText("key.role.bc.report"),
				getText("key.role.bc.report.History"),
				getText("key.role.bc.admin"));
	}
	
	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(650)
				.setMinHeight(200).setMinWidth(300);
	}

	

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		
	}
}
