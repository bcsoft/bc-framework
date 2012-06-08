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
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
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
		return new OrderCondition("l.file_date", Direction.Desc);

	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select l.id,l.type_,l.file_date,ac.actor_name author,l.way,l.ptype,l.pid,l.subject ");
		sql.append(" from bc_log_operate l");
		sql.append(" left join BC_IDENTITY_ACTOR_HISTORY ac on ac.id=l.author_id");
		sql.append(" left join bc_identity_actor a on a.id=ac.actor_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("type", rs[i++]);// 类别：0-工作日志,1-审计日志
				map.put("file_date", rs[i++]);// 创建时间
				map.put("author", rs[i++]);// 创建人
				map.put("way", rs[i++]);// 创建方式：0-用户创建,1-自动生成
				map.put("ptype", rs[i++]);// 所属模块
				map.put("pid", rs[i++]);// 文档标识
				map.put("subject", rs[i++]);// 标题
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("l.id", "id"));
		columns.add(new TextColumn4MapKey("l.file_date", "file_date",
				getText("operateLog.fileDate"), 140).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("l.subject", "subject",
				getText("operateLog.subject")).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("ac.actor_name", "author",
				getText("operateLog.author"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("l.pid", "pid",
				getText("operateLog.pid"), 80).setSortable(true)
				.setUseTitleFromLabel(true));

		return columns;
	}




	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "ac.actor_name", "l.subject", "l.pid", "a.code",
				"a.py" };
	}

	@Override
	protected String getFormActionName() {
		return "operateLog";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 模块条件
		Condition moduleCondition = null;
		if (module != null) {
			moduleCondition = new EqualsCondition("l.ptype", module);
		}
		// 车辆Id条件
		Condition carIdCondition = null;
		if (carId != null) {
			carIdCondition = new EqualsCondition("l.pid", carId);
		}
		// 司机ID条件
		Condition carManIdCondition = null;
		if (carManId != null) {
			carManIdCondition = new EqualsCondition("l.pid", carManId);
		}
		return ConditionUtils.mix2AndCondition(moduleCondition, ConditionUtils
				.mix2OrCondition(carIdCondition, carManIdCondition));
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();

		// 模块条件
		if (this.module != null && this.module.trim().length() > 0) {
			json.put("module", module);
		}
		// 车辆ID条件
		if (this.carId != null && this.carId.trim().length() > 0) {
			json.put("carId", carId);
		}
		// 司机ID条件
		if (this.carManId != null && this.carManId.trim().length() > 0) {
			json.put("carManId", carManId);
		}
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
			// 查看按钮
			tb.addButton(this.getDefaultOpenToolbarButton());
			// 车辆司机表单页签下可以新建
			if (carId != null || carManId != null) {
				// 编辑按钮
				tb.addButton(this.getDefaultCreateToolbarButton());

			}
		}

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected String getGridDblRowMethod() {
		return "bc.page.open";
	}


}
