package cn.bc.subscribe.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.NotEqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
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
 * 我的订阅视图Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SubscribePersonalsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(SubscribePersonalsAction.class);

	private ActorService actorService;
	
	@Autowired
	public void setActorService(ActorService actorService) {
		this.actorService = actorService;
	}
	
	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("e.file_date");
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select s.id,s.status_,s.type_,s.order_,s.subject,s.event_code,e.type_ actor_type,e.file_date");
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
		columns.add(new TextColumn4MapKey("e.type_", "actorType",
				getText("subscribe.type"), 60)
				.setUseTitleFromLabel(true)
				.setValueFormater(new KeyValueFormater(getTypes())));
		columns.add(new TextColumn4MapKey("s.subject", "subject",
				getText("subscribe.subject"))
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("e.file_date", "fileDate",
				getText("subscribe.date"), 90)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new HiddenColumn4MapKey("subject", "subject"));
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
				"s.order_"
				,"s.subject"
				,"s.event_code"};
	}
	
	@Override
	protected String getGridDblRowMethod() {
		return null;
	}

	@Override
	protected String getFormActionName() {
		return "subscribePersonal";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(450).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		// 添加
		tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
				.setText(getText("subscribePersonal.add"))
				.setClick("bc.subscribePersonalView.create"));
		// 删除
		tb.addButton(new ToolbarButton().setIcon("ui-icon-trash")
				.setText(getText("label.delete"))
				.setClick("bc.subscribePersonalView.delete_"));

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}
	
	@Override
	protected Condition getGridSpecalCondition() {
		AndCondition ac = new AndCondition();
		// 查找当前登录用户条件
		SystemContext context = (SystemContext) this.getContext();
		//当前用户
		Actor actor=context.getUser();
		
		//定义id集合
		List<Long> ids=new ArrayList<Long>();
		
		//查找属于当前用户
		ids.add(actor.getId());
		
		//查找拥有的上级订阅
		List<Actor> uppers=this.actorService.findAncestorOrganization(actor.getId(),
				new Integer[] { Actor.TYPE_GROUP,Actor.TYPE_DEPARTMENT,Actor.TYPE_UNIT});
		
		for(Actor upper:uppers){
			ids.add(upper.getId());
		}
		
		//使用in条件
		ac.add(new InCondition("e.aid",ids));
		
		//不显示草稿的
		ac.add(new NotEqualsCondition("s.status_",-1));

		return ac;
	}
	
	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();

		return json.isEmpty()?null:json;
	}
	
	

	@Override
	protected String getHtmlPageJs() {
		return this.getModuleContextPath() + "/subscribe/personal/view.js"
				+","+this.getModuleContextPath() + "/subscribe/subscribe.js";
	}

}
