/**
 * 
 */
package cn.bc.template.web.struts2;

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
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.template.domain.Template;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 查看历史版本号Action
 * 
 * @author 
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ShowTemplateVersionAction extends
		AbstractSelectPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 车辆保单险种的状态，多个用逗号连接
	public String code;
	public String tid;

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：创建日期
		return new OrderCondition("t.order_", Direction.Asc);
	}
	
	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();
		
		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select t.id,t.order_ as order,t.code,t.type_ as type,t.desc_,t.path,t.subject");
		sql.append(",au.actor_name as uname,t.file_date,am.actor_name as mname");
		sql.append(",t.modified_date,t.inner_ as inner,t.status_ as status,t.version_ as version");
		sql.append(",t.category");
		sql.append(" from bc_template t");
		sql.append(" inner join bc_identity_actor_history au on au.id=t.author_id ");
		sql.append(" left join bc_identity_actor_history am on am.id=t.modifier_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("order", rs[i++]);
				map.put("code", rs[i++]);
				map.put("type", rs[i++]);
				map.put("desc_", rs[i++]);
				map.put("path", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("uname", rs[i++]);
				map.put("file_date", rs[i++]);
				map.put("mname", rs[i++]);
				map.put("modified_date", rs[i++]);
				map.put("inner", rs[i++]);
				map.put("status", rs[i++]);
				map.put("version", rs[i++]);
				map.put("category", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}       

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("t.id", "id"));
		columns.add(new TextColumn4MapKey("a.status_", "status",
				getText("template.status"), 40)
				.setSortable(true)
				.setValueFormater(new KeyValueFormater(this.getStatuses())));
		columns.add(new TextColumn4MapKey("t.order_", "order",
				getText("template.order"), 60).setSortable(true));
		columns.add(new TextColumn4MapKey("t.code", "code",
				getText("template.code"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.version_", "version",
				getText("template.version"), 100)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.category", "category",
				getText("template.category"), 100)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.type_", "type",
				getText("template.type"), 80)
				.setValueFormater(new KeyValueFormater(this.getTypes())));
		columns.add(new TextColumn4MapKey("t.subject", "subject",
				getText("template.tfsubject"), 200).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.path", "path",
				getText("template.tfpath")).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.desc_", "desc_",
				getText("template.desc"), 100).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.inner_", "inner",
				getText("template.inner"), 35).setSortable(true)
				.setValueFormater(new BooleanFormater()));
		columns.add(new TextColumn4MapKey("au.actor_name", "uname",
				getText("template.author"), 80));
		columns.add(new TextColumn4MapKey("t.file_date", "file_date",
				getText("template.fileDate"), 130)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("am.actor_name", "mname",
				getText("template.modifier"), 80));
		columns.add(new TextColumn4MapKey("t.modified_date", "modified_date",
				getText("template.modifiedDate"), 130)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));

		return columns;
	}
	/**
	 * 类型值转换:Excel|Word|文本文件|自定义文本|其它附件
	 * 
	 */
	private Map<String, String> getTypes() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put(String.valueOf(Template.TYPE_EXCEL),
				getText("template.type.excel"));
		map.put(String.valueOf(Template.TYPE_WORD),
				getText("template.type.word"));
		map.put(String.valueOf(Template.TYPE_TEXT),
				getText("template.type.text"));
		map.put(String.valueOf(Template.TYPE_CUSTOM),
				getText("template.type.costom"));
		map.put(String.valueOf(Template.TYPE_OTHER),
				getText("template.type.other"));
		return map;
	}
	
	//状态键值转换
	private Map<String,String> getStatuses(){
		Map<String,String> statuses=new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED)
				, getText("template.status.normal"));
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED)
				, getText("template.status.disabled"));
		statuses.put(""
				, getText("template.status.all"));
		return statuses;
	}
	
	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "t.code", "am.actor_name"
				, "t.path", "t.subject","t.version_","t.category" };
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("template.code")+code+this.getText("template.history.version");
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption()
				.setMinHeight(200).setMinWidth(300).setWidth(610).setHeight(340).setModal(false);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected HtmlPage buildHtmlPage() {
		return super.buildHtmlPage().setNamespace(
				this.getHtmlPageNamespace() + "/showTemplateVersion");
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/template/show.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if(code != null && code.length() > 0){
			statusCondition=new EqualsCondition("t.code",code);
		}
		return statusCondition;
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		if(tid!=null&&tid.length()>0){
			json.put("tid", tid);
		}
		if(code!=null&&code.length()>0){
			json.put("code", code);
		}
		return json;
	}

	@Override
	protected String getClickOkMethod() {
		return "bc.templateShowDialog.clickOk";
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BCConstants.NAMESPACE;
	}
	
	@Override
	protected String getFormActionName() {
		return "template";
	}

}
