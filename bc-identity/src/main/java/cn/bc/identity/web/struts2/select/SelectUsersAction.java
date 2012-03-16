/**
 * 
 */
package cn.bc.identity.web.struts2.select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 选择用户Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectUsersAction extends
		AbstractSelectPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED); // 车辆的状态，多个用逗号连接

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认排序方向：创建时间
		return new OrderCondition("h.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select h.id,a.status_,h.actor_name,h.upper_name,a.code ");
		sql.append("from bc_identity_actor_history h");
		sql.append(" left join bc_identity_actor a on a.id=h.actor_id ");
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
				map.put("actor_name", rs[i++]);
				map.put("upper_name", rs[i++]);
				map.put("code", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("h.id", "id"));
		columns.add(new TextColumn4MapKey("h.actor_name", "actor_name",
				getText("car.code"), 50).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.code", "code",
				getText("car.motorcade"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("h.upper_name", "upper_name",
				getText("car.businessType"), 60).setSortable(true)
				.setUseTitleFromLabel(true));
		return columns;
	}

	@Override
	protected String getHtmlPageTitle() {
		return this.getText("user.title.selectUser");
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "h.actor_name", "a.code" };
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(400).setHeight(450);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['actor_name']";
	}

	@Override
	protected HtmlPage buildHtmlPage() {
		return super.buildHtmlPage().setNamespace(
				this.getHtmlPageNamespace() + "/selectUser");
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/user/select.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				return new EqualsCondition("a.status_", new Integer(ss[0]));
			} else {
				return new InCondition("a.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		} else {
			return null;
		}
	}

	@Override
	protected Json getGridExtrasData() {
		if (this.status == null || this.status.length() == 0) {
			return null;
		} else {
			Json json = new Json();
			json.put("status", status);
			return json;
		}
	}

	@Override
	protected String getClickOkMethod() {
		return "bs.carSelectDialog.clickOk";
	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + "/bc";
	}
}
