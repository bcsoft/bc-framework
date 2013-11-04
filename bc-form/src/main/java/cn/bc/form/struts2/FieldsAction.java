package cn.bc.form.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 自定义表单字段视图Action
 * 
 * @author hwx
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FieldsAction extends ViewAction<Map<String, Object>> {

	private static final long serialVersionUID = 1L;
	public String id;

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select ff.id as id, ff.pid as pid, ff.name_ as name,");
		sql.append("ff.type_ as type, ff.value_ as value, ff.label_ as label");
		sql.append(" from bc_form_field ff");
		sqlObject.setSql(sql.toString());
		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("pid", rs[i++]);
				map.put("name", rs[i++]);
				map.put("type", rs[i++]);
				map.put("value", rs[i++]);
				map.put("label", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("w.id", "id"));

		columns.add(new TextColumn4MapKey("ff.pid", "pid",
				getText("field.pid"), 40).setSortable(true));

		columns.add(new TextColumn4MapKey("ff.name_", "name",
				getText("field.name"), 40).setSortable(true));

		columns.add(new TextColumn4MapKey("ff.type_", "type",
				getText("field.type"), 40).setSortable(true));

		columns.add(new TextColumn4MapKey("ff.value_", "value",
				getText("field.value"), 110).setSortable(true));

		columns.add(new TextColumn4MapKey("ff.label_", "label",
				getText("field.lable"), 110).setSortable(true));
		return columns;
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("form.title");
	}

	protected Condition getGridSpecalCondition() {
		/** 发布状态条件 */
		Condition statusCondition = ConditionUtils
				.toConditionByComma4IntegerValue(this.id, "ff.pid");

		return statusCondition;
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(860).setMinWidth(450)
				.setHeight(400).setMinHeight(200);
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		// 编辑按钮
		tb.addButton(getDefaultEditToolbarButton());

		// 删除按钮
		tb.addButton(Toolbar
				.getDefaultDeleteToolbarButton(getText("label.delete")));

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "ff.type_", "ff.name_","ff.label_" };
	}

	@Override
	protected String getFormActionName() {
		return "fieldManage";
	}

	@Override
	protected String getHtmlPageJs() {
		return "";
	}

}
