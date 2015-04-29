package cn.bc.identity.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.PrivilegeService;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class PrivilegeByRolesAction extends ViewAction<Map<String, Object>> {
	@Autowired
	private PrivilegeService privilegeService;
	public Long roleId;

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		StringBuffer sql = new StringBuffer();
		sql.append("select a.id , a.type_, a.name, a.pname, a.order_");
		sql.append(" from bc_identity_role r");
		sql.append(" inner join bc_identity_role_actor ra on r.id = ra.rid");
		sql.append(" inner join bc_identity_actor a on a.id = ra.aid");

		sqlObject.setSql(sql.toString());

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
	protected OrderCondition getGridDefaultOrderCondition() {
		return new OrderCondition("a.type_", Direction.Asc).add("a.order_", Direction.Asc);
	}
	
	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("a.id", "id"));
		columns.add(new TextColumn4MapKey("a.type_", "type",
				getText("privilege.type"), 60)
				.setSortable(true)
				.setValueFormater(new EntityStatusFormater(getActorStatuses3())));
		columns.add(new TextColumn4MapKey("a.name", "name",
				getText("label.name"),160).setSortable(true));
		columns.add(new TextColumn4MapKey("a.pname", "pname",
				getText("privilege.parent")).setSortable(true));
		return columns;
	}

	/**
	 * 0-未定义1-单位 2-部门 3-岗位 4-用户
	 * 
	 * @return
	 */
	private Map<String, String> getActorStatuses3() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Actor.TYPE_UNDEFINED),
				getText("privilege.undefined"));
		statuses.put(String.valueOf(Actor.TYPE_UNIT), getText("privilege.unit"));
		statuses.put(String.valueOf(Actor.TYPE_DEPARTMENT),
				getText("privilege.department"));
		statuses.put(String.valueOf(Actor.TYPE_GROUP),
				getText("privilege.group"));
		statuses.put(String.valueOf(Actor.TYPE_USER), getText("privilege.user"));
		return statuses;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] {  "a.name", "a.pname" };
	}

	@Override
	protected String getFormActionName() {
		return "privilegeByRole";
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
		//添加单位或部门
		tb.addButton(new ToolbarButton().setText(
				getText("privilege.addUnitOrDep")).setClick(
				"bc.privilege.addUnitOrDep"));
		//添加岗位
		tb.addButton(new ToolbarButton().setText(getText("privilege.addGroup"))
				.setClick("bc.privilege.addGroup"));
		//添加用户
		tb.addButton(new ToolbarButton().setText(getText("privilege.addUser"))
				.setClick("bc.privilege.addUser"));
		// 删除
		tb.addButton(new ToolbarButton().setText(getText("privilege.delete"))
				.setClick("bc.privilege.deleteActor"));

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());
		return tb;
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc/identity/role/privilege.js";
	}
	
	@Override
	protected String getGridDblRowMethod() {
		return "bc.privilege.LookActor";
	}
	
	@Override
	protected String getDefaultExportFileName() {
		return "导出"+privilegeService.getRoleNameById(roleId)+"的权限分配";
	}
}
