/**
 * 
 */
package cn.bc.docs.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.FileSizeFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;

/**
 * 附件视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class AttachsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status;

	@Override
	protected String getFormActionName() {
		return "attach";
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.attach"),
				getText("key.role.bc.admin"));// 附件管理或超级管理角色
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		return new OrderCondition("a.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id id,a.status_ status,a.file_date fileDate,a.size_ size,a.format format,a.subject subject");
		sql.append(",a.path path,a.ptype ptype,a.puid puid,a.apppath apppath,h.actor_name authorName");
		sql.append(" from bc_docs_attach a");
		sql.append(" inner join bc_identity_actor_history h on h.id = a.author_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status", rs[i++]);
				map.put("fileDate", rs[i++]);
				map.put("size", rs[i++]);
				map.put("format", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("path", rs[i++]);
				map.put("ptype", rs[i++]);
				map.put("puid", rs[i++]);
				map.put("apppath", rs[i++]);
				map.put("authorName", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc/docs/attach/list.js";
	}

	@Override
	protected String getGridDblRowMethod() {
		return null;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("a.id", "id"));
		columns.add(new TextColumn4MapKey("a.status_", "status",
				getText("attach.status"), 45)
				.setValueFormater(new EntityStatusFormater(getEntityStatuses())));
		columns.add(new TextColumn4MapKey("a.file_date", "fileDate",
				getText("attach.fileDate"), 130).setSortable(true)
				.setDir(Direction.Desc)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("h.actor_name", "authorName",
				getText("attach.authorName"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("a.size_", "size",
				getText("attach.size"), 60).setSortable(true).setValueFormater(
				new FileSizeFormater()));
		columns.add(new TextColumn4MapKey("a.format", "format",
				getText("attach.format"), 45).setSortable(true));
		columns.add(new TextColumn4MapKey("a.subject", "subject",
				getText("attach.subject")).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.path", "path",
				getText("attach.path"), 120).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.ptype", "ptype",
				getText("attach.ptype"), 120).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.puid", "puid",
				getText("attach.puid"), 150).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.apppath", "apppath",
				getText("attach.appPath"), 90).setSortable(true)
				.setValueFormater(
						new BooleanFormater(getText("label.yes"),
								getText("label.no"))));
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "a.subject", "h.actor_name", "a.path" };
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();
		// 查看按钮
		// tb.addButton(getDefaultOpenToolbarButton());

		// 在线查看
		tb.addButton(new ToolbarButton().setIcon("ui-icon-lightbulb")
				.setText(getText("label.preview.inline"))
				.setClick("bc.attachList.inline"));

		// 下载
		tb.addButton(new ToolbarButton().setIcon("ui-icon-arrowthickstop-1-s")
				.setText(getText("label.download"))
				.setClick("bc.attachList.download"));

		// 打包下载
		tb.addButton(new ToolbarButton().setIcon("ui-icon-link")
				.setText(getText("label.download.zip"))
				.setClick("bc.attachList.downloadZip"));

		// 访问日志
		tb.addButton(new ToolbarButton().setIcon("ui-icon-calendar")
				.setText(getText("attach.visit.history"))
				.setClick("bc.attachList.visitHistory"));

		// 搜索
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected Condition getGridSpecalCondition() {
		if (this.isReadonly()) {// 非管理员只能看自己上传的附件
			SystemContext context = (SystemContext) this.getContext();
			return new EqualsCondition("a.author_id", context.getUserHistory()
					.getId());
		} else {
			return null;
		}
	}
}
