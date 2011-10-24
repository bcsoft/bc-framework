/**
 * 
 */
package cn.bc.scheduler.web.struts2;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.CrudService;
import cn.bc.identity.web.SystemContext;
import cn.bc.scheduler.domain.ScheduleLog;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.CalendarRangeFormaterEx;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.Grid;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 任务调度日志Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ScheduleLogAction extends EntityAction<Long, ScheduleLog> {
	// private static Log logger = LogFactory.getLog(ScheduleLogAction.class);
	private static final long serialVersionUID = 1L;

	@Autowired
	public void setScheduleLogService(
			@Qualifier(value = "scheduleLogService") CrudService<ScheduleLog> crudService) {
		this.setCrudService(crudService);
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.admin"));// 超级管理角色
	}

	@Override
	protected Grid buildGrid() {
		return super.buildGrid().setSingleSelect(true).setDblClickRow("bc.page.open");
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("cfgName");
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(680).setMinWidth(300)
				.setMinHeight(300).setHeight(450);
	}

	@Override
	protected String getPageNamespace() {
		return getContextPath() + this.getActionPathPrefix() + "/schedule/log";
	}

	@Override
	protected Toolbar buildToolbar() {
		Toolbar tb = new Toolbar();
		// 查看
		tb.addButton(getDefaultOpenToolbarButton());

		// 搜索
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "cfgName", "cfgCron", "cfgBean", "cfgMethod" };
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("startDate", Direction.Desc);
	}

	@Override
	protected List<Column> buildGridColumns() {
		List<Column> columns = super.buildGridColumns();
		columns.add(new TextColumn("success", getText("scheduleLog.success"),
				65).setSortable(true).setValueFormater(new BooleanFormater()));
		columns.add(new TextColumn("cfgName", getText("scheduleLog.cfgName"))
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn("startDate",
				getText("scheduleLog.startDate2endDate"), 310)
				.setSortable(true).setDir(Direction.Desc)
				.setValueFormater(new CalendarRangeFormaterEx() {
					@Override
					public Calendar getToDate(Object context, Object value) {
						ScheduleLog log = (ScheduleLog) context;
						return log.getEndDate();
					}
				}));
		return columns;
	}
}
