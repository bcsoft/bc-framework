/**
 * 
 */
package cn.bc.log.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.log.domain.OperateLog;
import cn.bc.option.OptionConstants;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.json.Json;

/**
 * 操作日志视图Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class OperateLogsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String carId;// 车辆Id
	public String carManId;// 司机Id
	public String pid;//pid 模块Id
	public String module;// 所属模块
	public List<Map<String, String>> ptypeList; // 所属模块列表
	private OptionService optionService;

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

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
		columns.add(new TextColumn4MapKey("l.type_", "type",
				getText("operateLog.type"), 60).setSortable(true)
				.setValueFormater(new KeyValueFormater(getType())));
		columns.add(new TextColumn4MapKey("l.file_date", "file_date",
				getText("operateLog.fileDate"), 140).setSortable(true)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("l.subject", "subject",
				getText("operateLog.subject")).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("ac.actor_name", "author",
				getText("operateLog.author"), 80).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("l.way", "way",
				getText("operateLog.way"), 60).setSortable(true)
				.setValueFormater(new EntityStatusFormater(getWay())));
		columns.add(new TextColumn4MapKey("l.ptype", "ptype",
				getText("operateLog.ptype"), 100).setSortable(true)
				.setValueFormater(new KeyValueFormater(getPtype())));
		columns.add(new TextColumn4MapKey("l.pid", "pid",
				getText("operateLog.pid"), 80).setSortable(true)
				.setUseTitleFromLabel(true));

		return columns;
	}

	/**
	 * 日志类型转换列表：工作日志|审计日志
	 * 
	 * @return
	 */
	protected Map<String, String> getType() {
		Map<String, String> type = new LinkedHashMap<String, String>();
		type.put(String.valueOf(OperateLog.TYPE_AUDIT),
				getText("operateLog.type.audit"));
		type.put(String.valueOf(OperateLog.TYPE_WORK),
				getText("operateLog.type.work"));
		return type;
	}

	/**
	 * 创建方式转换列表：用户创建|自动生成
	 * 
	 * @return
	 */
	protected Map<String, String> getWay() {
		Map<String, String> way = new LinkedHashMap<String, String>();
		way.put(String.valueOf(OperateLog.WAY_SYSTEM),
				getText("operateLog.way.system"));
		way.put(String.valueOf(OperateLog.WAY_USER),
				getText("operateLog.way.user"));
		return way;
	}

	protected Map<String, String> getPtype() {
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] { OptionConstants.OPERATELOG_PTYPE });

		ptypeList = optionItems.get(OptionConstants.OPERATELOG_PTYPE);
		Map<String, String> ptype = new LinkedHashMap<String, String>();
		for (int i = 0; i < ptypeList.size(); i++) {
			ptype.put(ptypeList.get(i).get("key"), ptypeList.get(i)
					.get("value"));
		}
		return ptype;

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
		
		AndCondition ac=new AndCondition();
		
		// 模块条件
		if (this.module != null && this.module != "") {
			ac.add(new EqualsCondition("l.ptype", module));
		}
		// 车辆Id条件
		if (this.carId != null && this.carId != "") {
			ac.add(new EqualsCondition("l.pid", carId));
		}
		// 司机ID条件
		if (this.carManId != null && this.carManId != "") {
			ac.add(new EqualsCondition("l.pid", carManId));
		}
		
		if(this.pid != null && this.pid != ""){
			ac.add(new EqualsCondition("l.pid", pid));
		}

		return ac.isEmpty()?null:ac;
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
		
		if(this.pid != null && this.pid != ""){
			json.put("pid", pid);
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

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}

	public JSONArray createWay;// 创建方式
	public JSONArray logType;// 日志类型
	public JSONArray subordinateModule;// 所属模块

	@Override
	protected void initConditionsFrom() throws Exception {
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] { OptionConstants.OPERATELOG_PTYPE });
		// 所属模块
		subordinateModule = OptionItem.toLabelValues(optionItems
				.get(OptionConstants.OPERATELOG_PTYPE));
		// 可选日志类型列表
		logType = new JSONArray();
		Map<String, String> lt = getType();
		if (lt != null) {
			JSONObject json;
			Iterator<String> iterator = lt.keySet().iterator();
			String key;
			while (iterator.hasNext()) {
				key = iterator.next();
				json = new JSONObject();
				json.put("label", lt.get(key));
				json.put("value", key);
				logType.put(json);
			}
		}

		// 可选创建方式列表
		createWay = new JSONArray();
		Map<String, String> cw = getWay();
		if (cw != null) {
			JSONObject json;
			Iterator<String> iterator = cw.keySet().iterator();
			String key;
			while (iterator.hasNext()) {
				key = iterator.next();
				json = new JSONObject();
				json.put("label", cw.get(key));
				json.put("value", key);
				createWay.put(json);
			}
		}

	}

	// ==高级搜索代码结束==

}
