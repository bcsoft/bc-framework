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
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.email.domain.EmailTrash;
import cn.bc.identity.web.SystemContext;
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

/**
 * 邮件垃圾箱视图Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class EmailTrashsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(EmailTrashsAction.class);


	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("e.send_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select t.id,t.src,t.handle_date,e.subject,e.id emailid,e.send_date");
		sql.append(" from bc_email_trash t");
		sql.append(" inner join bc_email e on e.id=t.pid");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("source", rs[i++]);
				map.put("handleDate", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("emailId", rs[i++]);
				map.put("sendDate", rs[i++]);
				map.put("openType", 3);//查看邮件 1-发件箱查看，2-收件箱查看，3-垃圾箱查看
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("t.id", "id"));
		columns.add(new TextColumn4MapKey("t.src", "source",
				getText("email.source"), 40)
				.setUseTitleFromLabel(true)
				.setValueFormater(new KeyValueFormater(getStatuses())));
		columns.add(new TextColumn4MapKey("e.subject", "subject",
				getText("email.subject")).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.handle_date", "handleDate",
				getText("email.date"), 90)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new HiddenColumn4MapKey("emailId", "emailId"));
		columns.add(new HiddenColumn4MapKey("source", "source"));
		columns.add(new HiddenColumn4MapKey("openType", "openType"));
		columns.add(new HiddenColumn4MapKey("handleDate", "handleDate"));
		return columns;
	}
	
	/**
	 * 状态值转换列表：收件箱|发件箱
	 * 
	 * @return
	 */
	private Map<String, String> getStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(EmailTrash.SOURCE_SEND),
				getText("emailSend"));
		statuses.put(String.valueOf(EmailTrash.SOURCE_TO),
				getText("emailTo"));
		statuses.put("", getText("bc.status.all"));
		return statuses;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "e.subject" };
	}

	@Override
	protected String getFormActionName() {
		return "emailTrash";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(550).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		// 查看按钮
		tb.addButton(Toolbar.getDefaultOpenToolbarButton(getText("label.read")).setClick("bc.emailViewBase.open").setAction(null));

		//还原
		tb.addButton(new ToolbarButton().setIcon("ui-icon-pencil")
				.setText(getText("emailTrash.restore"))
				.setClick("bc.emailTrashView.restore"));
		//删除
		tb.addButton(new ToolbarButton().setIcon("ui-icon-closethick")
				.setText(getText("label.delete"))
				.setClick("bc.emailTrashView._delete"));
		//清空
		tb.addButton(new ToolbarButton().setIcon("ui-icon-alert")
				.setText(getText("emailTrash.clear"))
				.setClick("bc.emailTrashView.clear"));

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getModuleContextPath() + "/email/trash/view.js" + ","
				+ this.getModuleContextPath() + "/email/view.js";
	}
	
	@Override
	protected String getGridDblRowMethod() {
		return "bc.emailViewBase.open";
	}
	
	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		AndCondition ac = new AndCondition();

		SystemContext context = (SystemContext) this.getContext();

		//状态必须为可恢复
		ac.add(new EqualsCondition("t.status_", EmailTrash.STATUS_RESUMABLE));
		ac.add(new EqualsCondition("t.owner_id", context.getUser().getId()));
		
		return ac;
	}

	
	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}

	// ==高级搜索代码结束==

}
