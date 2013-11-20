package cn.bc.form.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.form.domain.Form;
import cn.bc.form.service.FieldService;
import cn.bc.form.service.FormService;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.Component;
import cn.bc.web.ui.html.Button;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;

/**
 * 自定义表单视图Action
 * 
 * @author hwx
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FormsAction extends ViewAction<Map<String, Object>> {

	private static final long serialVersionUID = 1L;
	public String status = "0"; // 表单状态
	private FormService formService;
	private FieldService fieldService;

	@Autowired
	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	@Autowired
	public void setFieldService(FieldService fieldService) {
		this.fieldService = fieldService;
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select f.id as id, f.pid as pid, f.uid_ as uid, f.type_ as type,");
		sql.append("f.code as code, f.status_ as status,f.subject as subject,");
		sql.append("f.tpl_ as tpl, a.actor_name as authorName, f.file_date as fileDate,");
		sql.append("m.actor_name as modifierName, f.modified_date as modifiedDate");
		sql.append(" from bc_form f");
		sql.append(" inner join bc_identity_actor_history a on a.id = f.author_id");
		sql.append(" left join bc_identity_actor_history m on m.id = f.modifier_id");
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
				map.put("uid", rs[i++]);
				map.put("type", rs[i++]);
				map.put("code", rs[i++]);
				map.put("status", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("tpl", rs[i++]);
				map.put("authorName", rs[i++]);
				map.put("fileDate", rs[i++]);
				map.put("modifierName", rs[i++]);
				map.put("modifiedDate", rs[i++]);

				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['type'] + ['subject']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "f.type_", "f.code" };
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("w.id", "id"));

		columns.add(new TextColumn4MapKey("f.status_", "status",
				getText("form.status"), 50).setSortable(true).setValueFormater(
				new KeyValueFormater(getStatuses())));

		columns.add(new TextColumn4MapKey("f.type_", "type",
				getText("form.type"), 40).setSortable(true));
		columns.add(new TextColumn4MapKey("f.pid", "pid", getText("form.pid"),
				80).setSortable(true));
		columns.add(new TextColumn4MapKey("f.uid_", "uid", getText("form.uid"),
				110).setSortable(true));
		columns.add(new TextColumn4MapKey("f.code", "code",
				getText("form.code"), 60).setSortable(true));
		columns.add(new TextColumn4MapKey("f.subject", "subject",
				getText("form.subject")).setSortable(true));
		columns.add(new TextColumn4MapKey("f.tpl_", "tpl", getText("form.tpl"),
				100).setSortable(true));
		columns.add(new TextColumn4MapKey("a.actor_name", "authorName",
				getText("form.authorName"), 150).setSortable(true));
		columns.add(new TextColumn4MapKey("f.file_date", "fileDate",
				getText("form.fileDate"), 150).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm:ss")));
		columns.add(new TextColumn4MapKey("m.actor_name", "modifierName",
				getText("form.modifierName"), 120).setSortable(true));
		columns.add(new TextColumn4MapKey(" f.modified_date", "modifiedDate",
				getText("form.modifiedDate"), 150).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm:ss")));
		return columns;
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(860).setMinWidth(450)
				.setHeight(400).setMinHeight(200);
	}

	@Override
	protected String getHtmlPageTitle() {

		return this.getText("form.title");

	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		// 编辑按钮
		tb.addButton(getToolbarButton4Edit());

		// 管理字段按钮
		tb.addButton(getToolbarButton4ManageField());

		// 管理审计日志按钮
		tb.addButton(getToolbarButton4ManageFieldLog());

		// 删除按钮
		tb.addButton(getToolbarButton4Delete());

		// 切换状态
		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(this.getStatuses(),
				"status", 1, getText("title.click2changeSearchStatus")));

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}

	// 编辑表单
	private Component getToolbarButton4Edit() {
		return new ToolbarButton().setIcon("ui-icon-wrench")
				.setText(getText("form.edit"))
				.setClick("bc.formMangeView.edit");
	}

	// 跳转到管理表单字段视图的按钮
	private Button getToolbarButton4ManageField() {
		return new ToolbarButton().setIcon("ui-icon-wrench")
				.setText(getText("form.manageField"))
				.setClick("bc.formMangeView.manageField");
	}

	// 跳转到管理表单审计日志视图的按钮
	private Button getToolbarButton4ManageFieldLog() {
		return new ToolbarButton().setIcon("ui-icon-wrench")
				.setText(getText("form.manageFieldLog"))
				.setClick("bc.formMangeView.manageFieldLog");
	}

	// 删除表单
	private Component getToolbarButton4Delete() {
		return new ToolbarButton().setIcon("ui-icon-wrench")
				.setText(getText("form.delete"))
				.setClick("bc.formMangeView.delete_");
	}

	@Override
	protected String getGridDblRowMethod() {
		return "bc.formMangeView.edit";
	}

	protected Condition getGridSpecalCondition() {
		/** 发布状态条件 */
		Condition statusCondition = ConditionUtils
				.toConditionByComma4IntegerValue(this.status, "f.status_");

		return statusCondition;
	}

	@Override
	protected String getFormActionName() {
		return "form";
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getModuleContextPath() + "/form/form/view.js" + ","
				+ this.getModuleContextPath() + "/form/customForm.js";
	}

	/**
	 * 获取自定义表单态值转换列表
	 * 
	 * @return
	 */
	private Map<String, String> getStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Form.STATUS_DRAFT),
				getText("form.status.draft"));
		statuses.put(String.valueOf(Form.STATUS_ENABLED),
				getText("form.status.enabled"));
		statuses.put("", getText("form.status.all"));
		return statuses;
	}

}
