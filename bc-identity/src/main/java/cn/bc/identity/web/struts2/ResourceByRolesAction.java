package cn.bc.identity.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Resource;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ResourceByRolesAction extends ViewAction<Map<String, Object>> {

	public Long roleId;
	
	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select s.id,s.type_,s.name,p.name pname, r.id");
		sql.append(" from bc_identity_resource as s");
		sql.append(" left join bc_identity_resource as p on p.id=s.belong");
		sql.append(" inner join bc_identity_role_resource as rs on rs.sid=s.id");
		sql.append(" inner join bc_identity_role as r on r.id=rs.rid");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {

			@Override
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
		columns.add(new IdColumn4MapKey("s.id", "id"));
		columns.add(new TextColumn4MapKey("s.type_", "type",
				getText("privilege.type"), 80)
				.setSortable(true)
				.setValueFormater(new EntityStatusFormater(getModuleTypes())));
		columns.add(new TextColumn4MapKey("s.name", "name",
				getText("label.name"), 100).setSortable(true));
		columns.add(new TextColumn4MapKey("p.name", "pname",
				getText("privilege.belong"), 90).setSortable(true));
		return columns;
	}

	/**
	 * 获取资源类型值转换列表
	 * 
	 * @return
	 */
	private Map<String, String> getModuleTypes() {
		Map<String, String> types = new HashMap<String, String>();
		types = new LinkedHashMap<String, String>();
		types.put(String.valueOf(Resource.TYPE_FOLDER),
				getText("resource.type.folder"));
		types.put(String.valueOf(Resource.TYPE_INNER_LINK),
				getText("resource.type.innerLink"));
		types.put(String.valueOf(Resource.TYPE_OUTER_LINK),
				getText("resource.type.outerLink"));
		return types;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] {  "s.name", "p.name" };
	}

	

	@Override
	protected String getFormActionName() {
		return "resourceByRole";
	}
	
	@Override
	protected Condition getGridSpecalCondition() {
		Condition roleCondition = null;

		if (roleId != null) {
			roleCondition = new EqualsCondition("r.id", roleId);
		}

		return ConditionUtils.mix2AndCondition(roleCondition);
	}
	
	@Override
	protected void extendGridExtrasData(JSONObject json) throws JSONException {
		super.extendGridExtrasData(json);

		if (roleId != null) {
			json.put("roleId", roleId);
		}
	}
	
	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		//添加
		tb.addButton(new ToolbarButton().setText(getText("privilege.add"))
				.setClick("bc.privilege.addResource"));
		// 删除
		tb.addButton(new ToolbarButton().setText(getText("privilege.delete"))
				.setClick("bc.privilege.deleteResource"));

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());
		return tb;
	}
	
	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc/identity/role/privilege.js";
	}

	@Override
	protected String getEditUrl() {
		return this.getModuleContextPath() + "/resource/edit";
	}
	
}
