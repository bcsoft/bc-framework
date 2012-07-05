/**
 * 
 */
package cn.bc.investigate.web.struts2;

import java.util.ArrayList;
import java.util.Date;
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
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.investigate.domain.Questionary;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.DateRangeFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 调查问卷 Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Questionary4UsersAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(Questionary.STATUS_ISSUE);
	public String userId = String.valueOf(this);

	@Override
	public boolean isReadonly() {
		// 网上考试管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.question.exam"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：创建日期
		return new OrderCondition("q.file_date", Direction.Desc);

	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select q.id,q.status_,q.subject,q.start_date,q.end_date");
		sql.append(",(select count(*) from bc_ivg_question where pid = q.id) count,q.permitted");
		sql.append(",(select count(*) from bc_ivg_respond where pid = q.id) answerNumber");
		sql.append(",iss.actor_name issuer,q.issue_date,q.pigeonhole_date,pig.actor_name pigeonholer");
		sql.append(",q.file_date,ad.actor_name author");
		sql.append(" from bc_ivg_questionary q");
		sql.append(" left join BC_IDENTITY_ACTOR_HISTORY ad on ad.id=q.author_id");
		sql.append(" left join BC_IDENTITY_ACTOR_HISTORY iss on iss.id=q.issuer_id");
		sql.append(" left join BC_IDENTITY_ACTOR_HISTORY pig on pig.id=q.pigeonholer_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status_", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("start_date", rs[i++]);
				map.put("end_date", rs[i++]);
				map.put("count", rs[i++]);
				map.put("permitted", rs[i++]);
				map.put("answerNumber", rs[i++]);
				map.put("issuer", rs[i++]);
				map.put("issue_date", rs[i++]);
				map.put("pigeonhole_date", rs[i++]);
				map.put("pigeonholer", rs[i++]);
				map.put("file_date", rs[i++]);
				map.put("author", rs[i++]);

				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("q.id", "id"));
		columns.add(new TextColumn4MapKey("q.status_", "status_",
				getText("questionary.status"), 60).setSortable(true)
				.setValueFormater(new KeyValueFormater(getBSStatuses())));
		columns.add(new TextColumn4MapKey("q.subject", "subject",
				getText("questionary.subject")).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("q.start_date", "start_date",
				getText("questionary.Deadline"), 180)
				.setValueFormater(new DateRangeFormater("yyyy-MM-dd") {
					@Override
					public Date getToDate(Object context, Object value) {
						@SuppressWarnings("rawtypes")
						Map contract = (Map) context;
						return (Date) contract.get("end_date");
					}
				}));
		columns.add(new TextColumn4MapKey("iss.actor_name", "count",
				getText("questionary.count"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("iss.actor_name", "answerNumber",
				getText("questionary.answerNumber"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("q.permitted", "permitted",
				getText("questionary.permitted"), 150).setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(new BooleanFormater()));
		columns.add(new TextColumn4MapKey("iss.actor_name", "issuer",
				getText("questionary.issuer"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("q.issue_date", "issue_date",
				getText("questionary.issueDate"), 140).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("pig.actor_name", "pigeonholer",
				getText("questionary.pigeonholer"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("q.pigeonhole_date", "file_date",
				getText("questionary.pigeonholeDate"), 140).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("ad.actor_name", "author",
				getText("questionary.author"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("q.file_date", "file_date",
				getText("questionary.fileDate"), 140).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));

		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "ac.actor_name", "q.subject", };
	}

	@Override
	protected String getFormActionName() {
		return "questionary4User";
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();

		return json.isEmpty() ? null : json;
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("q.status_", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("q.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		}

		return ConditionUtils.mix2AndCondition(statusCondition);
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		super.extendGridExtrasData(json);

		// 状态条件
		if (this.status != null && this.status.trim().length() > 0) {
			json.put("status", status);
		}
		// 用户条件
		if (this.userId != null && this.userId.trim().length() > 0) {
			json.put("status", status);
		}

	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();
		// // // 如果是管理员,可以看到状态按钮组
		// if (!this.isReadonly()) {
		// tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
		// this.getBSStatuses(), "status", 1,
		// getText("title.click2changeSearchStatus")));
		//
		// }
		tb.addButton(Toolbar.getDefaultEmptyToolbarButton());
		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}

	/**
	 * 状态值转换列表：待发布|已发布|已归档|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getBSStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Questionary.STATUS_DRAFT),
				getText("questionary.release.wait"));
		statuses.put(String.valueOf(Questionary.STATUS_ISSUE),
				getText("questionary.release.already"));
		statuses.put(String.valueOf(Questionary.STATUS_END),
				getText("questionary.release.end"));
		statuses.put("", getText("questionary.release.all"));
		return statuses;
	}

	/**
	 * 状态值转换列表：待发布|已发布|已归档|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getUserId() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Questionary.STATUS_DRAFT),
				getText("questionary.release.wait"));
		statuses.put(String.valueOf(Questionary.STATUS_ISSUE),
				getText("questionary.release.already"));
		statuses.put(String.valueOf(Questionary.STATUS_END),
				getText("questionary.release.end"));
		statuses.put("", getText("questionary.release.all"));
		return statuses;
	}

	@Override
	protected String getGridDblRowMethod() {
		// 如果作答表里有用户的记录则证明该用户已经作答过

		// 强制为只读表单
		return "bc.questionary4UserView.dblclick";
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc/questionary4User/view.js";
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}

	// ==高级搜索代码结束==

}
