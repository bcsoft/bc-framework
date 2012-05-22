package cn.bc.report.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.exception.CoreException;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.report.domain.ReportTask;
import cn.bc.report.service.ReportTaskService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 报表任务表单Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ReportTaskAction extends FileEntityAction<Long, ReportTask> {
	private static final long serialVersionUID = 1L;

	public ReportTaskService reportTaskService;

	@Autowired
	public void setReportTaskService(ReportTaskService reportTaskService) {
		this.reportTaskService = reportTaskService;
		this.setCrudService(reportTaskService);
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：报表管理员，报表任务管理员、超级管理员
		return !context.hasAnyRole(getText("key.role.bc.report"),
				getText("key.role.bc.report.Task"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(650)
				.setMinHeight(200).setMinWidth(300).setHeight(460);
	}

	@Override
	protected void afterCreate(ReportTask entity) {
		super.afterCreate(entity);
		entity.setIgnoreError(false);
		entity.setStatus(BCConstants.STATUS_DISABLED);
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {

		boolean readonly = this.isReadonly();

		if (editable && !readonly) {
			// 添加查看调度历史按钮
			pageOption.addButton(new ButtonOption(
					getText("reportTask.viewExcuteRecode"), null,
					"bc.reportTaskForm.viewExcuteRecode"));
			// 添加保存按钮
			pageOption.addButton(this.getDefaultSaveButtonOption());
		}
	}

	// 启动/重置
	public String start() throws Exception {
		if (this.getIds() == null || this.getIds().length() == 0) {
			throw new CoreException("must set property ids");
		}

		Long[] ids = cn.bc.core.util.StringUtils.stringArray2LongArray(this
				.getIds().split(","));
		Json json = new Json();
		try {
			for (Long id : ids) {
				// 逐个任务处理，避免某个处理失败影响其它任务的处理
				json.put("_id", id);
				this.reportTaskService.doStart(id);
			}
			json.put("success", true);
			json.put("msg", "报表任务启动/重置成功！");
		} catch (Exception e) {
			json.put("success", false);
			json.put("msg", "报表任务启动/重置失败！");
		}

		this.json = json.toString();
		return "json";
	}

	// 停止任务
	public String stop() throws Exception {
		if (this.getIds() == null || this.getIds().length() == 0) {
			throw new CoreException("must set property ids");
		}

		Long[] ids = cn.bc.core.util.StringUtils.stringArray2LongArray(this
				.getIds().split(","));
		Json json = new Json();
		try {
			for (Long id : ids) {
				// 逐个任务处理，避免某个处理失败影响其它任务的处理
				json.put("_id", id);
				this.reportTaskService.doStop(id);
			}
			json.put("success", true);
			json.put("msg", "报表任务停止成功！");
		} catch (Exception e) {
			json.put("success", false);
			json.put("msg", "报表任务停止失败！");
		}

		this.json = json.toString();
		return "json";
	}
}
