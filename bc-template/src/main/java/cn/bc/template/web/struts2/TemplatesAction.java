package cn.bc.template.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.template.domain.Template;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;

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
		sql.append("select t.id,t.order_ as order,t.code,t.type_ as type,t.name,t.path as filename");
		sql.append(",au.actor_name as uname,t.file_date,am.actor_name as mname");
		sql.append(",t.modified_date,t.inner_ as inner");
		sql.append(" from bc_template t");
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
				map.put("order", rs[i++]);
				map.put("code", rs[i++]);
				map.put("type", rs[i++]);
				map.put("name", rs[i++]);
				map.put("filename", rs[i++]);
				map.put("uname", rs[i++]);
				map.put("file_date", rs[i++]);
				map.put("mname", rs[i++]);
				map.put("modified_date", rs[i++]);
				map.put("inner", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("t.id", "id"));
		columns.add(new TextColumn4MapKey("t.order_", "order",
				getText("template.order"), 60).setSortable(true));
		columns.add(new TextColumn4MapKey("t.code", "code",
				getText("template.code"), 100).setSortable(true));
		columns.add(new TextColumn4MapKey("t.type_", "type",
				getText("template.type"), 80)
				.setValueFormater(new KeyValueFormater(this.getTypes())));
		columns.add(new TextColumn4MapKey("t.name", "name",
				getText("template.name"), 100).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.path", "filename",
				getText("template.tfpath")).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.inner_", "inner",
				getText("template.inner"),35).setSortable(true).setValueFormater(
				new KeyValueFormater(this.getInners())));
		columns.add(new TextColumn4MapKey("au.actor_name", "uname",
				getText("template.author"), 60));
		columns.add(new TextColumn4MapKey("t.file_date", "file_date",
				getText("template.fileDate"), 150)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm:ss")));
		columns.add(new TextColumn4MapKey("am.actor_name", "mname",
				getText("template.modifier"), 80));
		columns.add(new TextColumn4MapKey("t.modified_date", "modified_date",
				getText("template.modifiedDate"),150)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm:ss")));
		
		return columns;
	}

	/**
	 * 类型值转换:Excel|Word|文本文件|Html文件|其它文件
	 * 
	 */
	private Map<String, String> getTypes() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put(String.valueOf(Template.TYPE_EXCEL),
				getText("template.type.excel"));
		map.put(String.valueOf(Template.TYPE_WORD),
				getText("template.type.word"));
		map.put(String.valueOf(Template.TYPE_TEXT),
				getText("template.type.text"));
		map.put(String.valueOf(Template.TYPE_CUSTOM),
				getText("template.type.costom"));
		map.put(String.valueOf(Template.TYPE_OTHER),
				getText("template.type.other"));
		return map;
	}

	/**
	 * 内置值转换:
	 * 
	 */
	private Map<String, String> getInners() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put(String.valueOf(true),
				getText("template.inner.ture"));
		map.put(String.valueOf(false),
				getText("template.inner.false"));

		return map;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "t.code", "t.name", "am.actor_name","t.path" };
	}

	@Override
	protected String getFormActionName() {
		return "template";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		if (!this.isReadonly()) {
			// 新建按钮
			tb.addButton(this.getDefaultCreateToolbarButton());

			// 编辑按钮
			tb.addButton(this.getDefaultEditToolbarButton());

			// 删除按钮
			tb.addButton(new ToolbarButton().setIcon("ui-icon-trash")
					.setText(getText("label.delete"))
					.setClick("bc.templateList.deleteone"));

			// 下载
			tb.addButton(new ToolbarButton()
					.setIcon("ui-icon-arrowthickstop-1-s")
					.setText(getText("label.download")).setClick("bc.templateList.download"));

			// 在线预览
			tb.addButton(new ToolbarButton().setIcon("ui-icon-lightbulb")
					.setText(getText("label.preview.inline")).setClick("bc.templateList.inline"));
		}

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}
	
	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace()+"/template/list.js";
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}
	// ==高级搜索代码结束==
	
	
}
