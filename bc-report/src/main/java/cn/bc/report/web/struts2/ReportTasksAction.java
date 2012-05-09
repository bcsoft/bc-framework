package cn.bc.report.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 报表任务视图Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ReportTasksAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED);

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：报表管理员，报表任务管理员、超级管理员
		return !context.hasAnyRole(getText("key.role.bc.report"),
				getText("key.role.bc.report.Task"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("a.order_");
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id,a.status_ as status,a.order_ as orderNo,t.category,a.name,t.name as tname,a.cron,a.desc_ as desc");
		sql.append(",b.actor_name as uname,a.file_date,c.actor_name as mname,a.modified_date");
		sql.append(" from bc_report_task a");
		sql.append(" inner join bc_report_template t on t.id=a.pid");
		sql.append(" inner join bc_identity_actor_history b on b.id=a.author_id ");
		sql.append(" left join bc_identity_actor_history c on c.id=a.modifier_id ");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status", rs[i++]);
				map.put("orderNo", rs[i++]);
				map.put("category", rs[i++]);
				map.put("name", rs[i++]);
				map.put("tname", rs[i++]);
				map.put("cron", rs[i++]);
				map.put("desc_", rs[i++]);
				map.put("uname", rs[i++]);
				map.put("file_date", rs[i++]);
				map.put("mname", rs[i++]);
				map.put("modified_date", rs[i++]);			
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("a.id", "id"));
		columns.add(new TextColumn4MapKey("a.status_", "status",
				getText("report.status"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(this.getStatuses())));
		columns.add(new TextColumn4MapKey("a.order_", "orderNo",
				getText("report.order"), 60).setSortable(true));
		columns.add(new TextColumn4MapKey("t.category", "category",
				getText("report.category"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.name", "name",
				getText("reportTask.name"), 200).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.name", "tname",
				getText("reportTask.template"), 200).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.cron", "cron",
				getText("reportTask.cron"), 100).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.desc_", "desc_",
				getText("report.desc")).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.actor_name", "uname",
				getText("report.author"), 80));
		columns.add(new TextColumn4MapKey("a.file_date", "file_date",
				getText("report.fileDate"), 130)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("c.actor_name", "mname",
				getText("report.modifier"), 80));
		columns.add(new TextColumn4MapKey("a.modified_date", "modified_date",
				getText("report.modifiedDate"), 130)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		return columns;
	}
	
	//状态键值转换
	private Map<String,String> getStatuses(){
		Map<String,String> statuses=new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED)
				, getText("report.status.normal"));
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED)
				, getText("report.status.disabled"));
		statuses.put(""
				, getText("report.status.all"));
		return statuses;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "t.name", "b.actor_name", "a.name","t.category" };
	}

	@Override
	protected String getFormActionName() {
		return "reportTask";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		if (!this.isReadonly()) {
			// 新建按钮
			tb.addButton(this.getDefaultCreateToolbarButton());

			// 编辑按钮
			tb.addButton(this.getDefaultEditToolbarButton());
			// 禁用按钮
			tb.addButton(this.getDefaultDisabledToolbarButton());
		}
		//状态按钮组
		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
				this.getStatuses(), "status", 0, getText("report.status.tips")));

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}
	
	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if(status != null && status.length() > 0){
			statusCondition = new EqualsCondition("a.status_",Integer.parseInt(status));
		}
		return statusCondition;
	}

	/*// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}
	// ==高级搜索代码结束==
*/
}
