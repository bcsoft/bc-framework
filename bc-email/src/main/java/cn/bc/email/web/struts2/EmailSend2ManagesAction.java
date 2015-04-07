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

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.JsonUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.email.domain.Email;
import cn.bc.identity.web.SystemContext;
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
 * 发件管理视图Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class EmailSend2ManagesAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(EmailSend2ManagesAction.class);

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：电子邮件管理角色或系统管理员
		return !context.hasAnyRole(getText("key.role.bc.email.manage"),
				getText("key.role.bc.admin"));
	}
	
	
	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("e.send_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

        //region 构建查询语句
		StringBuffer sql = new StringBuffer();
		sql.append("select e.id,e.status_,e.subject,e.send_date,a.name,e.type_,v.type_str,v.receiver_str");
		sql.append(" from bc_email e");
        sql.append(" inner join bc_identity_actor a on a.id=e.sender_id");
        sql.append(" inner join view_bc_email_to_receiver_info v on v.id = e.id");// 邮件收件箱收件人信息视图
		sqlObject.setSql(sql.toString());
        //endregion

		// 注入参数
		sqlObject.setArgs(null);

        //region 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("status", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("sendDate", rs[i++]);
				map.put("name", rs[i++]);
				map.put("type", rs[i++]);
				map.put("type_str", rs[i++]);
				map.put("receiver_str", rs[i++]);
				return map;
			}
		});
        //endregion
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("e.id", "id"));
		/*columns.add(new TextColumn4MapKey("e.status_", "status",
				getText("email.status"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(this.getStatuses())));*/
		columns.add(new TextColumn4MapKey("e.type_", "type",
				getText("email.type"), 60).setSortable(true)
				.setValueFormater(new KeyValueFormater(this.getTypes())));
		columns.add(new TextColumn4MapKey("a.name", "name",
				getText("email.sender"), 60).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("", "receiver_str",
				getText("email.receiver"), 150).setUseTitleFromLabel(true)
				.setValueFormater(new AbstractFormater<String>() {
					@Override
					public String format(Object context, Object value) {
						if (value == null)return null;

                        Map<String, Object> map = (Map<String, Object>) context;
                        String type_str = (String) map.get("type_str");
                        String[] types = type_str.split(",");
                        String[] receivers = String.valueOf(value).split(";");

                        String receiver = "";
                        for (int i = 0; i < types.length; i++) {
                            if("0".equals(types[i]))
                                receiver += receivers[i] + "; ";
                            if("1".equals(types[i]))
                                receiver += getText("email.cc") + "--[" + receivers[i] + "]; ";
                            if("2".equals(types[i]))
                                receiver += getText("email.bcc") + "--[" + receivers[i] + "]; ";
                        }

						if(receiver.length()>0)
						receiver=receiver.substring(0,receiver.lastIndexOf(";"));

						return receiver;
					}
				}));
		columns.add(new TextColumn4MapKey("e.subject", "subject",
				getText("email.subject")).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("e.send_date", "sendDate",
				getText("email.date"), 120)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		return columns;
	}
	
	/**
	 * 状态值转换列表：已读|未读
	 * 
	 * @return
	 */
	/*private Map<String, Object> getStatuses() {
		Map<String, Object> statuses = new LinkedHashMap<String, Object>();
		statuses.put(String.valueOf(Email.STATUS_DRAFT),
				getText("bc.status.draft"));
		statuses.put(String.valueOf(Email.STATUS_SENDED),
				getText("email.to"));
		statuses.put("", getText("bc.status.all"));
		return statuses;
	}*/
	
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

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "a.name","e.subject","v.receiver_str"};
	}

	@Override
	protected String getFormActionName() {
		return "emailSend2Manage";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(810).setMinWidth(400)
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
		return this.getModuleContextPath() + "/email/manage/view.js";
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
