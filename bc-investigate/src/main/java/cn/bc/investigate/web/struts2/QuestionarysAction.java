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
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.investigate.domain.Questionary;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.DateRangeFormater;
import cn.bc.web.formater.EntityStatusFormater;
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
public class QuestionarysAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String carId;// 车辆Id
	public String carManId;// 司机Id
	public String module;// 所属模块
	public List<Map<String, String>> ptypeList; // 所属模块列表

	@Override
	public boolean isReadonly() {
		// 操作日志管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.operateLog"),
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
		sql.append("select q.id,q.status_,q.subject,q.start_date,q.end_date,iss.actor_name issuer,q.issue_date");
		sql.append(",q.file_date,ad.actor_name author");
		sql.append(" from bc_ivg_questionary q");
		sql.append(" left join BC_IDENTITY_ACTOR_HISTORY ad on ad.id=q.author_id");
		sql.append(" left join BC_IDENTITY_ACTOR_HISTORY iss on iss.id=q.issuer_id");
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
				map.put("issuer", rs[i++]);
				map.put("issue_date", rs[i++]);
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
				getText("questionary.status"), 40).setSortable(true)
				.setValueFormater(new EntityStatusFormater()));
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
		columns.add(new TextColumn4MapKey("iss.actor_name", "issuer",
				getText("questionary.issuer"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("q.issue_date", "issue_date",
				getText("questionary.issueDate"), 140).setSortable(true)
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
		return "questionary";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 模块条件
		Condition moduleCondition = null;
		// 车辆Id条件
		Condition carIdCondition = null;
		// 司机ID条件
		Condition carManIdCondition = null;
		return ConditionUtils.mix2AndCondition(moduleCondition, ConditionUtils
				.mix2OrCondition(carIdCondition, carManIdCondition));
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();

		return json.isEmpty() ? null : json;
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

		if (this.isReadonly()) {
			// 查看按钮
			tb.addButton(this.getDefaultOpenToolbarButton());
		} else {
			// 新建按钮
			tb.addButton(this.getDefaultCreateToolbarButton());

			// 编辑按钮
			tb.addButton(this.getDefaultEditToolbarButton());
			// 删除
			tb.addButton(this.getDefaultDeleteToolbarButton());
			// 如果是管理员,可以看到状态按钮组
			if (!this.isReadonly()) {
				tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
						this.getBSStatuses(), "status", 1,
						getText("title.click2changeSearchStatus")));

			}
		}

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}

	/**
	 * 状态值转换列表：待发布|已发布|已结束|全部
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

	@Override
	protected String getGridDblRowMethod() {
		return "bc.page.open";
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}

	// ==高级搜索代码结束==

}
