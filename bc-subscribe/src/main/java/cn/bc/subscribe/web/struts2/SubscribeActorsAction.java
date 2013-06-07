package cn.bc.subscribe.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.subscribe.domain.SubscribeActor;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.json.Json;

/**
 * 订阅人视图Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SubscribeActorsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(SubscribeActorsAction.class);
	
	public Integer sid;//订阅id

	
	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：订阅管理角色或系统管理员
		return !context.hasAnyRole(getText("key.role.bc.subscribe"),
				getText("key.role.bc.admin"));
	}
	
	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("e.file_date",Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select s.id,s.status_,s.type_,s.order_,s.subject,s.event_code,e.type_ actor_type,e.file_date");
		sql.append(",e.aid,f.name,f.pname");
		sql.append(" from bc_subscribe s");
		sql.append(" inner join bc_subscribe_actor e on e.pid=s.id");
		sql.append(" inner join bc_identity_actor f on f.id=e.aid");
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
				map.put("type", rs[i++]);
				map.put("orderNo", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("eventCode", rs[i++]);
				map.put("actorType", rs[i++]);
				map.put("fileDate", rs[i++]);
				map.put("aid", rs[i++]);
				map.put("name", rs[i++]);
				map.put("pname", rs[i++]);//上级机构
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("s.id", "id"));
		
		if( sid!=null ){
			columns.add(new TextColumn4MapKey("e.type_", "actorType",
					getText("subscribe.type"), 60)
					.setUseTitleFromLabel(true)
					.setValueFormater(new KeyValueFormater(getTypes())));
			columns.add(new TextColumn4MapKey("a.pname", "pname",
					getText("subscribeActor.pname")).setSortable(true)
					.setUseTitleFromLabel(true));
			columns.add(new TextColumn4MapKey("f.name", "name",
					getText("subscribeActor.name"),120)
					.setUseTitleFromLabel(true));
		}else{
			columns.add(new TextColumn4MapKey("s.status_", "status",
					getText("subscribe.status"), 40)
					.setUseTitleFromLabel(true)
					.setValueFormater(new KeyValueFormater(getStatuses())));
			columns.add(new TextColumn4MapKey("e.type_", "actorType",
					getText("subscribe.type"), 60)
					.setUseTitleFromLabel(true)
					.setValueFormater(new KeyValueFormater(getTypes())));
			columns.add(new TextColumn4MapKey("s.subject", "subject",
					getText("subscribe.subject"))
					.setUseTitleFromLabel(true));
			columns.add(new TextColumn4MapKey("a.pname", "pname",
					getText("subscribeActor.pname")).setSortable(true)
					.setUseTitleFromLabel(true));
			columns.add(new TextColumn4MapKey("f.name", "name",
					getText("subscribeActor.name"),120)
					.setUseTitleFromLabel(true));
		}
		
		
		columns.add(new TextColumn4MapKey("e.file_date", "fileDate",
				getText("subscribe.date"), 120)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new HiddenColumn4MapKey("subject", "subject"));
		columns.add(new HiddenColumn4MapKey("aid", "aid"));
		return columns;
	}
	
	/**
	 * 状态值转换列表：草稿|正常|禁用|全部
	 * 
	 * @return
	 */
	private Map<String, String> getStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_DRAFT),
				getText("subscribe.status.draft"));
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("subscribe.status.enabled"));
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
				getText("subscribe.status.disable"));
		statuses.put("", getText("bc.status.all"));
		return statuses;
	}
	
	private Map<String, String> getTypes() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(SubscribeActor.TYPE_ACTIVE),
				getText("subscribePersonal.active"));
		statuses.put(String.valueOf(SubscribeActor.TYPE_PASSIVE),
				getText("subscribePersonal.passive"));
		statuses.put("", getText("bc.status.all"));
		return statuses;
	}


	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']+'的订阅配置'";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { 
				"f.name"
				,"s.order_"
				,"s.subject"
				,"s.event_code"};
	}
	
	@Override
	protected String getGridDblRowMethod() {
		return null;
	}

	@Override
	protected String getFormActionName() {
		return "subscribeActor";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setMinWidth(200).setMinHeight(300);
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		if(!this.isReadonly() && this.sid != null){
			// 添加
			tb.addButton(new ToolbarButton().setIcon("ui-icon-person")
					.setText(getText("subscribeActor.add.actor"))
					.setClick("bc.subscribeActorView.selectUser"));
			tb.addButton(new ToolbarButton().setIcon("ui-icon-contact")
					.setText(getText("subscribeActor.add.group"))
					.setClick("bc.subscribeActorView.selectGroup"));
			tb.addButton(new ToolbarButton().setIcon("ui-icon-home")
					.setText(getText("subscribeActor.add.unitAndDepartment"))
					.setClick("bc.subscribeActorView.selectUnitOrDepartment"));
			
			// 删除
			tb.addButton(new ToolbarButton().setIcon("ui-icon-trash")
					.setText(getText("label.delete"))
					.setClick("bc.subscribeActorView._delete"));
		}
		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}
	
	@Override
	protected Condition getGridSpecalCondition() {
		AndCondition ac = new AndCondition();
	
		if(this.sid != null){
			ac.add(new EqualsCondition("s.id",sid));
		}

		return ac.isEmpty()?null:ac;
	}
	
	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();

		if(sid != null){
			json.put("sid", sid);
		}
		
		return json.isEmpty()?null:json;
	}
	
	

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/subscribe/actor/view.js"
				+","+this.getHtmlPageNamespace() + "/subscribe/subscribe.js";
	}

}
