/**
 * 
 */
package cn.bc.option.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 选项分组视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class OptionGroupsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;

	@Override
	protected String getFormActionName() {
		return "optionGroup";
	}

	@Override
	public boolean isReadonly() {
		// 选项管理或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.option"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		return new OrderCondition("g.order_", Direction.Asc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select g.id as id,g.key_ as key,g.value_ as value,g.order_ as orderNo,g.icon as icon");
		sql.append(" from bc_option_group as g");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("key", rs[i++]);
				map.put("value", rs[i++]);
				map.put("orderNo", rs[i++]);
				map.put("icon", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("g.id", "id"));
		columns.add(new TextColumn4MapKey("g.order_", "orderNo",
				getText("label.order"), 80).setSortable(true).setDir(
				Direction.Asc));
		columns.add(new TextColumn4MapKey("g.key_", "key",
				getText("option.key")).setSortable(true));
		columns.add(new TextColumn4MapKey("g.value_", "value",
				getText("option.value"), 200).setSortable(true));
		return columns;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['value']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "g.key", "g.value", "g.order_", "g.icon" };
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(600).setHeight(350)
				.setMinWidth(200).setMinHeight(200);
	}
}
