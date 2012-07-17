package cn.bc.template.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.template.service.TemplateService;
import cn.bc.template.service.TemplateTypeService;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.FileSizeFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.html.toolbar.ToolbarMenuButton;
import cn.bc.web.ui.json.Json;

/**
 * 模板视图Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class TemplatesAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED);
	public String code;

	@Override
	public boolean isReadonly() {
		// 模板管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：模板管理员
		return !context.hasAnyRole(getText("key.role.bc.template"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("t.order_");
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select t.id,t.uid_,t.order_ as orderNo,t.code,a.name as type,t.desc_,t.path,t.subject");
		sql.append(",au.actor_name as uname,t.file_date,am.actor_name as mname");
		sql.append(",t.modified_date,t.inner_ as inner,t.status_ as status,t.version_ as version");
		sql.append(",t.category,a.code as typeCode,t.size_ as size,t.formatted");
		sql.append(" from bc_template t");
		sql.append(" inner join bc_template_type a on a.id=t.type_id ");
		sql.append(" inner join bc_identity_actor_history au on au.id=t.author_id ");
		sql.append(" left join bc_identity_actor_history am on am.id=t.modifier_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("uid", rs[i++]);
				map.put("orderNo", rs[i++]);
				map.put("code", rs[i++]);
				map.put("type", rs[i++]);
				map.put("desc_", rs[i++]);
				map.put("path", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("uname", rs[i++]);
				map.put("file_date", rs[i++]);
				map.put("mname", rs[i++]);
				map.put("modified_date", rs[i++]);
				map.put("inner", rs[i++]);
				map.put("status", rs[i++]);
				map.put("version", rs[i++]);
				map.put("category", rs[i++]);
				map.put("typeCode", rs[i++]);
				map.put("size", rs[i++]);
				map.put("formatted", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("t.id", "id"));
		columns.add(new TextColumn4MapKey("a.status_", "status",
				getText("template.status"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(this.getStatuses())));
		columns.add(new TextColumn4MapKey("t.order_", "orderNo",
				getText("template.order"), 60).setSortable(true));
		columns.add(new TextColumn4MapKey("t.category", "category",
				getText("template.category"), 150).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.name", "type",
				getText("template.type"), 150).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.subject", "subject",
				getText("template.tfsubject"), 200).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.code", "code",
				getText("template.code"), 200).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.version_", "version",
				getText("template.version"), 100).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.path", "path",
				getText("template.tfpath")).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.formatted", "formatted",
				getText("template.file.formatted"), 80).setSortable(true)
				.setValueFormater(new BooleanFormater()));
		columns.add(new TextColumn4MapKey("t.size_", "size",
				getText("template.file.size"),110).setUseTitleFromLabel(true)
				.setValueFormater(new FileSizeFormater()));
		columns.add(new TextColumn4MapKey("t.desc_", "desc_",
				getText("template.desc"), 100).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.inner_", "inner",
				getText("template.inner"), 35).setSortable(true)
				.setValueFormater(new BooleanFormater()));
		columns.add(new TextColumn4MapKey("au.actor_name", "uname",
				getText("template.author"), 80));
		columns.add(new TextColumn4MapKey("t.file_date", "file_date",
				getText("template.fileDate"), 130)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("am.actor_name", "mname",
				getText("template.modifier"), 80));
		columns.add(new TextColumn4MapKey("t.modified_date", "modified_date",
				getText("template.modifiedDate"), 130)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new HiddenColumn4MapKey("typeCode", "typeCode"));
		columns.add(new HiddenColumn4MapKey("uid", "uid"));
		return columns;
	}


	// 状态键值转换
	private Map<String, String> getStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("template.status.normal"));
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
				getText("template.status.disabled"));
		statuses.put("", getText("template.status.all"));
		return statuses;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "t.code", "am.actor_name", "t.path", "t.subject",
				"t.version_", "t.category","a.name" };
	}

	@Override
	protected String getFormActionName() {
		return "template";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(850).setMinWidth(400)
				.setHeight(400).setMinHeight(300).setHelp("mubanguanli");
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		if (!this.isReadonly()) {
			if(code == null || code.length()==0){
				// 新建按钮
				tb.addButton(this.getDefaultCreateToolbarButton());

				// 编辑按钮
				tb.addButton(this.getDefaultEditToolbarButton());
				// 删除按钮
				tb.addButton(new ToolbarButton().setIcon("ui-icon-trash")
						.setText(getText("label.delete"))
						.setClick("bc.templateList.deleteone"));
			}

			// 下载
			tb.addButton(new ToolbarButton()
					.setIcon("ui-icon-arrowthickstop-1-s")
					.setText(getText("label.download"))
					.setClick("bc.templateList.download"));

			// 在线查看
			tb.addButton(new ToolbarButton().setIcon("ui-icon-lightbulb")
					.setText(getText("template.preview.inline"))
					.setClick("bc.templateList.inline"));
			
			if(code == null || code.length()==0){
				// "更多"按钮
				ToolbarMenuButton menuButton = new ToolbarMenuButton(
						getText("template.config"))
						.setChange("bc.templateList.config");
				tb.addButton(menuButton.setIcon("ui-icon-wrench"));
				// 配置模板类型
				menuButton.addMenuItem(getText("template.config.type"), "type");
				// 配置模板参数
				menuButton.addMenuItem(getText("template.config.param"), "param");
			}
		}

		// 状态按钮组
		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(this.getStatuses(),
				"status", 0, getText("template.status.tips")));

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if (status != null && status.length() > 0 && code != null
				&& code.length() > 0) {
			statusCondition = new AndCondition(new EqualsCondition("t.status_",
					Integer.parseInt(status)), new EqualsCondition("t.code",
					code));
		} else if (status != null && status.length() > 0) {
			statusCondition = new EqualsCondition("t.status_",
					Integer.parseInt(status));
		} else if (code != null && code.length() > 0) {
			statusCondition = new EqualsCondition("t.code", code);
		}
		return statusCondition;
	}
	
	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		if(status != null && status.length() > 0 && code != null
				&& code.length() > 0){
			json.put("status", status);
			json.put("code", code);
		}else if(status != null && status.length() > 0){
			json.put("status", status);
		}else if(code != null && code.length() > 0){
			json.put("code", code);		
		}
		if(json.isEmpty()) return null;
		return json;
	}
	

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/template/list.js";
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}
	
	private TemplateTypeService templateTypeService;
	private TemplateService templateService;
	
	@Autowired
	public void setTemplateTypeService(TemplateTypeService templateTypeService) {
		this.templateTypeService = templateTypeService;
	}
	
	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	public JSONArray types;
	public JSONArray categorys;

	@Override
	protected void initConditionsFrom() throws Exception {
		this.types=OptionItem.toLabelValues(this.templateTypeService.findTemplateTypeOption(false));
		this.categorys=OptionItem.toLabelValues(this.templateService.findCategoryOption());
	}
	
	
	// ==高级搜索代码结束==

	
}
