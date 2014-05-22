package cn.bc.form.struts2;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.form.domain.Form;
import cn.bc.form.service.FormService;
import cn.bc.web.formater.CalendarFormater;
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
 * 自定义表单审计日志视图Action
 * 
 * @author hwx
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FieldLogsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = -3905205931530124808L;
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
		sql.append("select ffg.id as id, ffg.value_ as value, ffg.updator as updator,ffg.update_time as updateTime,");
		sql.append("ffg.batch_no as batchNo,a.actor_name as updatorName,ff.name_ as fieldName,");
		sql.append("f.subject as formSubject,f.type_ as formType");
		sql.append(" from bc_form_field_log ffg");
		sql.append(" inner join bc_identity_actor_history a on a.id = ffg.updator");
		sql.append(" inner join bc_form_field ff on ff.id = ffg.pid");
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
				map.put("value", rs[i++]);
				map.put("updator", rs[i++]);
				map.put("updateTime", rs[i++]);
				map.put("batchNo", rs[i++]);
				map.put("updatorName", rs[i++]);
				map.put("fieldName", rs[i++]);
				map.put("formSubject", rs[i++]);
				map.put("formType", rs[i++]);
				return map;
			}
		});
		return sqlObject;
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
	protected OrderCondition getGridDefaultOrderCondition() {
		return new OrderCondition("ffg.update_time", Direction.Desc);
				
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("ffg.id", "id"));
		
		columns.add(new TextColumn4MapKey("f.subject", "formSubject",
				getText("fieldLog.formSubject"), 100).setSortable(true));
		
		columns.add(new TextColumn4MapKey("f.type_", "formType",
				getText("fieldLog.formType"), 100).setSortable(true));
		
		columns.add(new TextColumn4MapKey("ff.name_", "fieldName",
				getText("fieldLog.fieldName"), 110).setSortable(true));

		columns.add(new TextColumn4MapKey("ffg.value_", "value",
				getText("fieldLog.value")).setSortable(true));

		columns.add(new TextColumn4MapKey("ffg.update_time", "updateTime",
				getText("fieldLog.updateTime"),150).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm:ss")));

		columns.add(new TextColumn4MapKey("ffg.batch_no", "batchNo",
				getText("fieldLog.batchNo"), 150).setSortable(true));

		columns.add(new TextColumn4MapKey("a.actor_name", "updatorName",
				getText("fieldLog.updatorName"), 110).setSortable(true));

		return columns;
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(450)
				.setHeight(400).setMinHeight(200);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return null;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "f.subject", "f.type_","ffg.batch_no", "ff.name_", "a.actor_name" };
	}

	@Override
	protected String getFormActionName() {
		return "fieldLogManage";
	}

	@Override
	protected String getHtmlPageTitle() {
		if(formId != null && !formId.equals("")) {
			Form f = formService.load(new Long(formId));
			return f.getSubject() + "的审计日志";
		} else {
			return this.getText("field.title.all");
		}
	}
	
	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		// 占位按钮
		tb.addButton(Toolbar.getDefaultEmptyToolbarButton());

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}
	
	@Override
	protected String getGridDblRowMethod() {
		return null;
	}
}
