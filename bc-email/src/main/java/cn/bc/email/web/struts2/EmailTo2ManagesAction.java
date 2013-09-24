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
import org.springframework.util.Assert;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.email.domain.Email;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;

/**
 * 收件管理视图Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class EmailTo2ManagesAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(EmailTo2ManagesAction.class);
	

	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("e.send_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>() {
			// 自己根据条件构建实际的sql
			@Override
			public String getSql(Condition condition) {
				Assert.notNull(condition);
				return getSelect() + " " + getFromWhereSql(condition);
			}

			@Override
			public String getFromWhereSql(Condition condition) {
				Assert.notNull(condition);
				String expression = condition.getExpression();
				if (!expression.startsWith("order by")) {
					expression = "where " + expression;
				}
				return getFrom().replace("${condition}", expression);
			}
		};
		
		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("select t2.*,getemailreceiverreadcount(t2.id,t2.code)");

		StringBuffer fromSql = new StringBuffer();
		
		fromSql.append(" from (select e.id,e.subject,e.send_date,t.id to_id,t.read_,a.name,a2.name receiver,a2.code,a3.name upper,e.type_");
		fromSql.append(" from bc_email_to t");
		fromSql.append(" inner join bc_email e on e.id=t.pid");
		fromSql.append(" inner join bc_identity_actor a on a.id=e.sender_id");
		fromSql.append(" inner join bc_identity_actor a2 on a2.id=t.receiver_id");
		fromSql.append(" left join bc_identity_actor a3 on a3.id=t.upper_id");
		fromSql.append(" ${condition} ) t2");
		
		sqlObject.setSelect(selectSql.toString());
		sqlObject.setFrom(fromSql.toString());

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
				map.put("receiver", rs[i++]);
				map.put("code", rs[i++]);
				map.put("upper", rs[i++]);
				map.put("type", rs[i++]);
				map.put("receiverReadCount", rs[i++]);//邮件查阅次数
				return map;
			}
		});
		return sqlObject;
	}
	
	
	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("e.id", "id"));
		columns.add(new TextColumn4MapKey("e.type_", "type",
				getText("email.type"), 60).setSortable(true)
				.setValueFormater(new KeyValueFormater(this.getTypes())));
		columns.add(new TextColumn4MapKey("", "receiverReadCount",
				getText("emailHistory2Manage.count"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("a2.name", "receiver",
				getText("email.receiver"), 100).setUseTitleFromLabel(true)
				.setValueFormater(new AbstractFormater<String>() {

					@SuppressWarnings("unchecked")
					@Override
					public String format(Object context, Object value) {
						Map<String, Object> map=(Map<String, Object>) context;
						if(map.get("upper")==null||"".equals(map.get("upper").toString())){
							return value.toString();
						}
						
						return value.toString()+"["+map.get("upper").toString()+"]";
					}
				}));
		columns.add(new TextColumn4MapKey("a.name", "name",
				getText("email.sender"), 60).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("e.subject", "subject",
				getText("email.subject")).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("e.send_date", "sendDate",
				getText("email.date"), 120)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("t.read_", "read",
				getText("email.mark.status"), 80).setSortable(true)
				.setValueFormater(new KeyValueFormater(this.getMarkStatuses())));
		return columns;
	}

	private Map<String, Object> getTypes() {
		Map<String, Object> types = new LinkedHashMap<String, Object>();
		types.put(String.valueOf(Email.TYPE_NEW),
				getText("email.new"));
		types.put(String.valueOf(Email.TYPE_REPLY),
				getText("email.reply"));
		types.put(String.valueOf(Email.TYPE_FORWARD), 
				getText("email.forwoard"));
		return types;
	}
	
	/**
	 * 状态值转换列表：标记已读|标记未读
	 * 
	 * @return
	 */
	private Map<String, Object> getMarkStatuses() {
		Map<String, Object> statuses = new LinkedHashMap<String, Object>();
		statuses.put(String.valueOf(false),
				getText("email.mark.unread"));
		statuses.put(String.valueOf(true),
				getText("email.mark.read"));
		statuses.put("", getText("bc.status.all"));
		return statuses;
	}
	
	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "e.subject", "a.name","a2.name","a3.name" };
	}

	@Override
	protected String getFormActionName() {
		return "emailTo2Manage";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(650).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();
		
		
		
		tb.addButton(new ToolbarButton().setIcon("ui-icon-check")
				.setText(getText("label.read"))
				.setClick("bc.email2ManageViewBase.open"));

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}
	
	

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/email/manage/view.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		AndCondition ac = new AndCondition();

		//状态必须为已发邮件
		ac.add(new EqualsCondition("e.status_", Email.STATUS_SENDED));
		return ac;
	}

	@Override
	protected String getGridDblRowMethod() {
		return "bc.email2ManageViewBase.open";
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}
	// ==高级搜索代码结束==

}
