package cn.bc.placeorigin.web.struts2;

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
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.placeorigin.domain.PlaceOrigin;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 籍贯视图Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class PlaceOriginsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED);

	// 权限
	@Override
	public boolean isReadonly() {
		// 系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.admin")
				,getText("key.role.bc.placeorigin"));
	}

	@Override
	protected OrderCondition getGridDefaultOrderCondition() {
		// 默认的排序方法
		return new OrderCondition("a.code", Direction.Asc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id as id,a.code as code, a.type_ as type,a.status_ as status");
		sql.append(",a.name as name,a.full_name as fullname,a.full_code as fullcode");
		sql.append(",p.name as pname,a.desc_ as desc");
		sql.append(" from bc_placeorigin a");
		sql.append(" left join bc_placeorigin p on p.id=a.pid");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("code", rs[i++]);
				map.put("type", rs[i++]);
				map.put("status", rs[i++]);
				map.put("name", rs[i++]);
				map.put("fullname", rs[i++]);
				map.put("fullcode", rs[i++]);
				map.put("pname", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("a.id", "id"));
		if (!this.isReadonly()) {
			// 状态
			columns.add(new TextColumn4MapKey("a.status_", "status",
					getText("placeorigin.status"), 40).setSortable(true)
					.setValueFormater(new KeyValueFormater(this.getStatuses())));
		}
		// 类型
		columns.add(new TextColumn4MapKey("a.type_", "type",
				getText("placeorigin.type"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(this.getTypes())));
		// 上级
		columns.add(new TextColumn4MapKey("p.name", "pname",
				getText("placeorigin.higherlevel"), 100)
				.setUseTitleFromLabel(true));
		// 编码
		columns.add(new TextColumn4MapKey("a.code", "code",
				getText("placeorigin.code"), 60).setUseTitleFromLabel(true));
		// 名称
		columns.add(new TextColumn4MapKey("a.name", "name",
				getText("placeorigin.name"), 100).setUseTitleFromLabel(true));
		// 全名
		columns.add(new TextColumn4MapKey("a.full_name", "fullname",
				getText("placeorigin.fullname"), 200)
				.setUseTitleFromLabel(true));
		// 全编码
		columns.add(new TextColumn4MapKey("a.full_code", "fullcode",
				getText("placeorigin.fullcode"), 140)
				.setUseTitleFromLabel(true));
		// 备注
		columns.add(new TextColumn4MapKey("a.desc_", "desc",
				getText("placeorigin.desc")).setUseTitleFromLabel(true));
		return columns;
	}

	// 状态值转换
	private Map<String, String> getStatuses() {
		Map<String, String> mstatus = new LinkedHashMap<String, String>();
		mstatus.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("placeorigin.status.enabled"));
		mstatus.put(String.valueOf(BCConstants.STATUS_DISABLED),
				getText("placeorigin.status.disabled"));
		mstatus.put("", getText("placeorigin.status.all"));
		return mstatus;
	}

	// 类型值转换
	private Map<String, String> getTypes() {
		Map<String, String> mstatus = new HashMap<String, String>();
		mstatus.put(String.valueOf(PlaceOrigin.TYPE_COUNTRY_LEVEL),
				getText("placeorigin.type.contry"));
		mstatus.put(String.valueOf(PlaceOrigin.TYPE_PROVINCE_LEVEL),
				getText("placeorigin.type.province"));
		mstatus.put(String.valueOf(PlaceOrigin.TYPE_PLACE_LEVEL),
				getText("placeorigin.type.place"));
		mstatus.put(String.valueOf(PlaceOrigin.TYPE_COUNTY_LEVEL),
				getText("placeorigin.type.county"));
		mstatus.put(String.valueOf(PlaceOrigin.TYPE_TOWNSHIP_LEVEL),
				getText("placeorigin.type.township"));
		mstatus.put(String.valueOf(PlaceOrigin.TYPE_VILLAGE_LEVEL),
				getText("placeorigin.type.village"));
		return mstatus;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "a.code","a.name", "a.full_name","a.full_code" };
	}

	@Override
	protected String getFormActionName() {
		return "placeOrigin";
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

			// 禁用按钮
			tb.addButton(this.getDefaultDisabledToolbarButton());
		}

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());
		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(this.getStatuses(),
				"status", 0, getText("title.click2changeSearchStatus")));

		return tb;
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(450)
				.setHeight(400).setMinHeight(200);
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("a.status_", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("a.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		}

		return statusCondition;
	}

	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		// 状态条件
		if (this.status != null || this.status.length() != 0) {
			json.put("status", status);
		}
		return json.isEmpty() ? null : json;
	}

}
