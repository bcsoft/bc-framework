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
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.subscribe.domain.Subscribe;
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
 * 订阅监控视图Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SubscribesAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(SubscribesAction.class);

	public String status;
	
	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：订阅管理角色或系统管理员
		return !context.hasAnyRole(getText("key.role.bc.subscribe"),
				getText("key.role.bc.admin"));
	}
	
	public boolean isAccessControl() {
		// 流程访问控制
		SystemContext context = (SystemContext) this.getContext();
		return context
				.hasAnyRole(getText("key.role.bc.workflow.accessControl"));
	}


	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("s.file_date", Direction.Desc).add(
				"s.modified_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select s.id,s.status_,s.type_,s.order_,s.subject,s.event_code,e.actor_name as author,s.file_date");
		sql.append(",f.actor_name as modifier,s.modified_date");
		sql.append(",getaccessactors4docidtype4docidinteger(s.id,'Subscribe')");
		sql.append(" from bc_subscribe s");
		sql.append(" inner join bc_identity_actor_history e on e.id=s.author_id");
		sql.append(" left join bc_identity_actor_history f on f.id=s.modifier_id");
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
				map.put("author", rs[i++]);
				map.put("fileDate", rs[i++]);
				map.put("modifier", rs[i++]);
				map.put("modifiedDate", rs[i++]);
				map.put("accessactors", rs[i++]);
				map.put("accessControlDocType","Subscribe");
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("s.id", "id"));
		columns.add(new TextColumn4MapKey("s.status_", "status",
				getText("subscribe.status"), 40)
				.setUseTitleFromLabel(true)
				.setValueFormater(new KeyValueFormater(getStatuses())));
		/*columns.add(new TextColumn4MapKey("s.type_", "type",
				getText("subscribe.type"), 40)
				.setUseTitleFromLabel(true)
				.setValueFormater(new KeyValueFormater(getTypes())));*/
		columns.add(new TextColumn4MapKey("s.subject", "subject",
				getText("subscribe.subject"))
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("s.event_code", "eventCode",
				getText("subscribe.eventCode"), 120)
				.setUseTitleFromLabel(true));
		if(this.isAccessControl()){
			columns.add(new TextColumn4MapKey("", "accessactors",
					getText("subscribe.accessActorAndRole"),150).setSortable(true)
					.setUseTitleFromLabel(true));
		}
		columns.add(new TextColumn4MapKey("e.actor_name", "author",
				getText("subscribe.author"), 60).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("s.file_date", "fileDate",
				getText("subscribe.fileDate"), 90)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("f.actor_name", "modifier",
				getText("subscribe.modifier"), 60)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("s.modified_date", "modifiedDate",
				getText("subscribe.modifiedDate"), 90)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new HiddenColumn4MapKey("accessControlDocType","accessControlDocType"));
		columns.add(new HiddenColumn4MapKey("accessControlDocName", "subject"));
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
		statuses.put(String.valueOf(Subscribe.TYPE_EMAIL),
				getText("subscribe.type.email"));
		statuses.put(String.valueOf(Subscribe.TYPE_SMS),
				getText("subscribe.type.sms"));
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
				"s.order_"
				,"s.subject"
				,"s.event_code"
				,"e.name"
				,"name"};
	}

	@Override
	protected String getFormActionName() {
		return "subscribe";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		if (!this.isReadonly()) {
			// 新建按钮
			tb.addButton(this.getDefaultCreateToolbarButton());
			// 编辑按钮
			tb.addButton(this.getDefaultEditToolbarButton());
			// 发布
			tb.addButton(new ToolbarButton().setIcon("ui-icon-person")
					.setText(getText("subscribe.release"))
					.setClick("bc.subscribeView.release"));
			// 停用
			tb.addButton(new ToolbarButton().setIcon("ui-icon-cancel")
					.setText(getText("subscribe.stop"))
					.setClick("bc.subscribeView.stop"));
		} else {
			// 查看
			tb.addButton(this.getDefaultOpenToolbarButton());
		}
		
		if(this.isAccessControl()){
			// 访问监控
			tb.addButton(new ToolbarButton().setIcon("ui-icon-wrench")
					.setText(getText("subscribe.accessControl"))
					.setClick("bc.accessControl"));
		}

		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(this.getStatuses(),
				"status",3,
				getText("title.click2changeSearchStatus")));
		

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}
	
	@Override
	protected Condition getGridSpecalCondition() {
		AndCondition andCondition = new AndCondition();
		if(status !=null && status.length()>0){
			String[] ss = status.split(",");
			if (ss.length == 1) {
				andCondition.add(new EqualsCondition("s.status_", new Integer(
						ss[0])));
			} else {
				andCondition.add(new InCondition("s.status_",
						StringUtils.stringArray2IntegerArray(ss)));
			}
		}
		
		return andCondition.isEmpty()?null:andCondition;
	}
	
	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		json.put("status", status);
		return status==null||status.length()==0?null:json;
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/subscribe/view.js"
				+","+this.getContextPath()+"/bc/acl/accessControl.js";
	}
	

}
