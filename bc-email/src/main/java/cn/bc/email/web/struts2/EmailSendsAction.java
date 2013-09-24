package cn.bc.email.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
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
import cn.bc.core.util.JsonUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.email.domain.EmailTrash;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;

/**
 * 邮件发件箱视图Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class EmailSendsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(EmailSendsAction.class);

	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("e.send_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select e.id,e.status_,e.subject,e.send_date,getemailreceiver2json(e.id)");
		sql.append(" from bc_email e");
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
				map.put("status", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("sendDate", rs[i++]);
				map.put("receiver", rs[i++]);
				map.put("source", EmailTrash.SOURCE_SEND);
				map.put("openType", 1);//查看邮件 1-发件箱查看，2-收件箱查看，3-垃圾箱查看
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("e.id", "id"));
		columns.add(new TextColumn4MapKey("", "receiver",
				getText("email.receiver"), 150).setUseTitleFromLabel(true)
				.setValueFormater(new AbstractFormater<String>() {

					@Override
					public String format(Object context, Object value) {
						if (value == null)return null;
						Map<String,Object> map=JsonUtils.toMap(value.toString());
						String receiver="";
						if(map.get("receiver").toString().length()>0)
							receiver=map.get("receiver").toString()+"; ";
						
						if(map.get("cc").toString().length()>0)
							receiver+=getText("email.cc")+"--["+map.get("cc").toString()+"]; ";
						
						if(map.get("bcc").toString().length()>0)
							receiver+=getText("email.bcc")+"--["+map.get("bcc").toString()+"]; ";
							
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
		columns.add(new HiddenColumn4MapKey("source", "source"));
		columns.add(new HiddenColumn4MapKey("openType", "openType"));
		return columns;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "e.subject"};
	}

	@Override
	protected String getFormActionName() {
		return "emailSend";
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
				.setClick("bc.emailViewBase.writeEmail")
				.addClazz("bc-email-vriteEmail-send"));
		
		//转发
		tb.addButton(new ToolbarButton().setIcon("ui-icon-arrowthick-1-e")
				.setText(getText("email.forwoard"))
				.setClick("bc.emailViewBase.forwoard"));
		
		//移至垃圾箱
		tb.addButton(new ToolbarButton().setIcon("ui-icon-trash")
				.setText(getText("email.moveTrash"))
				.setClick("bc.emailViewBase.moveTrash"));
		
		//彻底删除
		tb.addButton(new ToolbarButton().setIcon("ui-icon-closethick")
				.setText(getText("email.shiftDelete"))
				.setClick("bc.emailViewBase.shiftDelete"));

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getModuleContextPath() + "/email/send/view.js" + ","
				+ this.getModuleContextPath() + "/email/view.js";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		AndCondition ac = new AndCondition();

		SystemContext context = (SystemContext) this.getContext();

		ac.add(new EqualsCondition("e.sender_id", context.getUser().getId()));
		
		//去掉垃圾箱已关联的此邮件
		String sql="not exists(select 1 from bc_email_trash t_e where t_e.pid=e.id";
		sql+=" and t_e.owner_id=e.sender_id and t_e.src="+EmailTrash.SOURCE_SEND+")";
		ac.add(new QlCondition(sql));

		return ac;
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
