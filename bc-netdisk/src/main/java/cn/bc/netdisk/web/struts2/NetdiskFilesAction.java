package cn.bc.netdisk.web.struts2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.FileSizeFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.html.toolbar.ToolbarMenuButton;

/**
 * 模板参数视图Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class NetdiskFilesAction extends ViewAction<Map<String, Object>> {
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
		return new OrderCondition("f.status_").add("f.order_", Direction.Asc);
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
		columns.add(new TextColumn4MapKey("f.order_", "orderNo",
				getText("netdisk.order"), 80).setSortable(true));
		columns.add(new TextColumn4MapKey("f.name", "name",
				getText("netdisk.name")).setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("f.size_", "size",
				getText("netdisk.size"), 80).setUseTitleFromLabel(true)
				.setValueFormater(new FileSizeFormater()));
		columns.add(new TextColumn4MapKey("a.actor_name", "actor_name",
				getText("netdisk.author"), 80));
		columns.add(new TextColumn4MapKey("f.file_date", "file_date",
				getText("netdisk.fileDate"), 120)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("f.modified_date", "modified_date",
				getText("netdisk.modifiedDate"), 120)
				.setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new HiddenColumn4MapKey("path", "path"));
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
		return super.getHtmlPageOption().setWidth(900).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		if (!this.isReadonly()) {
			JSONObject json = new JSONObject();
			try {
				json.put("callback", "bc.netdiskFileForm.afterUploadfile");
				json.put("subdir", "netdisk");
				json.put("ptype", "Netdisk");
				json.put("puid", "Netdisk1");
			} catch (JSONException e) {
			}
			// 上传操作
			tb.addButton(new ToolbarMenuButton("上传")
					.addMenuItem(
							"<div style=\"position:relative;width:100%;height:100%;\">上传文件<input type=\"file\" class=\"auto uploadFile\" id=\"uploadFile\" name=\"uploadFile\" title=\"点击上传文件\""
									+ " data-cfg=\"{&quot;callback&quot;:&quot;bc.netdiskFileForm.afterUploadfile&quot;,&quot;subdir&quot;:&quot;netdisk&quot;"
									+ ",&quot;ptype&quot;:&quot;Netdisk&quot;,&quot;puid&quot;:&quot;Template.mt.6378&quot;}\" style=\"position: absolute;"
									+ " left: 0;top: 0;width: 100%;height: 100%;filter: alpha(opacity = 10);opacity: 0;cursor: pointer;\"/></div>",
							"shangchuanwenjian")
					.addMenuItem("上传文件夹", "shangchuanwenjianjia")
					.setChange("bc.netdiskFileView.selectMenuButtonItem"));
			// // 上传文件
			// tb.addButton(new ToolbarButton()
			// .setIcon("ui-icon-arrowthickstop-1-n").setText("上传文件")
			// .setAction("create"));
			// // 上传文件夹
			// tb.addButton(new ToolbarButton()
			// .setIcon("ui-icon-arrowthickstop-1-n").setText("上传文件夹")
			// .setAction("create"));
			// 共享
			tb.addButton(new ToolbarButton().setIcon("ui-icon-person")
					.setText("共享").setClick("bc.netdiskFileView.share"));
			// 整理
			tb.addButton(new ToolbarButton()
					.setIcon("ui-icon-folder-collapsed").setText("整理")
					.setClick("bc.netdiskFileView.clearUp"));
			// 删除
			tb.addButton(this.getDefaultDeleteToolbarButton());
			// 预览
			tb.addButton(new ToolbarButton().setIcon("ui-icon-pencil")
					.setText("预览").setClick("bc.netdiskFileView.preview"));
			// 其他操作
			tb.addButton(new ToolbarMenuButton("其他")
					.addMenuItem("下载", "xiazai")
					.addMenuItem("新建文件夹", "xinjianwenjianjia")
					.setChange("bc.netdiskFileView.selectMenuButtonItem"));
			// // 下载
			// tb.addButton(new ToolbarButton()
			// .setIcon("ui-icon-arrowthickstop-1-s").setText("下载")
			// .setAction("create"));
			// // 新建文件夹
			// tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
			// .setText("新建文件夹").setAction("create"));
			// tb.addButton(Toolbar.getDefaultToolbarRadioGroup(
			// this.getStatuses(), "status", 0,
			// getText("title.click2changeSearchStatus")));

			// // 编辑按钮
			// tb.addButton(this.getDefaultEditToolbarButton());
		} else {
			tb.addButton(this.getDefaultOpenToolbarButton());
		}

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc/netdiskFile/view.js,"
				+ this.getContextPath() + "/bc/netdiskFile/form.js";
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

}
