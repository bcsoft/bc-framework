/**
 * 
 */
package cn.bc.feedback.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.feedback.domain.Feedback;
import cn.bc.identity.web.SystemContext;
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
 * 用户反馈视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FeedbacksAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status;

	@Override
	protected String getFormActionName() {
		return "feedback";
	}

	@Override
	public boolean isReadonly() {
		// 公告管理或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.feedback"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		return new OrderCondition("f.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select f.id,f.status_,f.subject,f.file_date,h.actor_name");
		sql.append(" from bc_feedback f");
		sql.append(" inner join bc_identity_actor_history h on h.id = f.author_id");
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
				map.put("subject", rs[i++]);
				map.put("fileDate", rs[i++]);
				map.put("authorName", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("f.id", "id"));
		columns.add(new TextColumn4MapKey("f.status_", "status",
				getText("label.status"), 80).setSortable(true)
				.setValueFormater(new KeyValueFormater(getStatuses())));
		columns.add(new TextColumn4MapKey("f.subject", "subject",
				getText("label.subject")).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("f.file_date", "fileDate",
				getText("label.submitDate"), 130).setSortable(true)
				.setDir(Direction.Desc)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		if (!this.isReadonly()) {
			columns.add(new TextColumn4MapKey("h.actor_name", "authorName",
					getText("label.submitterName"), 80).setSortable(true));
		}

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "f.subject", "f.author_id" };
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(450)
				.setHeight(400).setMinHeight(200);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		// 新建按钮
		tb.addButton(Toolbar
				.getDefaultCreateToolbarButton(getText("label.create")));

		// 查看按钮
		tb.addButton(Toolbar.getDefaultOpenToolbarButton(getText("label.read")));

		if (!this.isReadonly()) {
			// 彻底删除按钮
			tb.addButton(new ToolbarButton().setIcon("ui-icon-trash")
					.setText(getText("label.delete.clean")).setAction("delete"));
		} else {// 普通用户
			// 删除按钮
			tb.addButton(Toolbar
					.getDefaultDeleteToolbarButton(getText("label.delete")));
		}

		// 搜索按钮
		tb.addButton(Toolbar
				.getDefaultSearchToolbarButton(getText("title.click2search")));

		return tb;
	}

	@Override
	protected Condition getGridSpecalCondition() {
		SystemContext context = (SystemContext) this.getContext();
		Condition authorCondition;
		if (!this.isReadonly()) {// 本模块管理员看全部
			authorCondition = null;
		} else {// 普通用户仅看自己提交的
			authorCondition = new EqualsCondition("f.author_id", context
					.getUserHistory().getId());
		}

		// 状态条件
		Condition statusCondition = ConditionUtils
				.toConditionByComma4IntegerValue(this.status, "f.status_");

		return ConditionUtils
				.mix2AndCondition(statusCondition, authorCondition);
	}

	/**
	 * 获取状态值转换列表
	 * 
	 * @return
	 */
	private Map<String, String> getStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Feedback.STATUS_DRAFT),
				getText("feedback.status.draft"));
		statuses.put(String.valueOf(Feedback.STATUS_SUMMIT),
				getText("feedback.status.submmit"));
		statuses.put(String.valueOf(Feedback.STATUS_ACCEPT),
				getText("feedback.status.accept"));
		statuses.put(String.valueOf(Feedback.STATUS_REJECT),
				getText("feedback.status.reject"));
		statuses.put(String.valueOf(Feedback.STATUS_DELETED),
				getText("feedback.status.deleted"));
		statuses.put("", getText("feedback.status.all"));
		return statuses;
	}
}
