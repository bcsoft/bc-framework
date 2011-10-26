/**
 * 
 */
package cn.bc.scheduler.web.struts2;

import java.util.List;

import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.CrudService;
import cn.bc.identity.web.SystemContext;
import cn.bc.scheduler.domain.ScheduleJob;
import cn.bc.scheduler.service.SchedulerManage;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.Grid;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.json.Json;

/**
 * 任务调度配置Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ScheduleJobAction extends EntityAction<Long, ScheduleJob>
		implements SessionAware {
	// private static Log logger = LogFactory.getLog(ScheduleJobAction.class);
	private static final long serialVersionUID = 1L;

	private SchedulerManage schedulerManage;

	@Autowired
	public void setScheduleJobService(
			@Qualifier(value = "scheduleJobService") CrudService<ScheduleJob> scheduleJobService) {
		this.setCrudService(scheduleJobService);
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.admin"));// 超级管理角色
	}

	@Autowired
	public void setSchedulerManage(SchedulerManage schedulerManage) {
		this.schedulerManage = schedulerManage;
	}

	@Override
	protected Grid buildGrid() {
		return super.buildGrid().setSingleSelect(true);
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("name");
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(700).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getPageNamespace() {
		return getContextPath() + this.getActionPathPrefix() + "/schedule/job";
	}

	@Override
	protected Toolbar buildToolbar() {
		Toolbar tb = new Toolbar();

		// 新建
		tb.addButton(getDefaultCreateToolbarButton());

		// 编辑
		tb.addButton(getDefaultEditToolbarButton());

		// 启动/重置
		tb.addButton(new ToolbarButton().setIcon("ui-icon ui-icon-lightbulb")
				.setText(getText("scheduleJob.button.start"))
				.setClick("bc.scheduleJobList.start"));

		// 停止/禁用
		tb.addButton(new ToolbarButton().setIcon("ui-icon-cancel")
				.setText(getText("scheduleJob.button.stop"))
				.setClick("bc.scheduleJobList.stop"));

		// 查看调度日志
		tb.addButton(new ToolbarButton().setIcon("ui-icon-calendar")
				.setText(getText("scheduleJob.button.showLog"))
				.setClick("bc.scheduleJobList.showLog"));

		// 搜索
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "name", "bean", "method", "groupn", "cron" };
	}

	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("status", getText("scheduleJob.status"), 60)
				.setValueFormater(new EntityStatusFormater(getEntityStatuses())));
		columns.add(new TextColumn("name", getText("scheduleJob.name"))
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("cron", getText("scheduleJob.cron"), 150)
				.setSortable(true).setUseTitleFromLabel(true));
		// columns.add(new TextColumn("nextDate",
		// getText("scheduleJob.nextDate"),
		// 110).setValueFormater(new CalendarFormater(
		// "yyyy-MM-dd HH:mm:ss")));
		columns.add(new TextColumn("ignoreError",
				getText("scheduleJob.ignoreError"), 65).setSortable(true)
				.setValueFormater(new BooleanFormater()));
		columns.add(new TextColumn("bean", getText("scheduleJob.bean"), 150)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("method", getText("scheduleJob.method"), 80)
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("orderNo", getText("label.order"), 65)
				.setSortable(true));
		return columns;
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("status", Direction.Asc).add("orderNo", Direction.Asc);
	}

	@Override
	protected String getJs() {
		return getPageNamespace() + "/list.js";
	}

	// 帮助
	public String help() throws Exception {
		return "help";
	}

	@Override
	public String create() throws Exception {
		String r = super.create();
		this.getE().setStatus(ScheduleJob.STATUS_DISABLED);// 初始化为禁用状态
		return r;
	}

	public Json json;

	// 启动/重置
	public String start() throws Exception {
		if (this.getIds() == null || this.getIds().length() == 0) {
			throw new CoreException("must set property ids");
		}

		Long[] ids = cn.bc.core.util.StringUtils.stringArray2LongArray(this
				.getIds().split(","));
		for (Long id : ids) {
			this.schedulerManage.scheduleJob(id);
		}

		this.json = new Json();
		this.json.put("msg", "任务启动/重置成功！");
		return "json";
	}

	// 停止/禁用
	public String stop() throws Exception {
		if (this.getIds() == null || this.getIds().length() == 0) {
			throw new CoreException("must set property ids");
		}

		Long[] ids = cn.bc.core.util.StringUtils.stringArray2LongArray(this
				.getIds().split(","));
		for (Long id : ids) {
			this.schedulerManage.stopJob(id);
		}

		this.json = new Json();
		this.json.put("msg", "任务停止/禁用成功！");
		return "json";
	}
}
