package cn.bc.report.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.query.condition.impl.QlCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.report.service.ReportHistoryService;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.json.Json;

/**
 * 历史报表视图Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ReportHistorysAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String success = String.valueOf(true);
	public Long taskId;
	public boolean my = false;

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：报表管理员，历史报表管理员、超级管理员
		return !context.hasAnyRole(getText("key.role.bc.report"),
				getText("key.role.bc.report.History"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("a.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句
		sqlObject
				.setSelect("a.id,a.success,a.file_date,a.category,a.subject,a.path,b.actor_name as uname,a.task_id");
		sqlObject.setFrom("bc_report_history a"
				+ " inner join bc_identity_actor_history b on b.id=a.author_id"
				+ " left join bc_report_task c on c.id=a.task_id"
				+ " left join bc_report_template d on d.id=c.pid");

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("success", rs[i++]);
				map.put("file_date", rs[i++]);
				map.put("category", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("path", rs[i++]);
				map.put("uname", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("a.id", "id"));
		columns.add(new TextColumn4MapKey("a.success", "success",
				getText("report.status"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(this.getStatuses())));
		columns.add(new TextColumn4MapKey("a.file_date", "file_date",
				getText("report.fileDate"), 130)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("a.category", "category",
				getText("report.category"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.subject", "subject",
				getText("reportHistory.subject"), 200)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.path", "path",
				getText("reportHistory.path"), 200).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.actor_name", "uname",
				getText("report.author")));
		return columns;
	}

	// 状态键值转换
	private Map<String, String> getStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(true),
				getText("reportHistory.status.success"));
		statuses.put(String.valueOf(false),
				getText("reportHistory.status.lost"));
		statuses.put("", getText("report.status.all"));
		return statuses;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "a.subject", "b.actor_name", "a.category" };
	}

	@Override
	protected String getFormActionName() {
		return my ? "myReportHistory" : "reportHistory";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		// 下载
		tb.addButton(new ToolbarButton().setIcon("ui-icon-arrowthickstop-1-s")
				.setText(getText("label.download"))
				.setClick("bc.reportHistoryList.download"));
		// 在线预览
		tb.addButton(new ToolbarButton().setIcon("ui-icon-lightbulb")
				.setText(getText("label.preview.inline"))
				.setClick("bc.reportHistoryList.inline"));

		// 状态按钮组
		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(this.getStatuses(),
				"success", 0, getText("report.status.tips")));

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		AndCondition andCondition = new AndCondition();
		if (success != null && success.length() > 0) {
			andCondition.add(new EqualsCondition("a.success", Boolean
					.valueOf(success)));
		}

		if (taskId != null) {
			andCondition.add(new EqualsCondition("a.task_id", taskId));
		}

		if (my) {
			SystemContext context = (SystemContext) this.getContext();
			OrCondition orCondition = new OrCondition();
			orCondition.add(new EqualsCondition("a.author_id", context
					.getUser().getId()));
			// 保存的用户id键值集合
			List<Object> ids = new ArrayList<Object>();
			ids.add(context.getUser().getId());
			Long[] aids = context.getAttr(SystemContext.KEY_ANCESTORS);
			for (Long id : aids) {
				ids.add(id);
			}

			// 根据集合数量，生成的占位符字符串
			String qlStr = "";
			for (int i = 0; i < ids.size(); i++) {
				if (i + 1 != ids.size()) {
					qlStr += "?,";
				} else {
					qlStr += "?";
				}
			}

			orCondition.add(new QlCondition(
					"d.id in (select r.tid from  bc_report_template_actor r where r.aid in ("
							+ qlStr + "))", ids));
			andCondition.add(orCondition.setAddBracket(true));
		}

		if (andCondition.isEmpty())
			return null;

		return andCondition;
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/report/history/list.js";
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		if (success != null && success.length() > 0 && taskId != null) {
			json.put("success", success);
			json.put("taskId", taskId);
		} else if (success != null && success.length() > 0) {
			json.put("success", success);
		} else if (taskId != null) {
			json.put("taskId", taskId);
		}
		if (json.isEmpty())
			return null;
		return json;
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}

	public ReportHistoryService reportHistoryService;

	@Autowired
	public void setReportHistoryService(
			ReportHistoryService reportHistoryService) {
		this.reportHistoryService = reportHistoryService;
	}

	public JSONArray categorys;// 所属分类下拉列表信息

	@Override
	protected void initConditionsFrom() throws Exception {
		this.categorys = OptionItem.toLabelValues(this.reportHistoryService
				.findCategoryOption());
	}

	@Override
	public String getAdvanceSearchConditionsJspPath() {
		return BCConstants.NAMESPACE + "/report/history";
	}

	// ==高级搜索代码结束==

}
