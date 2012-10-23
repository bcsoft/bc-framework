package cn.bc.netdisk.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.netdisk.domain.NetdiskFile;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 选择视图Action
 * 
 * @author zxr
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectFoldersAction extends AbstractSelectPageAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED);

	@Override
	public boolean isReadonly() {
		// 模板管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：模板管理员
		return !context.hasAnyRole(getText("key.role.bc.netdisk"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("f.status_").add("f.order_", Direction.Asc)
				.add("f.file_date", Direction.Asc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select f.id,f.status_,order_,name,size_,a.actor_name,file_date,modified_date");
		sql.append(",f.path from bc_netdisk_file f");
		sql.append(" inner join bc_identity_actor_history a on a.id=f.author_id");
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
				map.put("orderNo", rs[i++]);
				map.put("name", rs[i++]);
				map.put("size", rs[i++]);
				map.put("actor_name", rs[i++]);
				map.put("file_date", rs[i++]);
				map.put("modified_date", rs[i++]);
				map.put("path", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("f.id", "id"));
		columns.add(new TextColumn4MapKey("f.status_", "status",
				getText("netdisk.status"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(this.getStatuses())));
		columns.add(new TextColumn4MapKey("f.name", "name",
				getText("netdisk.name")).setUseTitleFromLabel(true));
		// columns.add(new TextColumn4MapKey("f.order_", "orderNo",
		// getText("netdisk.order"), 80).setSortable(true));
		// columns.add(new TextColumn4MapKey("f.size_", "size",
		// getText("netdisk.size"), 80).setUseTitleFromLabel(true)
		// .setValueFormater(new FileSizeFormater()));
		// columns.add(new TextColumn4MapKey("a.actor_name", "actor_name",
		// getText("netdisk.author"), 80));
		// columns.add(new TextColumn4MapKey("f.file_date", "file_date",
		// getText("netdisk.fileDate"), 120)
		// .setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		// columns.add(new TextColumn4MapKey("f.modified_date", "modified_date",
		// getText("netdisk.modifiedDate"), 120)
		// .setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		// columns.add(new HiddenColumn4MapKey("path", "path"));
		return columns;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['name']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "f.name" };
	}

	@Override
	protected String getFormActionName() {
		return "netdiskFile";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(450).setHeight(450);
	}

	@Override
	protected Condition getGridSpecalCondition() {
		Condition statusCondition = null;
		Condition typeCondition = null;
		Condition powerCondition = null;

		if (status != null && status.length() > 0) {
			String[] ss = status.split(",");
			if (ss.length == 1) {
				statusCondition = new EqualsCondition("f.status_", new Integer(
						ss[0]));
			} else {
				statusCondition = new InCondition("f.status_",
						StringUtils.stringArray2IntegerArray(ss));
			}
		}
		// 类型
		typeCondition = new EqualsCondition("f.type_", NetdiskFile.TYPE_FOLDER);
		// 选择用户所创建的文件夹
		SystemContext context = (SystemContext) this.getContext();
		powerCondition = new EqualsCondition("f.author_id", context
				.getUserHistory().getId());

		return ConditionUtils.mix2AndCondition(statusCondition, typeCondition,
				powerCondition);
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc/netdiskFile/selectFolder.js";
	}

	/**
	 * 状态值转换列表：使用中|已删除|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("netdiskFile.enabled"));
		statuses.put(String.valueOf(BCConstants.STATUS_DELETED),
				getText("entity.status.deleted"));
		statuses.put("", getText("bc.status.all"));
		return statuses;
	}

	@Override
	protected String getClickOkMethod() {
		return "bc.folderSelectDialog.clickOk";	}

	@Override
	protected String getHtmlPageNamespace() {
		return this.getContextPath() + "/bc";
	}
}
