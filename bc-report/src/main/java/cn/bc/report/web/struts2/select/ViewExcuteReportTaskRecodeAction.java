package cn.bc.report.web.struts2.select;

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
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.json.Json;

/**
 * 查看执行记录Action
 * 
 * @author lbj
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ViewExcuteReportTaskRecodeAction extends
		AbstractSelectPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String success = String.valueOf(true);
	public Long taskId;

	@Override
	protected String getClickOkMethod() {
		return "bc.viewExcuteReportTaskRecodeList.clickOk";
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id,a.success,a.file_date,a.category,a.subject,a.path,b.actor_name as uname,a.task_id");
		sql.append(" from bc_report_history a");
		sql.append(" inner join bc_identity_actor_history b on b.id=a.author_id");
		sqlObject.setSql(sql.toString());

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
				getText("reportHistory.subject"), 200).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.path", "path",
				getText("reportHistory.path"), 200).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.actor_name", "uname",
				getText("report.author")));
		return columns;
	}


	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setMinWidth(500)
				.setMinHeight(300).setWidth(600).setHeight(450).setModal(false);
	}
	
	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		// 下载
		tb.addButton(new ToolbarButton()
				.setIcon("ui-icon-arrowthickstop-1-s")
				.setText(getText("label.download"))
				.setClick("bc.viewExcuteReportTaskRecodeList.download"));
		// 在线预览
		tb.addButton(new ToolbarButton().setIcon("ui-icon-lightbulb")
				.setText(getText("label.preview.inline"))
				.setClick("bc.viewExcuteReportTaskRecodeList.inline"));
		
		//状态按钮组
		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
				this.getStatuses(), "success", 0, getText("report.status.tips")));

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}
	
	//状态键值转换
	private Map<String,String> getStatuses(){
		Map<String,String> statuses=new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(true)
				, getText("reportHistory.status.success"));
		statuses.put(String.valueOf(false)
				, getText("reportHistory.status.lost"));
		statuses.put(""
				, getText("report.status.all"));
		return statuses;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "a.subject", "b.actor_name","a.category" };
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BCConstants.NAMESPACE;
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认的排序方法
		return new OrderCondition("a.file_date",Direction.Desc);
	}

	@Override
	protected HtmlPage buildHtmlPage() {
		return super.buildHtmlPage().setNamespace(
				this.getHtmlPageNamespace() + "/viewExcuteReportTaskRecode");
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/report/history/view/select.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if(success != null && success.length() > 0&&taskId!=null){
			statusCondition = new AndCondition(new EqualsCondition("a.success",Boolean.valueOf(success)),
					new EqualsCondition("a.task_id",taskId));
		}else if(success != null && success.length() > 0){
			statusCondition = new EqualsCondition("a.success",Boolean.valueOf(success));
		}else if(taskId!=null){
			statusCondition =new EqualsCondition("a.task_id",taskId);
		}
		return statusCondition;
	}
	
	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		if(success != null && success.length() > 0&&taskId!=null){
			json.put("success", success);
			json.put("taskId", taskId);
		}else if(success != null && success.length() > 0){
			json.put("success", success);
		}else if(taskId!=null){
			json.put("taskId", taskId);		
		}
		if(json.isEmpty()) return null;
		return json;
	}
	
	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}

	@Override
	protected String getAdvanceSearchConditionsActionPath() {
		return this.getHtmlPageNamespace()+"/report/history/view/conditions.jsp";
	}
	// ==高级搜索代码结束==

}
