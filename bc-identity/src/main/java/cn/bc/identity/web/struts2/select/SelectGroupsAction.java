/**
 * 
 */
package cn.bc.identity.web.struts2.select;

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
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Actor;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 选择岗位信息
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectGroupsAction extends
		AbstractSelectPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public boolean history = false;// 是否选择ActorHistory信息默认不选
	public String status = String.valueOf(BCConstants.STATUS_ENABLED) + ","
			+ String.valueOf(BCConstants.STATUS_DISABLED); // 用户的状态，多个用逗号连接

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|创建时间
		return new OrderCondition("a.status_", Direction.Asc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		// 查看历史的信息
		if (this.history) {
			sql.append("select h.id,a.status_,a.type_,a.code,case when a.pname is null then a.name else");
			sql.append(" a.pname||'/'||a.name end as name,a.name cname,a.pcode,a.pname");
			sql.append(" from bc_identity_actor_history h");
			sql.append(" inner join bc_identity_actor a on a.id=h.actor_id");
		} else {
			sql.append("select a.id,a.status_,a.type_,a.code,case when a.pname is null then a.name else");
			sql.append(" a.pname||'/'||a.name end as name,a.name cname,a.pcode,a.pname");
			sql.append(" from BC_IDENTITY_ACTOR a");
		}
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
				map.put("type_", rs[i++]);
				map.put("code", rs[i++]);
				map.put("name", rs[i++]);
				map.put("cname", rs[i++]);
				map.put("pname", rs[i++]);
				return map;

			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		if (history) {
			columns.add(new IdColumn4MapKey("h.id", "id"));
		} else {
			columns.add(new IdColumn4MapKey("a.id", "id"));
		}
		columns.add(new TextColumn4MapKey("a.status_", "status",
				getText("actor.status"), 2).setSortable(true).setValueFormater(
				new EntityStatusFormater(getBCStatuses())));
		columns.add(new TextColumn4MapKey("a.name", "name", getText("group"),
				40).setSortable(true).setUseTitleFromLabel(true));
		columns.add(new HiddenColumn4MapKey("type", "type_"));
		columns.add(new HiddenColumn4MapKey("code", "code"));
		columns.add(new HiddenColumn4MapKey("cname", "cname"));
		columns.add(new HiddenColumn4MapKey("pname", "pname"));
		return columns;
	}

	/**
	 * 状态值转换列表：正常|禁用|删除|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getBCStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("bc.status.enabled"));
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
				getText("bc.status.disabled"));
		statuses.put(String.valueOf(BCConstants.STATUS_DELETED),
				getText("bc.status.deleted"));
		statuses.put("", getText("bc.status.all"));
		return statuses;
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("group.title.select");
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "a.name", "a.code" };
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(450).setHeight(450);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected HtmlPage buildHtmlPage() {
		return super.buildHtmlPage().setNamespace(
				this.getHtmlPageNamespace() + "/selectGroups");
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/identity/group/select.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		Condition statusCondition = null;
		Condition typeCondition = null;
		Condition historyCondition = null;
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("a.status_", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("a.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		}
		// 是否查看历史信息
		if (history) {
			historyCondition = new EqualsCondition("h.current", true);
		}

		typeCondition = new EqualsCondition("a.type_", Actor.TYPE_GROUP);
		return ConditionUtils.mix2AndCondition(statusCondition, typeCondition,
				historyCondition);
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		// 状态条件
		if (this.status != null || this.status.length() != 0) {
			json.put("status", status);
		}
		// 历史条件
		json.put("history", history);

		return json.isEmpty() ? null : json;
	}

	@Override
	protected String getClickOkMethod() {
		return "bc.groupSelectDialog.clickOk";
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + "/bc";
	}
}
