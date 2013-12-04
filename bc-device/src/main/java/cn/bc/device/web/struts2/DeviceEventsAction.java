package cn.bc.device.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.IsNotNullCondition;
import cn.bc.core.query.condition.impl.IsNullCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;

/**
 * 设备事件Action
 * 
 * @author luliang
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class DeviceEventsAction extends ViewAction<Map<String, Object>> {

	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED);// 设备事件的状态

	@Override
	public boolean isReadonly() {
		// 设备管理或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole("BC_DEVICE_MANAGE",
				getText("key.role.bc.admin"));
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询sql语句
		StringBuffer sql = new StringBuffer(
				"select de.id id,(case when en.id is null then 0 else 1 end) status_,de.trigger_time trigger_time");
		sql.append(" ,d.code code,d.name device_name,d.purpose purpose,de.type_ type_");
		sql.append(" from bc_device_event de");
		sql.append(" inner join bc_device d on d.id=de.device_id");
		sql.append(" left join bc_device_event_new en on en.id=de.id ");
		sqlObject.setSql(sql.toString());

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status_", rs[i++]);
				map.put("trigger_time", rs[i++]);
				map.put("code", rs[i++]);
				map.put("device_name", rs[i++]);
				map.put("purpose", rs[i++]);
				map.put("type_", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("de.id", "id"));
		columns.add(new TextColumn4MapKey("en.id", "status_",
				getText("deviceEvent.status"), 60)
				.setSortable(true)
				.setValueFormater(
						new EntityStatusFormater(getDeviceEventStatus()))
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("de.trigger_time", "trigger_time",
				getText("deviceEvent.triggerTime"), 150)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm:ss"))
				.setSortable(true).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("d.code", "code",
				getText("device.code"), 70).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("d.name", "device_name",
				getText("deviceEvent.name"), 110).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("d.purpose", "purpose",
				getText("deviceEvent.purpose")).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("de.type_", "type_",
				getText("deviceEvent.type"), 120).setSortable(true)
				.setUseTitleFromLabel(true));
		return columns;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['type_']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1 && ss[0].equals("1")) {
				statusCondition = new IsNotNullCondition("en.id");
			} else if (ss.length == 1 && ss[0].equals("0")) {
				statusCondition = new IsNullCondition("en.id");
			} else {
				;
			}
		} else {
			return null;
		}
		return statusCondition;
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "d.name", "de.type_", "d.code", "d.purpose" };
	}

	@Override
	protected String getGridDblRowMethod() {
		return null;
	}

	@Override
	protected String getModuleContextPath() {
		return this.getContextPath() + "/bc";
	}

	@Override
	protected String getFormActionName() {
		return "deviceEvent";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(730).setHeight(300)
				.setMinWidth(300).setMinHeight(200);
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar t = new Toolbar();
		if (!isReadonly()) {
			t.addButton(Toolbar.getDefaultToolbarRadioGroup(
					getDeviceEventStatus(), "status", 0,
					getText("title.click2changeSearchStatus")));
		} else {
			t.addButton(Toolbar.getDefaultEmptyToolbarButton());
		}
		t.addButton(getDefaultSearchToolbarButton());
		return t;
	}

	private Map<String, String> getDeviceEventStatus() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		// 已处理
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("device.status.handled"));
		// 未处理
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
				getText("device.status.unhandled"));
		// 全部
		statuses.put("", getText("bc.status.all"));
		return statuses;
	}
}
