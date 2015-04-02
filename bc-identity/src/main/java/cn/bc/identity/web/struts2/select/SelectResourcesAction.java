package cn.bc.identity.web.struts2.select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Resource;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectResourcesAction extends 
			AbstractSelectPageAction<Map<String, Object>>{

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String,Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id as id,a.type_ as type,a.name as name,a.pname as pname");
		sql.append(" from  bc_identity_resource a");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射
		sqlObject.setRowMapper(new RowMapper<Map<String,Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("type", rs[i++]);
				map.put("name", rs[i++]);
				map.put("pname", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("id", "id"));
		columns.add(new TextColumn4MapKey("a.type_", "type",
				getText("resource.type"), 40)
				.setValueFormater(new EntityStatusFormater(getTypes()))
			);
				
		columns.add(new TextColumn4MapKey("a.name", "name",
				getText("resource.name"),100)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.pname", "pname",
				getText("resource.belong"),100)
				.setUseTitleFromLabel(true));

		return columns;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "a.name"};
	}
	
	@Override
	protected String getHtmlPageTitle() {
		return this.getText("resource.title.select");
	}
	
	private Map<String, String> getTypes() {
		Map<String, String> mtype = new LinkedHashMap<String,String>();
		mtype.put(String.valueOf(Resource.TYPE_FOLDER), getText("resource.type.folder"));
		mtype.put(String.valueOf(Resource.TYPE_INNER_LINK), getText("resource.type.innerLink"));
		mtype.put(String.valueOf(Resource.TYPE_OUTER_LINK), getText("resource.type.outerLink"));
		mtype.put(String.valueOf(Resource.TYPE_HTML), getText("resource.type.html"));
		return mtype;
	}
	
	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(400).setHeight(450);
	}
	
	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + "/bc";
	}

	@Override
	protected HtmlPage buildHtmlPage() {
		return super.buildHtmlPage().setNamespace(
				this.getHtmlPageNamespace() + "/selectResources");
	}
	
	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/identity/resource/select.js";
	}
	
	@Override
	protected String getClickOkMethod() {
		return "bc.resourceSelectDialog.clickOK";
	}
}
