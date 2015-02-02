package cn.bc.workday.web.struts2;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

/**
 * 
 * @author LeeDane
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class WorkdaysAction extends ViewAction<Map<String, Object>> {

	private static final long serialVersionUID = 1L;
	public String status = "0";

	@Override
	public boolean isReadonly() {
		// 证工作日管理||超级管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.workday.manage"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected String getModuleContextPath() {
		return this.getContextPath() + "/bc";
	}

	@Override
	protected String getHtmlPageNamespace() {
		return getModuleContextPath() + "/workday";
	}

	@Override
	protected String getFormActionName() {
		return "workday";
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select w.id, w.dayoff, w.from_date, w.to_date,w.desc_ from bc_workday w ");
		sqlObject.setSql(sql.toString());
		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("dayoff", rs[i++]);
				map.put("from_date", rs[i++]);
				map.put("to_date", rs[i++]);
				map.put("desc_", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {

		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("w.id", "id"));

		// 类别
		columns.add(new TextColumn4MapKey("w.dayoff", "dayoff",
				getText("workday.dayoff"), 50).setSortable(true)
				.setValueFormater(new AbstractFormater<String>() {

					@Override
					public String format(Object context, Object value) {
						if (Boolean.valueOf(String.valueOf(value))) {
							return "放假";
						} else {
							return "上班";
						}
					}
		}));

		// 日期
		columns.add(new TextColumn4MapKey("", "", getText("workday.date"), 180)
				.setSortable(true).setValueFormater(
						new AbstractFormater<Object>() {
							@Override
							public Object format(Object context, Object value) {
								@SuppressWarnings("unchecked")
								Map<String, Object> m = (Map<String, Object>) context;
								return m.get("to_date") == null ? String
										.valueOf(m.get("from_date")) : String
										.valueOf(m.get("from_date"))
										+ "~"
										+ String.valueOf(m.get("to_date"));
							}
						}));

		// 备注
		columns.add(new TextColumn4MapKey("w.desc_", "desc_",
				getText("workday.desc")).setSortable(true));

		return columns;
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("workday.title");
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(500).setMinWidth(450)
				.setHeight(400).setMinHeight(200);
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();
		if (!this.isReadonly()) { // 工作日管理||超级管理员
			// 新建按钮
			tb.addButton(Toolbar
					.getDefaultCreateToolbarButton(getText("label.create")));

			// 编辑按钮
			tb.addButton(Toolbar
					.getDefaultEditToolbarButton(getText("label.edit")));

			// 删除按钮
			tb.addButton(Toolbar
					.getDefaultDeleteToolbarButton(getText("label.delete")));

			// 搜索按钮
			tb.addButton(this.getDefaultSearchToolbarButton());

		} else {
			// 查看按钮
			tb.addButton(this.getDefaultOpenToolbarButton());
		}
		return tb;
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		return new OrderCondition("w.from_date", Direction.Desc);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['desc_']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] {"w.desc_" };
	}
}