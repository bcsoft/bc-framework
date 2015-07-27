package cn.bc.identity.web.struts2.select;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Resource;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectResourceAction extends AbstractSelectPageAction<Map<String, Object>> {
	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		sqlObject.setSql("select a.id as id, a.type_ as type, a.name as name, b.name as pname"
				+ " from bc_identity_resource a"
				+ " left join bc_identity_resource b on a.belong = b.id");

		// 数据映射
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("type", rs[i++]);
				map.put("name", rs[i++]);
				map.put("pname", rs[i]);
				return map;
			}
		});
		return sqlObject;
	}


	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		return new OrderCondition("a.order_", Direction.Asc);
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<>();
		columns.add(new IdColumn4MapKey("id", "id"));
		columns.add(new TextColumn4MapKey("a.type_", "type", getText("resource.type"), 40)
						.setValueFormater(new KeyValueFormater(getTypes()))
		);
		columns.add(new TextColumn4MapKey("a.name", "name", getText("resource.name"), 100).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.name", "pname", getText("resource.belong"), 100).setUseTitleFromLabel(true));
		return columns;
	}


	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[]{"a.name", "b.name"};
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("resource.title.select");
	}

	private Map<String, String> getTypes() {
		Map<String, String> types = new LinkedHashMap<>();
		types.put(String.valueOf(Resource.TYPE_FOLDER), getText("resource.type.folder"));
		types.put(String.valueOf(Resource.TYPE_INNER_LINK), getText("resource.type.innerLink"));
		types.put(String.valueOf(Resource.TYPE_OUTER_LINK), getText("resource.type.outerLink"));
		types.put(String.valueOf(Resource.TYPE_HTML), getText("resource.type.html"));
		return types;
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(400).setHeight(450);
	}

	@Override
	protected void addJsCss(List<String> container) {
		container.add(this.getActionNamespace() + "/select.js");
	}

	@Override
	protected String getClickOkMethod() {
		return "onOk";
	}

	@Override
	protected boolean canExport() {
		return false;
	}

	@Override
	protected boolean isQuirksMode() {
		return false;
	}
}