/**
 * 
 */
package cn.bc.identity.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Direction;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Actor;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;

/**
 * 用户Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class UsersAction extends AbstractActorsAction {
	private static final long serialVersionUID = 1L;

	@Override
	protected String getFormActionName() {
		return "user";
	}

	@Override
	protected String getActorType() {
		// 用户条件
		return String.valueOf(Actor.TYPE_USER);
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = getHtmlPageToolbar(false);
		if (!this.isReadonly())
			tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
					.setText(getText("user.password.reset"))
					.setClick("bc.userList.setPassword"));
		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(this.getBCStatuses(),
				"status", 0, getText("title.click2changeSearchStatus")));

		return tb;
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id,a.type_,a.status_,a.name name,a.code,a.order_,a.phone,a.email,a.pname");
		sql.append(",ad.card,ad.sex,ad.create_date,ad.work_date,d.name duty");
		sql.append(" from bc_identity_actor a");
		sql.append(" left join bc_identity_actor_detail ad on ad.id = a.detail_id");
		sql.append(" left join bc_identity_duty d on d.id = ad.duty_id");
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
				map.put("status", rs[i++]);
				map.put("name", rs[i++]);
				map.put("code", rs[i++]);
				map.put("orderNo", rs[i++]);
				map.put("phone", rs[i++]);
				map.put("email", rs[i++]);
				map.put("pname", rs[i++]);
				map.put("card", rs[i++]);
				map.put("sex", rs[i++]);
				map.put("createDate", rs[i++]);
				map.put("workDate", rs[i++]);
				map.put("duty", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("a.id", "id"));
		columns.add(new TextColumn4MapKey("a.status_", "status",
				getText("actor.status"), 60).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getBCStatuses())));
		columns.add(new TextColumn4MapKey("a.pname", "pname",
				getText("actor.pname")).setSortable(true).setUseTitleFromLabel(
				true));
		columns.add(new TextColumn4MapKey("a.name", "name",
				getText("user.name"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("ad.sex", "sex", getText("user.sex"),
				60).setUseTitleFromLabel(true).setSortable(true)
				.setValueFormater(new SexFormater()));
		columns.add(new TextColumn4MapKey("a.code", "code",
				getText("user.code"), 120).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("d.duty", "duty",
				getText("user.duty"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("ad.card", "card",
				getText("user.card"), 120).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.order_", "orderNo",
				getText("actor.order"), 100).setSortable(true)
				.setDir(Direction.Asc).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.phone", "phone",
				getText("user.phone"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("ad.work_date", "workDate",
				getText("user.workDate"), 100).setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(new CalendarFormater()));
		columns.add(new TextColumn4MapKey("ad.create_date", "createDate",
				getText("user.createDate"), 100).setSortable(true)
				.setUseTitleFromLabel(true)
				.setValueFormater(new CalendarFormater()));

		return columns;
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc/identity/user/list.js";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(450)
				.setHeight(500).setMinHeight(200);
	}
}
