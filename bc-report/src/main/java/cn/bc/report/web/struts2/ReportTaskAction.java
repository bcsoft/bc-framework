package cn.bc.report.web.struts2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
				.setMinHeight(200).setMinWidth(300);
	}

	@Override
	protected void afterCreate(ReportTask entity) {
		super.afterCreate(entity);
		entity.setIgnoreError(false);
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		
		boolean readonly = this.isReadonly();

		if (editable && !readonly) {
			// 添加查看调度历史按钮
			pageOption.addButton(new ButtonOption(getText("reportTask.viewExcuteRecode"), null,
					"bc.reportTaskForm.viewExcuteRecode"));
			// 添加保存按钮
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bc.reportTaskForm.save"));
		}
	}

	// 启动/重置
		public String start() throws Exception {
			if (this.getIds() == null || this.getIds().length() == 0) {
				throw new CoreException("must set property ids");
			}

			Long[] ids = cn.bc.core.util.StringUtils.stringArray2LongArray(this
					.getIds().split(","));
			for (Long id : ids) {
				
			}

			Json json = new Json();
			json.put("msg", "任务启动/重置成功！");
			this.json=json.toString();
			return "json";
		}

	//停止任务
	public String stop() throws Exception {
		SystemContext context = this.getSystyemContext();
		// 将状态设置为禁用而不是物理删除,更新最后修改人和修改时间
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("status", new Integer(BCConstants.STATUS_DISABLED));
		attributes.put("modifier", context.getUserHistory());
		attributes.put("modifiedDate", Calendar.getInstance());

		if (this.getIds() != null && this.getIds().length() > 0) {
			Long[] ids = cn.bc.core.util.StringUtils
					.stringArray2LongArray(this.getIds().split(","));
			this.reportTaskService.update(ids, attributes);
		} else {
			throw new CoreException("must set property id or ids");
		}
		
		Json json = new Json();
		json.put("msg", "任务停止成功！");
		this.json=json.toString();
		return "json";
	}

}
