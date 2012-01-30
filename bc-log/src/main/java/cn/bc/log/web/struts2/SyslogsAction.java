/**
 * 
 */
package cn.bc.log.web.struts2;

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
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.web.SystemContext;
import cn.bc.log.domain.Syslog;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;

/**
 * 系统日志视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SyslogsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public boolean my = false;

	@Override
	protected String getFormActionName() {
		return "syslog";
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		return new OrderCondition("l.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select l.id as id,l.type_ as type,l.subject as subject,l.file_date as fileDate");
		sql.append(",l.author_id as authorId,h.actor_name as authorName,h.pname as authorDepart");
		sql.append(",l.c_ip as clientIp,l.c_info as clientInfo,l.s_ip as serverIp");
		sql.append(" from bc_log_system as l");
		sql.append(" inner join bc_identity_actor_history as h on h.id=l.author_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("type", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("fileDate", rs[i++]);
				map.put("authorId", rs[i++]);
				map.put("authorName", rs[i++]);
				map.put("authorDepart", rs[i++]);
				map.put("clientIp", rs[i++]);
				map.put("clientInfo", rs[i++]);
				map.put("serverIp", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("l.id", "id"));
		columns.add(new TextColumn4MapKey("l.file_date", "fileDate",
				getText("syslog.createDate"), 130).setSortable(true)
				.setDir(Direction.Desc)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("l.type_", "type",
				getText("syslog.type"), 60).setSortable(true).setValueFormater(
				new SyslogTypeFormater(getSyslogTypes())));
		columns.add(new TextColumn4MapKey("h.actor_name", "authorName",
				getText("syslog.userName"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("l.c_ip", "clientIp",
				getText("syslog.clientIp"), 110).setSortable(true));
		columns.add(new TextColumn4MapKey("h.pname", "authorDepart",
				getText("syslog.departName")).setSortable(true)
				.setUseTitleFromLabel(true));
		if (!my)
			columns.add(new TextColumn4MapKey("l.s_ip", "serverIp",
					getText("syslog.serverIp"), 110).setSortable(true));
		columns.add(new TextColumn4MapKey("l.c_info", "clientInfo",
				getText("syslog.clientInfo"), 200).setSortable(true)
				.setUseTitleFromLabel(true));

		return columns;
	}

	/**
	 * 获取系统日志分类值转换列表
	 * 
	 * @return
	 */
	private Map<String, String> getSyslogTypes() {
		Map<String, String> types = new HashMap<String, String>();
		types = new HashMap<String, String>();
		types.put(String.valueOf(Syslog.TYPE_LOGIN),
				getText("syslog.type.login"));
		types.put(String.valueOf(Syslog.TYPE_LOGOUT),
				getText("syslog.type.logout"));
		types.put(String.valueOf(Syslog.TYPE_LOGIN_TIMEOUT),
				getText("syslog.type.logout2"));
		return types;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "l.subject", "l.c_ip", "l.c_info", "l.s_ip",
				"h.actor_name", "h.pname" };
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setHeight(400)
				.setMinWidth(300).setMinHeight(300);
	}

	@Override
	protected Condition getGridSpecalCondition() {
		if (!my) {// 查看所有用户的日志信息
			return null;
		} else {// 仅查看自己的日志信息
			ActorHistory curUser = (ActorHistory) ((SystemContext) this
					.getContext()).getUserHistory();
			return new EqualsCondition("l.author_id", curUser.getId());
		}
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc/log/syslog/list.js";
	}

	@Override
	protected String getHtmlPageTitle() {
		if (my)
			return this.getText(this.getFormActionName() + ".title.my");
		else
			return this.getText(this.getFormActionName() + ".title");
	}

	@Override
	protected Toolbar getHtmlPageToolbar(boolean useDisabledReplaceDelete) {
		Toolbar tb = new Toolbar();

		// 查看按钮
		tb.addButton(new ToolbarButton().setIcon("ui-icon-check")
				.setText(getText("label.check"))
				.setClick("bc.syslogList.checkWork"));

		// 搜索按钮
		tb.addButton(Toolbar
				.getDefaultSearchToolbarButton(getText("title.click2search")));

		return tb;
	}
}
