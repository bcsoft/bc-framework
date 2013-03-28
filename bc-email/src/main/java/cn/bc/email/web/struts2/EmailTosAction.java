package cn.bc.email.web.struts2;

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

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.query.condition.impl.QlCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.email.domain.Email;
import cn.bc.email.domain.EmailTrash;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.html.toolbar.ToolbarMenuButton;
import cn.bc.web.ui.json.Json;

/**
 * 邮件收件箱视图Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class EmailTosAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(EmailTosAction.class);
	
	public Boolean read;

	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("e.send_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select e.id,e.subject,e.send_date,t.id to_id,t.read_,a.name");
		sql.append(" from bc_email_to t");
		sql.append(" inner join bc_email e on e.id=t.pid");
		sql.append(" inner join bc_identity_actor a on a.id=e.sender_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("sendDate", rs[i++]);
				map.put("toId", rs[i++]);
				map.put("read", rs[i++]);
				map.put("name", rs[i++]);
				map.put("source", EmailTrash.SOURCE_TO);
				map.put("openType", 2);//查看邮件 1-发件箱查看，2-收件箱查看，3-垃圾箱查看
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("a.id", "id"));
		columns.add(new TextColumn4MapKey("t.read_", "read",
				getText("email.status"), 35).setSortable(true)
				.setValueFormater(new BooleanFormater() {
					@Override
					public String format(Object context, Object value) {
						if (value == null)
							return null;
						if (value instanceof Boolean)
							return ((Boolean) value).booleanValue() ? getText("email.status.read")
									: getText("email.status.unread");
						else if (value instanceof String)
							return "true".equalsIgnoreCase((String) value) ? getText("email.status.read")
									: getText("email.status.unread");
						else
							return value.toString();
					}
				}));
		columns.add(new TextColumn4MapKey("a.name", "name",
				getText("email.sender"), 60).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("e.subject", "subject",
				getText("email.subject")).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("e.send_date", "sendDate",
				getText("email.date"), 120)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new HiddenColumn4MapKey("source", "source"));
		columns.add(new HiddenColumn4MapKey("openType", "openType"));
		columns.add(new HiddenColumn4MapKey("read", "read"));
		return columns;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "e.subject", "a.name" };
	}

	@Override
	protected String getFormActionName() {
		return "emailTo";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(600).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		// 写邮件
		tb.addButton(new ToolbarButton().setIcon("ui-icon-pencil")
				.setText(getText("email.write"))
				.setClick("bc.emailViewBase.writeEmail"));
		
		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(this.getStatuses(),
				"read",2,
				getText("title.click2changeSearchStatus")));

		// "更多"按钮
		ToolbarMenuButton menuButton = new ToolbarMenuButton(
				getText("label.operate"))
				.setChange("bc.emailToView.selectMenuButtonItem");
		
		tb.addButton(menuButton);
		
		// --查看垃圾箱
		menuButton.addMenuItem("查看"+getText("emailTrash"),"emailTrash");
		// --标记为已读
		menuButton.addMenuItem(getText("email.mark.read"),"markRead");
		// --标记为未读
		menuButton.addMenuItem(getText("email.mark.unread"),"markUnread");
		// --全部标记为已读
		menuButton.addMenuItem("全部"+getText("email.mark.read"),"markReadAll");
		// --回复
		menuButton.addMenuItem(getText("email.reply"),"reply");
		// --转发
		menuButton.addMenuItem(getText("email.forwoard"),"forwoard");
		// --移至垃圾箱
		menuButton.addMenuItem(getText("email.moveTrash"),"moveTrash");
		// --彻底删除
		menuButton.addMenuItem(getText("email.shiftDelete"),"shiftDelete");

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}
	
	/**
	 * 状态值转换列表：已读|未读
	 * 
	 * @return
	 */
	private Map<String, String> getStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(true),
				getText("email.status.read"));
		statuses.put(String.valueOf(false),
				getText("email.status.unread"));
		statuses.put("", getText("bc.status.all"));
		return statuses;
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/email/to/view.js" + ","
				+ this.getHtmlPageNamespace() + "/email/view.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		AndCondition ac = new AndCondition();
		
		if(this.read!=null&&this.read){
			ac.add(new EqualsCondition("t.read_", this.read));
		}else if(this.read!=null&&!this.read){
			ac.add(new EqualsCondition("t.read_", this.read));
		}
		

		SystemContext context = (SystemContext) this.getContext();

		//状态必须为已发邮件
		ac.add(new EqualsCondition("e.status_", Email.STATUS_SENDED));
		ac.add(new EqualsCondition("t.receiver_id", context.getUser().getId()));

		//去掉垃圾箱已关联的此邮件
		String sql="not exists(select 1 from bc_email_trash t_e where t_e.pid=e.id";
		sql+=" and t_e.owner_id=t.receiver_id and t_e.src="+EmailTrash.SOURCE_TO+")";
		ac.add(new QlCondition(sql));
		
		return ac;
	}
	
	@Override
	protected Json getGridExtrasData() {
		Json json = new Json();
		// 状态条件
		if (read != null)
			json.put("read", read);
		
		return json;
	}

	@Override
	protected String getGridDblRowMethod() {
		return "bc.emailViewBase.open";
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}
	// ==高级搜索代码结束==

}
