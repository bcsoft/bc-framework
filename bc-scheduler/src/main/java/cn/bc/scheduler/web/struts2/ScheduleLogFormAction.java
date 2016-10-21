/**
 *
 */
package cn.bc.scheduler.web.struts2;

import cn.bc.core.service.CrudService;
import cn.bc.scheduler.domain.ScheduleLog;
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
 * 定时任务调度日志表单
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ScheduleLogFormAction extends EntityAction<Long, ScheduleLog> {
	private static final long serialVersionUID = 1L;

	@Autowired
	private SchedulerManage schedulerManage;

	@Autowired
	public void setScheduleLogService(@Qualifier(value = "scheduleLogService") CrudService<ScheduleLog> crudService) {
		this.setCrudService(crudService);
	}

	@Override
	public boolean isReadonly() {
		return true;
	}

	@Override
	public String getPageNamespace() {
		return this.getContextPath() + "/bc/schedule/log";
	}

	@Override
	protected void addJsCss(List<String> container) {
		container.add("bc/schedule/log/form.js");
	}

	@Override
	protected PageOption buildPageOption(boolean editable) {
		return super.buildPageOption(editable).setWidth(618).setMinWidth(250).setMinHeight(150);
	}
}