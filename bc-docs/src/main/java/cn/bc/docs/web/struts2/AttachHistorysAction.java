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
import cn.bc.docs.domain.AttachHistory;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 附件视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class AttachHistorysAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status;

	@Override
	protected String getFormActionName() {
		return "attachHistory";
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.attach"),
				getText("key.role.bc.admin"));// 附件管理或超级管理角色
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		return new OrderCondition("ah.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select ah.id id,ah.file_date fileDate,ah.type_ as type,ah.format format,ah.subject subject");
		sql.append(",a.path path,ah.c_ip clientIp,ah.c_info clientInfo,h.actor_name authorName");
		sql.append(" from bc_docs_attach_history ah");
		sql.append(" inner join bc_docs_attach a on a.id = ah.aid");
		sql.append(" inner join bc_identity_actor_history h on h.id = ah.author_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("fileDate", rs[i++]);
				map.put("type", rs[i++]);
				map.put("format", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("path", rs[i++]);
				map.put("clientIp", rs[i++]);
				map.put("clientInfo", rs[i++]);
				map.put("authorName", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected String getGridDblRowMethod() {
		return null;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("ah.id", "id"));
		columns.add(new TextColumn4MapKey("ah.file_date", "fileDate",
				getText("attachHistory.fileDate"), 130).setSortable(true)
				.setDir(Direction.Desc)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("h.actor_name", "authorName",
				getText("attachHistory.authorName"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("ah.type_", "type",
				getText("attachHistory.type"), 60).setSortable(true).setValueFormater(
				new KeyValueFormater(getTypes())));
		columns.add(new TextColumn4MapKey("ah.format", "format",
				getText("attachHistory.format"), 60).setSortable(true));
		columns.add(new TextColumn4MapKey("ah.subject", "subject",
				getText("attachHistory.subject")).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.path", "path",
				getText("attachHistory.path"), 120).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("l.c_ip", "clientIp",
				getText("attachHistory.clientIp"), 110).setSortable(true));
		columns.add(new TextColumn4MapKey("l.c_info", "clientInfo",
				getText("attachHistory.clientInfo"), 200).setSortable(true)
				.setUseTitleFromLabel(true));
		return columns;
	}

	/**
	 * 获取操作类型值转换列表
	 * 
	 * @return
	 */
	private Map<String, String> getTypes() {
		Map<String, String> types = new HashMap<String, String>();
		types.put(String.valueOf(AttachHistory.TYPE_DOWNLOAD),
				getText("attachHistory.type.download"));
		types.put(String.valueOf(AttachHistory.TYPE_INLINE),
				getText("attachHistory.type.inline"));
		types.put(String.valueOf(AttachHistory.TYPE_ZIP),
				getText("attachHistory.type.zip"));
		types.put(String.valueOf(AttachHistory.TYPE_CONVERT),
				getText("attachHistory.type.convert"));
		types.put(String.valueOf(AttachHistory.TYPE_DELETED),
				getText("attachHistory.type.deleted"));
		return types;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "ah.subject", "h.actor_name", "a.path", "ah.c_ip" };
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(600).setMinWidth(300)
				.setHeight(380).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();
		
		tb.addButton(Toolbar.getDefaultEmptyToolbarButton());

		// 搜索
		tb.addButton(getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected Condition getGridSpecalCondition() {
		if (this.isReadonly()) {// 非管理员只能看自己的操作记录
			SystemContext context = (SystemContext) this.getContext();
			return new EqualsCondition("ah.author_id", context.getUserHistory()
					.getId());
		} else {
			return null;
		}
	}
}