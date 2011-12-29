/**
 * 
 */
package cn.bc.bulletin.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.bulletin.domain.Bulletin;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
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
 * 公告视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class BulletinsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status;

	@Override
	protected String getFormActionName() {
		return "bulletin";
	}

	@Override
	public boolean isReadonly() {
		// 公告管理或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.bulletin"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		if (!this.isReadonly()) {// 管理员看本单位的所有状态公告或全系统公告
			return new OrderCondition("b.status_", Direction.Asc).add(
					"b.issue_date", Direction.Desc);
		} else {// 普通用户仅看已发布的本单位或全系统公告
			return new OrderCondition("b.issue_date", Direction.Desc);
		}
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select b.id id,b.status_ status,b.scope scope,b.subject subject,b.unit_id unitId,u.name unitName");
		sql.append(",b.issue_date issueDate,b.issuer_id issuerId,i.name issuerName,b.file_date fileDate,a.actor_name authorName");
		sql.append(" from bc_bulletin b");
		sql.append(" inner join bc_identity_actor u on u.id = b.unit_id");
		sql.append(" inner join bc_identity_actor_history a on a.id = b.author_id");
		sql.append(" left join bc_identity_actor i on i.id = b.issuer_id");
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
				map.put("scope", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("unitId", rs[i++]);
				map.put("unitName", rs[i++]);
				map.put("issueDate", rs[i++]);
				map.put("issuerId", rs[i++]);
				map.put("issuerName", rs[i++]);
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
		columns.add(new IdColumn4MapKey("b.id", "id"));

		boolean isManager = !this.isReadonly();
		if (isManager) {
			columns.add(new TextColumn4MapKey("b.status_", "status",
					getText("bulletin.status"), 60).setSortable(true)
					.setValueFormater(new KeyValueFormater(getStatuses())));
		}

		columns.add(new TextColumn4MapKey("b.issue_date", "issueDate",
				getText("bulletin.issueDate"), 90).setSortable(true)
				.setDir(Direction.Desc)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("i.name", "issuerName",
				getText("bulletin.issuerName"), 90).setSortable(true));
		columns.add(new TextColumn4MapKey("b.subject", "subject",
				getText("bulletin.subject")).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.scope", "scope",
				getText("bulletin.scope"), 90).setSortable(true)
				.setValueFormater(new KeyValueFormater(getScopes())));

		if (isManager) {
			columns.add(new TextColumn4MapKey("b.file_date", "fileDate",
					getText("bulletin.fileDate"), 90).setSortable(true)
					.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
			columns.add(new TextColumn4MapKey("a.name", "authorName",
					getText("bulletin.authorName"), 80).setSortable(true));
			columns.add(new TextColumn4MapKey("u.name", "unitName",
					getText("bulletin.unitName"), 80).setSortable(true));
		}

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "b.subject", "i.name" };
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
		Toolbar tb = super.getHtmlPageToolbar();
		if (!this.isReadonly())
			tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
					this.getStatuses(), "status", 1,
					getText("title.click2changeSearchStatus")));
		return tb;
	}

	@Override
	protected Condition getGridSpecalCondition() {
		SystemContext context = (SystemContext) this.getContext();
		Long curUnitId = context.getUnit().getId();// 当前用户所在单位的id
		boolean isManager = !this.isReadonly();// 当前用户是否有管理公告权限

		// 状态条件
		Condition statusCondition = ConditionUtils
				.toConditionByComma4IntegerValue(this.status, "b.status_");

		// 按权限的条件
		Condition securityCondition;
		if (isManager) {// 管理员看 本单位的所有状态公告 或 全系统的已发布公告
			securityCondition = new OrCondition()
					.setAddBracket(true)
					.add(new AndCondition()
							.setAddBracket(true)
							.add(new EqualsCondition("b.status_", new Integer(
									Bulletin.STATUS_ISSUED)))
							.add(new EqualsCondition("b.scope",
									Bulletin.SCOPE_SYSTEM)))
					.add(new EqualsCondition("u.id", curUnitId));
		} else {// 普通用户仅看已发布的 本单位或全系统 公告
			securityCondition = new AndCondition()
					.setAddBracket(true)
					.add(new EqualsCondition("b.status_", new Integer(
							Bulletin.STATUS_ISSUED)))
					.add(new OrCondition()
							.setAddBracket(true)
							.add(new EqualsCondition("u.id", curUnitId))
							.add(new EqualsCondition("b.scope",
									Bulletin.SCOPE_SYSTEM)));
		}

		// 合并条件
		return ConditionUtils.mix2AndCondition(statusCondition, securityCondition);
	}

	/**
	 * 获取公告状态值转换列表
	 * 
	 * @return
	 */
	private Map<String, String> getStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Bulletin.STATUS_DRAFT),
				getText("bulletin.status.draft"));
		statuses.put(String.valueOf(Bulletin.STATUS_ISSUED),
				getText("bulletin.status.issued"));
		statuses.put(String.valueOf(Bulletin.STATUS_OVERDUE),
				getText("bulletin.status.overdue"));
		statuses.put("", getText("bulletin.status.all"));
		return statuses;
	}

	/**
	 * 获取公告发布范围转换列表
	 * 
	 * @return
	 */
	private Map<String, String> getScopes() {
		Map<String, String> scopes = new HashMap<String, String>();
		scopes.put(String.valueOf(Bulletin.SCOPE_LOCALUNIT),
				getText("bulletin.scope.localUnit"));
		scopes.put(String.valueOf(Bulletin.SCOPE_SYSTEM),
				getText("bulletin.scope.system"));
		return scopes;
	}
}
