package cn.bc.form.struts2;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.form.domain.Form;
import cn.bc.form.service.FormService;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public String formId;
	private FormService formService;

	@Autowired
	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select ff.id as id, ff.pid as pid, ff.name_ as name,");
		sql.append("ff.type_ as type, ff.value_ as value, ff.label_ as label,");
		sql.append("f.subject as formSubject,f.type_ as formType");
		sql.append(" from bc_form_field ff");
		sql.append(" inner join bc_form f on f.id = ff.pid");
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
				map.put("formSubject", rs[i++]);
				map.put("formType", rs[i++]);
				map.put("fieldTitle", "字段名称为" + rs[3]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("w.id", "id"));

		columns.add(new TextColumn4MapKey("f.subject", "formSubject",
				getText("field.formSubject"), 100).setSortable(true));

		columns.add(new TextColumn4MapKey("f.type_", "formType",
				getText("field.formType"), 100).setSortable(true));

		columns.add(new TextColumn4MapKey("ff.name_", "name",
				getText("field.name"), 100).setSortable(true));

		columns.add(new TextColumn4MapKey("ff.type_", "type",
				getText("field.type"), 80).setSortable(true));

		columns.add(new TextColumn4MapKey("ff.value_", "value",
				getText("field.value")).setSortable(true));

		columns.add(new TextColumn4MapKey("ff.label_", "label",
				getText("field.lable"), 110).setSortable(true));
		return columns;
	}

	@Override
	protected String getHtmlPageTitle() {
		if(formId != null && !formId.equals("")) {
			Form f = formService.load(new Long(formId));
			return f.getSubject() + "的字段管理";
		} else {
			return this.getText("field.title.all");
		}
	}

	@Override
	protected Condition getGridSpecalCondition() {
		/** 当前记录id条件 */
		Condition condition = ConditionUtils.toConditionByComma4IntegerValue(
				this.formId, "ff.pid");

		return condition;
	}

	@Override
    protected void extendGridExtrasData(JSONObject json) throws JSONException {
		if (this.formId != null && !formId.equals(""))
			json.put("formId", this.formId);
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
		return "['fieldTitle']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "f.subject", "f.type_", "ff.type_", "ff.name_",
				"ff.label_" };
	}

	@Override
	protected String getFormActionName() {
		return "fieldManage";
	}

}
