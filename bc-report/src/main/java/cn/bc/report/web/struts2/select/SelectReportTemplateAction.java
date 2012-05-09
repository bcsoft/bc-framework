package cn.bc.report.web.struts2.select;

import java.util.ArrayList;
import java.util.HashMap;
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
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 选择报表模板Action
 * 
 * @author lbj
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectReportTemplateAction extends
		AbstractSelectPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;

	public String status = String.valueOf(BCConstants.STATUS_ENABLED);

	@Override
	protected String getClickOkMethod() {
		return "bc.selectReportTemplate.clickOk";
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id,a.status_ as status,a.order_ as orderNo,a.category,a.code,a.name,a.desc_ as desc");
		sql.append(",b.actor_name as uname,a.file_date,c.actor_name as mname");
		sql.append(",a.modified_date");
		sql.append(" from bc_report_template a");
		sql.append(" inner join bc_identity_actor_history b on b.id=a.author_id");
		sql.append(" left join bc_identity_actor_history c on c.id=a.modifier_id");
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
				map.put("code", rs[i++]);
				map.put("name", rs[i++]);
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
		columns.add(new TextColumn4MapKey("a.order_", "orderNo",
				getText("reportTemplate.order"), 60).setSortable(true));
		columns.add(new TextColumn4MapKey("a.category", "category",
				getText("reportTemplate.category"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.name", "name",
				getText("reportTemplate.name"), 200).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.code", "code",
				getText("reportTemplate.code"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.desc_", "desc_",
				getText("reportTemplate.desc")).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.actor_name", "uname",
				getText("reportTemplate.author"), 80));
		columns.add(new TextColumn4MapKey("a.file_date", "file_date",
				getText("reportTemplate.fileDate"), 130)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("c.actor_name", "mname",
				getText("reportTemplate.modifier"), 80));
		columns.add(new TextColumn4MapKey("a.modified_date", "modified_date",
				getText("reportTemplate.modifiedDate"), 130)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		return columns;
	}


	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(500).setHeight(450);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "a.code", "b.actor_name", "a.name","a.category"};
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BCConstants.NAMESPACE;
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认的排序方法
		return new OrderCondition("a.order_");
	}

	@Override
	protected HtmlPage buildHtmlPage() {
		return super.buildHtmlPage().setNamespace(
				this.getHtmlPageNamespace() + "/selectReportTemplate");
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/report/template/select.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		return new EqualsCondition("a.status_",Integer.parseInt(status));
	}

	@Override
	protected Json getGridExtrasData() {
		if (this.status == null || this.status.length() == 0) {
			return null;
		} else {
			Json json = new Json();
			json.put("status", status);
			return json;
		}
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("selectReportTemplate.title");
	}

}
