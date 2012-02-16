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
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 选项条目视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class OptionItemsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;

	@Override
	protected String getFormActionName() {
		return "optionItem";
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
		return new OrderCondition("g.order_", Direction.Asc).add("i.order_",
				Direction.Asc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select g.id as pid,g.value_ as pvalue,i.id as id,i.status_ as status,i.key_ as key");
		sql.append(",i.value_ as value,i.order_ as orderNo,i.icon as icon");
		sql.append(" from bc_option_item as i");
		sql.append(" inner join bc_option_group as g on g.id=i.pid");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("pid", rs[i++]);
				map.put("pvalue", rs[i++]);
				map.put("id", rs[i++]);
				map.put("status", rs[i++]);
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
		columns.add(new IdColumn4MapKey("i.id", "id"));
		columns.add(new TextColumn4MapKey("i.status_", "status",
				getText("label.status"), 60).setSortable(true)
				.setValueFormater(new KeyValueFormater(getEntityStatuses())));
		columns.add(new TextColumn4MapKey("i.order_", "orderNo",
				getText("label.order"), 70).setSortable(true));
		columns.add(new TextColumn4MapKey("g.value_", "pvalue",
				getText("option.optionGroup"), 120).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("i.key_", "key",
				getText("option.key")).setSortable(true).setUseTitleFromLabel(
				true));
		columns.add(new TextColumn4MapKey("i.value_", "value",
				getText("option.value"), 200).setSortable(true)
				.setUseTitleFromLabel(true));
		// columns.add(new TextColumn4MapKey("i.icon", "icon",
		// getText("option.icon"), 60).setSortable(true));

		return columns;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['value']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "g.key_",  "g.value_", "i.order_", "i.value_", "i.key_",
				"i.icon" };
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(650).setHeight(350)
				.setMinWidth(200).setMinHeight(200);
	}
}
