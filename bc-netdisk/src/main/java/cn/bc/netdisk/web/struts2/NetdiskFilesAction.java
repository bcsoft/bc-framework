package cn.bc.netdisk.web.struts2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.query.condition.impl.QlCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.netdisk.service.NetdiskFileService;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.FileSizeFormater;
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
 * 网络文件Action
 * 
 * @author zxr
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class NetdiskFilesAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	public String status = String.valueOf(BCConstants.STATUS_ENABLED);
	private NetdiskFileService netdiskFileService;

	@Autowired
	public void setNetdiskFileService(NetdiskFileService netdiskFileService) {
		this.netdiskFileService = netdiskFileService;
	}

	@Override
	public boolean isReadonly() {
		// 模板管理员或系统管理员
	//	SystemContext context = (SystemContext) this.getContext();
		// 配置权限：模板管理员
		// return !context.hasAnyRole(getText("key.role.bc.netdisk"),
		// getText("key.role.bc.admin"));
		return false;
	}

	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("f.status_").add("f.order_", Direction.Asc)
				.add("f.file_date", Direction.Desc);
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select f.id,f.status_,f.order_,f.name,f.size_,a.actor_name,f.file_date,f.modified_date");
		sql.append(",f.path,f.pid,f2.name folder,f.type_ from bc_netdisk_file f");
		sql.append(" inner join bc_identity_actor_history a on a.id=f.author_id");
		sql.append(" left join bc_netdisk_file f2 on f.pid = f2.id");
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
				map.put("pid", rs[i++]);
				map.put("folder", rs[i++]);
				map.put("type", rs[i++]);
				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("f.id", "id"));
		// columns.add(new TextColumn4MapKey("f.status_", "status",
		// getText("netdisk.status"), 40).setSortable(true)
		// .setValueFormater(new KeyValueFormater(this.getStatuses())));
		columns.add(new TextColumn4MapKey("f2.name", "folder",
				getText("netdisk.folder"), 80));
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
		// columns.add(new TextColumn4MapKey("f.modified_date", "modified_date",
		// getText("netdisk.modifiedDate"), 120)
		// .setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
		columns.add(new TextColumn4MapKey("f.order_", "orderNo",
				getText("netdisk.order"), 80).setSortable(true));
		columns.add(new HiddenColumn4MapKey("path", "path"));
		columns.add(new HiddenColumn4MapKey("pid", "pid"));
		columns.add(new HiddenColumn4MapKey("type", "type"));
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
		return super.getHtmlPageOption().setWidth(730).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridDblRowMethod() {
		// 双击预览
		return "bc.netdiskFileView.dblclick";
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
							"<div style=\"position:relative;width:100%;height:100%;white-space:nowrap;\">上传文件<input type=\"file\" class=\"auto uploadFile\" id=\"uploadFile\" name=\"uploadFile\" title=\"点击上传文件\""
									+ " multiple=\"true\" data-cfg=\"{&quot;callback&quot;:&quot;bc.netdiskFileForm.afterUploadfile&quot;,&quot;subdir&quot;:&quot;netdisk&quot;"
									+ ",&quot;ptype&quot;:&quot;Netdisk&quot;,&quot;puid&quot;:&quot;Template.mt.6378&quot;}\" style=\"position: absolute;"
									+ " left: 0;top: 0;width: 100%;height: 100%;filter: alpha(opacity = 10);opacity: 0;cursor: pointer;\"/></div>",
							"shangchuanwenjian")
					.addMenuItem(
							"<div style=\"position:relative;width:100%;height:100%;white-space:nowrap;\">上传文件夹<input type=\"file\" class=\"auto uploadFile\" id=\"uploadFolder\" name=\"uploadFolder\" title=\"点击上传文件夹\""
									+ " multiple=\"true\" directory=\"true\" webkitdirectory=\"true\" data-cfg=\"{&quot;callback&quot;:&quot;bc.netdiskFileForm.afterUploadfolder&quot;,&quot;subdir&quot;:&quot;netdisk&quot;"
									+ ",&quot;ptype&quot;:&quot;Netdisk&quot;,&quot;puid&quot;:&quot;Template.mt.6378&quot;}\" style=\"position: absolute;"
									+ " left: 0;top: 0;width: 100%;height: 100%;filter: alpha(opacity = 10);opacity: 0;cursor: pointer;\"/></div>",
							"shangchuanwenjianjia")
					.setChange("bc.netdiskFileView.selectMenuButtonItem"));
			// 共享
			tb.addButton(new ToolbarButton().setIcon("ui-icon-person")
					.setText("共享").setClick("bc.netdiskFileView.share"));
			// 整理
			tb.addButton(new ToolbarButton()
					.setIcon("ui-icon-folder-collapsed").setText("整理")
					.setClick("bc.netdiskFileView.clearUp"));
			// 删除
			tb.addButton(new ToolbarButton().setIcon("ui-icon-trash")
					.setText("删除").setClick("bc.netdiskFileView.remove"));
			// 预览
			tb.addButton(new ToolbarButton().setIcon("ui-icon-pencil")
					.setText("查看").setClick("bc.netdiskFileView.preview"));
			// 其他操作
			tb.addButton(new ToolbarMenuButton("更多")
					.addMenuItem("下载", "xiazai")
					.addMenuItem("新建文件夹", "xinjianwenjianjia")
					.setChange("bc.netdiskFileView.selectMenuButtonItem"));
		} else {
			tb.addButton(this.getDefaultOpenToolbarButton());
		}

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected Condition getGridSpecalCondition() {
		OrCondition orCondition = new OrCondition();
		// 状态条件
		Condition statusCondition = null;
		// Condition typeCondition = null;
		Condition userCondition = null;
		Condition authorityCondition = null;
		// 状态
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
		// 当前用户只能查看自己上传的文件
		SystemContext context = (SystemContext) this.getContext();
		userCondition = new EqualsCondition("f.author_id", context
				.getUserHistory().getId());
		// 当前用户有权限查看的文件
		Serializable[] ids = this.netdiskFileService.getUserSharFileId(context
				.getUser().getId());
		String qlStr4File = "";
		if (ids != null) {
			for (int i = 0; i < ids.length; i++) {
				if (i + 1 != ids.length) {
					qlStr4File += "?,";
				} else {
					qlStr4File += "?";
				}
			}
		}
		authorityCondition = orCondition.add(
				userCondition,
				(ids != null ? new QlCondition("f.id in (" + qlStr4File + ")",
						ids) : null)).setAddBracket(true);
		return ConditionUtils.mix2AndCondition(statusCondition,
				authorityCondition);
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		super.extendGridExtrasData(json);

		// 状态条件
		if (this.status != null && this.status.trim().length() > 0) {
			json.put("status", status);
		}

	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc/netdiskFile/view.js,"
				+ this.getContextPath() + "/bc/netdiskFile/form.js";
	}

	/** 页面加载后调用的js初始化方法 */
	protected String getHtmlPageInitMethod() {
		return "bc.netdiskFileView.init";
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
