package cn.bc.acl.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;

/**
 * 访问控制视图Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class AccessControlsAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(AccessControlsAction.class);

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：访问监控管理角色或系统管理员
		return !context.hasAnyRole(getText("key.role.bc.acl"),
				getText("key.role.bc.admin"));
	}

	public boolean isDelete() {
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：访问监控删除角色
		return context.hasAnyRole(getText("key.role.bc.acl.delete"));
	}

	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("b.file_date", Direction.Desc).add(
				"b.modified_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select b.id,b.doc_id,b.doc_type,b.doc_name,e.actor_name author,b.file_date,f.actor_name modifier,b.modified_date");
		sql.append(",getaccessactors4pid(b.id) accessactors");
		sql.append(" from bc_acl_doc b");
		sql.append(" inner join bc_identity_actor_history e on e.id=b.author_id");
		sql.append(" left join bc_identity_actor_history f on f.id=b.modifier_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("docId", rs[i++]);
				map.put("docType", rs[i++]);
				map.put("docName", rs[i++]);
				map.put("author", rs[i++]);
				map.put("fileDate", rs[i++]);
				map.put("modifier", rs[i++]);
				map.put("modifiedDate", rs[i++]);
				map.put("accessactors", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("a.id", "id"));
		columns.add(new TextColumn4MapKey("b.doc_name", "docName",
				getText("accessControl.docName"), 250)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.doc_type", "docType",
				getText("accessControl.docType"), 150)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.doc_id", "docId",
				getText("accessControl.docId"), 80).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("", "accessactors",
				getText("accessControl.accessActorAndRole")).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("e.actor_name", "author",
				getText("accessControl.author"), 60).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.file_date", "fileDate",
				getText("accessControl.fileDate"), 90)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		columns.add(new TextColumn4MapKey("f.actor_name", "modifier",
				getText("accessControl.modifier"), 60)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("b.modified_date", "modifiedDate",
				getText("accessControl.modifiedDate"), 90)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd")));
		return columns;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['docType']+'的监控配置'";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "b.doc_id", "b.doc_type", "b.doc_name", "c.name",
				"e.actor_name", "f.actor_name", "getaccessactors4pid(b.id)" };
	}

	@Override
	protected String getFormActionName() {
		return "accessControl";
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
		} else {
			// 查看
			tb.addButton(this.getDefaultOpenToolbarButton());
		}

		if (this.isDelete()) {
			// 删除按钮
			tb.addButton(new ToolbarButton().setIcon("ui-icon-trash")
					.setText(getText("label.delete"))
					.setClick("bc.accessControlView.deleteone"));
		}

		// 访问历史
		tb.addButton(new ToolbarButton().setIcon("ui-icon-search")
				.setText("查看" + getText("accessHistroy"))
				.setClick("bc.accessControlView.history"));

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getHtmlPageNamespace() + "/acl/control/view.js";
	}

	// ==高级搜索代码开始==
	@Override
	protected boolean useAdvanceSearch() {
		return true;
	}

	@Override
	protected void initConditionsFrom() throws Exception {

	}

	// ==高级搜索代码结束==

}
