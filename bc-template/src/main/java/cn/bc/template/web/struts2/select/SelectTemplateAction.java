/**
 * 
 */
package cn.bc.template.web.struts2.select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 模板接口Action
 * 
 * @author lbj
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectTemplateAction extends
		AbstractSelectPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); //状态
	public String category;//所属分类

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：排序号
		return new OrderCondition("t.order_");
	}
	
	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select t.id,t.order_ as orderNo,t.code,a.name as type,t.desc_,t.path,t.subject");
		sql.append(",au.actor_name as uname,t.file_date,am.actor_name as mname");
		sql.append(",t.modified_date,t.inner_ as inner,t.status_ as status,t.version_ as version");
		sql.append(",t.category,a.code as typeCode,t.formated");
		sql.append(" from bc_template t");
		sql.append(" inner join bc_template_type a on a.id=t.type_id ");
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
				map.put("orderNo", rs[i++]);
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
				map.put("typeCode", rs[i++]);
				map.put("formated", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}       

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("t.id", "id"));
		columns.add(new TextColumn4MapKey("t.order_", "orderNo",
				getText("template.order"), 60).setSortable(true));
		columns.add(new TextColumn4MapKey("a.name", "type",
				getText("template.type"), 150).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.subject", "subject",
				getText("template.tfsubject"), 200).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.code", "code",
				getText("template.code"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.version_", "version",
				getText("template.version"), 100).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.path", "path",
				getText("template.tfpath")).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.formated", "formated",
				getText("template.file.formated"), 80).setSortable(true)
				.setValueFormater(new BooleanFormater()));
		columns.add(new TextColumn4MapKey("t.size_", "size",
				getText("template.file.size"),110).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.desc_", "desc_",
				getText("template.desc"), 100).setUseTitleFromLabel(true));
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
		columns.add(new HiddenColumn4MapKey("typeCode", "typeCode"));
		return columns;
	}
	
	@Override
	protected String[] getGridSearchFields() {
		return new String[]{"t.code", "am.actor_name", "t.path", "t.subject",
				"t.version_", "t.category","a.name"};
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("template.title.selectTemplate");
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(600).setHeight(480);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected HtmlPage buildHtmlPage() {
		return super.buildHtmlPage().setNamespace(
				this.getHtmlPageNamespace() + "/selectTemplate");
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/template/select.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		AndCondition andCondition=new AndCondition();
		if(status!=null&&status.length()>0){
			andCondition.add(new EqualsCondition("t.status_", Integer.parseInt(status)));
		}
		
		if(category!=null&&category.length()>0){
			if(category.indexOf(",")==-1){
				andCondition.add(new EqualsCondition("t.category", category));
			}else{
				andCondition.add(new InCondition("t.category", category.split(",")));
			}
		}
		return andCondition;
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		if(status!=null&&status.length()>0){
			json.put("status", status);
		}
		if(category!=null&&category.length()>0){
			json.put("category", category);
		}
		return json;
	}

	@Override
	protected String getClickOkMethod() {
		return "bc.templateSelectDialog.clickOk";
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + BCConstants.NAMESPACE;
	}
}
