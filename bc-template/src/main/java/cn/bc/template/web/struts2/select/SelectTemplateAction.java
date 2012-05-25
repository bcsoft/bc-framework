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
import cn.bc.web.formater.FileSizeFormater;
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
		sql.append("select t.id,t.code,a.name as type,t.subject,t.path");
		sql.append(",t.version_ as version,t.category,a.code as typeCode,t.formatted,t.size_ as size,t.desc_");
		sql.append(" from bc_template t");
		sql.append(" inner join bc_template_type a on a.id=t.type_id ");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("code", rs[i++]);
				map.put("type", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("path", rs[i++]);
				map.put("version", rs[i++]);
				map.put("category", rs[i++]);
				map.put("typeCode", rs[i++]);
				map.put("formatted", rs[i++]);
				map.put("size", rs[i++]);
				map.put("desc_", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}       

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("t.id", "id"));
		columns.add(new TextColumn4MapKey("t.subject", "subject",
				getText("template.tfsubject"), 200).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.desc_", "desc_",
				getText("template.desc")).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.version_", "version",
				getText("template.version"), 200).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.code", "code",
				getText("template.code"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.name", "type",
				getText("template.type"), 150).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.category", "category",
				getText("template.category"), 150).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.size_", "size",
				getText("template.file.size"),110).setUseTitleFromLabel(true)
				.setValueFormater(new FileSizeFormater()));
		columns.add(new TextColumn4MapKey("t.formatted", "formatted",
				getText("template.file.formatted"), 80).setSortable(true)
				.setValueFormater(new BooleanFormater()));
		columns.add(new HiddenColumn4MapKey("typeCode", "typeCode"));
		columns.add(new HiddenColumn4MapKey("path", "path"));
		return columns;
	}
	
	@Override
	protected String[] getGridSearchFields() {
		return new String[]{"t.code", "t.subject","t.version_", "t.category","a.name"};
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
