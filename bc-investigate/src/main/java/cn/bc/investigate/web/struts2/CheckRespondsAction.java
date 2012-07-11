/**
 * 
 */
package cn.bc.investigate.web.struts2;

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
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 查看答卷人 Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CheckRespondsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public Long pid;// 试卷ID

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：创建日期
		return new OrderCondition("r.file_date", Direction.Desc);

	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select r.id,r.pid,r.file_date,actor_name answer,r.score,r.author_id");
		sql.append(" from bc_ivg_respond r");
		sql.append(" left join BC_IDENTITY_ACTOR_HISTORY ad on ad.id=r.author_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("pid", rs[i++]);
				map.put("file_date", rs[i++]);
				map.put("answer", rs[i++]);
				map.put("score", rs[i++]);
				map.put("author_id", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("r.pid", "pid"));
		columns.add(new TextColumn4MapKey("ad.actor_name", "answer",
				getText("checkRespond.answer"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("r.score", "score",
				getText("checkRespond.score"), 50).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("r.file_date", "file_date",
				getText("checkRespond.submitTime"), 140).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new HiddenColumn4MapKey("userId", "author_id"));
		return columns;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "ad.actor_name", "r.score", };
	}

	@Override
	protected String getFormActionName() {
		return "checkRespond";
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		// 试卷ID
		if (this.pid != null) {
			json.put("pid", pid);
		}
		return json.isEmpty() ? null : json;
	}

	@Override
	protected Condition getGridSpecalCondition() {
		Condition pidCondition = null;
		pidCondition = new EqualsCondition("r.pid", new Long(pid));
		return pidCondition;
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		super.extendGridExtrasData(json);

	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(400).setMinWidth(100)
				.setHeight(300).setMinHeight(100);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();
		tb.addButton(Toolbar.getDefaultEmptyToolbarButton());
		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected String getGridDblRowMethod() {
		// 如果作答表里有用户的记录则证明该用户已经作答过

		// 强制为只读表单
		return "bc.checkRespondView.dblclick";
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath()
				+ "/bc/questionary4User/view4checkRespond.js";
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}

	// ==高级搜索代码结束==

}
