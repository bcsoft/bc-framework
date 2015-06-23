/**
 *
 */
package cn.bc.scheduler.web.struts2;

import cn.bc.BCConstants;
import cn.bc.core.service.CrudService;
import cn.bc.identity.web.SystemContext;
import cn.bc.scheduler.domain.ScheduleJob;
import cn.bc.scheduler.service.SchedulerManage;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.PageOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 定时任务表单
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ScheduleJobFormAction extends EntityAction<Long, ScheduleJob> {
	private static final long serialVersionUID = 1L;

	@Autowired
	private SchedulerManage schedulerManage;

	@Autowired
	public void setScheduleJobService(@Qualifier(value = "scheduleJobService") CrudService<ScheduleJob> scheduleJobService) {
		this.setCrudService(scheduleJobService);
	}

	@Override
	public boolean isReadonly() {
		// 组织架构管理或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return schedulerManage.isDisabled() || !context.hasAnyRole("BC_ORGANIZE_MANAGE", "BC_ADMIN");
	}

	@Override
	public String getPageNamespace() {
		return this.getContextPath() + "/bc/schedule/job";
	}

	@Override
	protected void addJsCss(List<String> container) {
		container.add("bc/schedule/job/form.js");
	}

	@Override
	protected PageOption buildPageOption(boolean editable) {
		return super.buildPageOption(editable).setWidth(635).setMinWidth(280).setMinHeight(200);
	}

	@Override
	protected void buildPageButtons(PageOption pageOption, boolean editable) {
		super.buildPageButtons(pageOption, editable);
	}

	@Override
	protected void afterCreate(ScheduleJob entity) {
		this.getE().setStatus(BCConstants.STATUS_DISABLED);// 初始化为禁用状态
		this.getE().setGroupn("bc");
	}
}