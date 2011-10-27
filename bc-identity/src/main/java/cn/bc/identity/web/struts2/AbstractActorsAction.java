/**
 * 
 */
package cn.bc.identity.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.Entity;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 通用的Actor视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public abstract class AbstractActorsAction extends
		ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(Entity.STATUS_ENABLED); // Actor的状态，多个用逗号连接

	@Override
	public boolean isReadonly() {
		// 组织架构管理或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.actor"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：状态|排序号
		return new OrderCondition("a.status_", Direction.Asc).add("a.order_",
				Direction.Asc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id,a.type_,a.status_,a.name,a.code,a.order_,a.phone,a.pname");
		sql.append(" from bc_identity_actor a");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("type", rs[i++]);
				map.put("status", rs[i++]);
				map.put("name", rs[i++]);
				map.put("code", rs[i++]);
				map.put("orderNo", rs[i++]);
				map.put("phone", rs[i++]);
				map.put("pname", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("a.id", "id"));
		columns.add(new TextColumn4MapKey("a.status_", "status",
				getText("actor.status"), 60).setSortable(true)
				.setValueFormater(new KeyValueFormater(getBCStatuses())));
		columns.add(new TextColumn4MapKey("a.pname", "pname",
				getText("actor.pname")).setSortable(true).setUseTitleFromLabel(
				true));
		columns.add(new TextColumn4MapKey("a.name", "name",
				getText("actor.name"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.code", "code",
				getText("actor.code"), 120).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.order_", "orderNo",
				getText("actor.order"), 100).setSortable(true)
				.setDir(Direction.Asc).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.phone", "phone",
				getText("actor.phone"), 100).setUseTitleFromLabel(true));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "a.name", "a.py", "a.code", "a.pname", "a.order_" };
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(700).setMinWidth(450)
				.setHeight(400).setMinHeight(200);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		return super.getHtmlPageToolbar()
				.addButton(
						Toolbar.getDefaultToolbarRadioGroup(
								this.getBCStatuses(), "status", 0,
								getText("title.click2changeSearchStatus")));
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = ConditionUtils
				.toConditionByComma4IntegerValue(this.status, "a.status_");

		// 类型条件
		Condition typeCondition = ConditionUtils
				.toConditionByComma4IntegerValue(this.getActorType(), "a.type_");

		// 合并条件
		return ConditionUtils.mix2AndCondition(statusCondition, typeCondition);
	}

	protected String getActorType() {
		return null;
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		super.extendGridExtrasData(json);
		if (this.status != null && this.status.trim().length() > 0) {
			json.put("status", status);
		}
	}
}
