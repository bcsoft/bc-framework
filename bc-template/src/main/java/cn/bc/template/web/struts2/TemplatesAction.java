package cn.bc.template.web.struts2;

import cn.bc.BCConstants;
import cn.bc.category.service.CategoryService;
import cn.bc.category.web.struts2.CategoryViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.DateUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.template.service.TemplateService;
import cn.bc.template.service.TemplateTypeService;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.FileSizeFormater;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.TreeViewAction;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.html.toolbar.ToolbarMenuButton;
import cn.bc.web.ui.html.tree.Tree;
import cn.bc.web.ui.html.tree.TreeNode;
import cn.bc.web.ui.json.Json;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.sun.star.uno.Exception;

import java.util.*;

/**
 * 模板视图Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class TemplatesAction extends TreeViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	private final static Log logger = LogFactory
			.getLog(CategoryViewAction.class);
	private TemplateService templateService;
	private CategoryService categoryService;

	public String status = String.valueOf(BCConstants.STATUS_ENABLED);
	public String code;
	private static Long ROOTID;
	/** 当前树节点ID */
	private Long pid;

	public void setPid(Long pid) {
		this.pid = pid;
	}

	@Autowired
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}

	@Override
	public boolean isReadonly() {
		// 模板管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：模板管理员
		return !context.hasAnyRole(getText("key.role.bc.template"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected OrderCondition getGridOrderCondition() {
		return new OrderCondition("t.order_");
	}

	@Override
	protected SqlObject<Map<String, Object>> getSqlObject() {
		SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

		// 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
		StringBuffer sql = new StringBuffer();
		sql.append("select t.id,t.uid_,t.order_ as orderNo,t.code,a.name as type,t.desc_,t.path,t.subject");
		sql.append(",au.actor_name as uname,t.file_date,am.actor_name as mname");
		sql.append(",t.modified_date,t.inner_ as inner,t.status_ as status,t.version_ as version");
		sql.append(",(select string_agg(name_, ',') from bc_category where id in");
		sql.append(" (select bc.cid from bc_template_template_category bc where bc.tid = t.id)) as category");
		sql.append(",a.code as typeCode,t.size_ as size,t.formatted,t.content");
		sql.append(" from bc_template t");
		sql.append(" inner join bc_template_type a on a.id=t.type_id ");
		sql.append(" inner join bc_identity_actor_history au on au.id=t.author_id ");
		sql.append(" left join bc_identity_actor_history am on am.id=t.modifier_id");
		sqlObject.setSql(sql.toString());

		// 注入参数
		sqlObject.setArgs(null);

		// 数据映射器
		sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
			public Map<String, Object> mapRow(Object[] rs, int rowNum) {
				Map<String, Object> map = new HashMap<String, Object>();
				int i = 0;
				map.put("id", rs[i++]);
				map.put("uid", rs[i++]);
				map.put("orderNo", rs[i++]);
				map.put("code", rs[i++]);
				map.put("type", rs[i++]);
				map.put("desc_", rs[i++]);
				map.put("path", rs[i++]);
				map.put("subject", rs[i++]);
				map.put("uname", rs[i++]);
				map.put("file_date", rs[i++]);
				map.put("mname", rs[i++]);
				map.put("modified_date", rs[i++]);
				map.put("inner", rs[i++]);
				map.put("status", rs[i++]);
				map.put("version", rs[i++]);
				map.put("category", rs[i++]);
				map.put("typeCode", rs[i++]);
				map.put("size", rs[i++]);
				map.put("formatted", rs[i++]);
				map.put("content", rs[i++]);
				if (map.get("content") == null
						|| map.get("content").toString().length() == 0) {
					map.put("isContent", "empty");
				} else {
					map.put("isContent", "full");
				}

				return map;
			}
		});
		return sqlObject;
	}

	@Override
	protected List<Column> getGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(new IdColumn4MapKey("t.id", "id"));
		columns.add(new TextColumn4MapKey("a.status_", "status",
				getText("template.status"), 40).setSortable(true)
				.setValueFormater(new KeyValueFormater(this.getStatuses())));
		columns.add(new TextColumn4MapKey("t.order_", "orderNo",
				getText("template.order"), 60).setSortable(true));
		columns.add(new TextColumn4MapKey("category", "category",
				getText("template.category"), 100).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("a.name", "type",
				getText("template.format"), 140).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.subject", "subject",
				getText("template.tfsubject")).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.code", "code",
				getText("template.code"), 160).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.version_", "version",
				getText("template.version"), 60).setSortable(true)
				.setUseTitleFromLabel(true));
		columns.add(new TextColumn4MapKey("t.formatted", "formatted",
				getText("template.file.formatted"), 40)
				.setValueFormater(new BooleanFormater()));
		// 最后修改
		columns.add(new TextColumn4MapKey("t.modified_date", "modified_date",
				getText("template.modified"), 210).setSortable(true)
				.setValueFormater(new AbstractFormater<Object>() {
					@Override
					public Object format(Object context, Object value) {
						if (value == null || "".equals(value.toString()))
							return null;
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map<String, Object>) context;
						return map.get("mname") + " ("
								+ DateUtils.formatDateTime2Minute((Date) value)
								+ "）";
					}
				}).setUseTitleFromLabel(true));
		columns.add(new HiddenColumn4MapKey("typeCode", "typeCode"));
		columns.add(new HiddenColumn4MapKey("uid", "uid"));
		columns.add(new HiddenColumn4MapKey("isContent", "isContent"));
		return columns;
	}

	// 状态键值转换
	private Map<String, String> getStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("template.status.normal"));
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
				getText("template.status.disabled"));
		statuses.put("", getText("template.status.all"));
		return statuses;
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['subject']";
	}

	@Override
	protected String[] getGridSearchFields() {
		return new String[] { "t.code", "am.actor_name", "t.path", "t.subject",
				"t.version_", "category", "a.name" };
	}

	@Override
	protected String getFormActionName() {
		return "template";
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(850).setMinWidth(400)
				.setHeight(400).setMinHeight(300).setHelp("mubanguanli");
	}

	@Override
	protected Toolbar getHtmlPageToolbar() {
		Toolbar tb = new Toolbar();

		if (!this.isReadonly()) {
			if (code == null || code.length() == 0) {
				// 新建按钮
				tb.addButton(this.getDefaultCreateToolbarButton());

				// 编辑按钮
				tb.addButton(this.getDefaultEditToolbarButton());
				// 删除按钮
				tb.addButton(new ToolbarButton().setIcon("ui-icon-trash")
						.setText(getText("label.delete"))
						.setClick("bc.templateList.deleteone"));
			}

			// 下载
			tb.addButton(new ToolbarButton()
					.setIcon("ui-icon-arrowthickstop-1-s")
					.setText(getText("label.download"))
					.setClick("bc.templateList.download"));

			// 在线查看
			tb.addButton(new ToolbarButton().setIcon("ui-icon-lightbulb")
					.setText(getText("template.preview.inline"))
					.setClick("bc.templateList.inline"));
		}

		// 状态按钮组
		tb.addButton(Toolbar.getDefaultToolbarRadioGroup(this.getStatuses(),
				"status", 0, getText("template.status.tips")));

		// 搜索按钮
		tb.addButton(this.getDefaultSearchToolbarButton());

		return tb;
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		Condition statusCondition = null;
		if (status != null && status.length() > 0 && code != null
				&& code.length() > 0) {
			statusCondition = new AndCondition(new EqualsCondition("t.status_",
					Integer.parseInt(status)), new EqualsCondition("t.code",
					code));
		} else if (status != null && status.length() > 0) {
			statusCondition = new EqualsCondition("t.status_",
					Integer.parseInt(status));
		} else if (code != null && code.length() > 0) {
			statusCondition = new EqualsCondition("t.code", code);
		}

		// 所属分类
		Condition categoryCondition = null;
		if (pid != null && pid.longValue() != ROOTID.longValue()) {
			List<Long> pids = this.templateService
					.findTemplateIdsByCategoryIdForList(pid);
			if (pids != null && pids.size() > 0)
				categoryCondition = new InCondition("t.id", pids);
			else {
				pids.add((long) 0);
				categoryCondition = new InCondition("t.id", pids);
			}
		}

		// 合并多个条件
		return ConditionUtils.mix2AndCondition(statusCondition,
				categoryCondition);
	}

	@Override
	protected void extendGridExtrasData(JSONObject json) throws JSONException {
		if (status != null && status.length() > 0 && code != null
				&& code.length() > 0) {
			json.put("status", status);
			json.put("code", code);
		} else if (status != null && status.length() > 0) {
			json.put("status", status);
		} else if (code != null && code.length() > 0) {
			json.put("code", code);
		}
		// 父节点条件
		json.put("pid", this.pid);
	}

	@Override
	protected Integer getTreeWith() {
		return 200;
	}

	@Override
	protected Tree getHtmlPageTree() {
		// 设置Root节点
		ROOTID = this.categoryService.getIdByFullCode("TPL");
		this.pid = ROOTID;
		Tree tree = new Tree(ROOTID.toString(), "全部");
		tree.setShowRoot(true);

		// 点击展开子节点图标的URL
		tree.setUrl(this.getHtmlPageNamespace() + "/loadTreeData");

		// 树的参数配置
		Json cfg = new Json();
		// 点击节点的回调函数
		cfg.put("clickNode", "bc.template.view.clickTreeNode");
		tree.setCfg(cfg);

		// 树的数据
		List<Map<String, Object>> treeData;

		// 构建树的子节点
		Collection<TreeNode> treeNodes;
		try {
			treeData = this.categoryService.findSubNodesData(this.pid);
			treeNodes = this.buildTreeNodes(treeData);
			for (TreeNode treeNode : treeNodes)
				tree.addSubNode(treeNode);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return tree;
	}

	/**
	 * 构建树节点
	 * 
	 * @param treeData
	 *            树节点数据
	 * @return
	 */
	private Collection<TreeNode> buildTreeNodes(
			List<Map<String, Object>> treeData) throws Exception {
		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		for (Map<String, Object> data : treeData) {
			TreeNode node = null;
			node = new TreeNode(String.valueOf(data.get("id")),
					String.valueOf(data.get("name")));
			treeNodes.add(node);
		}
		return treeNodes;
	}

	/**
	 * 展开树形菜单的子节点
	 * 
	 * @return
	 */
	public String loadTreeData() {
		JSONObject json = new JSONObject();
		try {
			List<Map<String, Object>> data = this.categoryService
					.findSubNodesData(this.pid);
			json.put("success", true);
			json.put("subNodesCount", data.size());
			json.put("html", TreeNode.buildSubNodes(this.buildTreeNodes(data)));
		} catch (java.lang.Exception e) {
			logger.error(e.getMessage());
		}

		this.json = json.toString();
		return "json";
	}

	@Override
	protected String getHtmlPageNamespace() {
		return getModuleContextPath() + "/templates";
	}

	@Override
	protected void addHtmlPageJsCss(Collection<String> jscss, String contextPath) {
		contextPath = this.getModuleContextPath();
		jscss.add("/bc/template/list.js");
		jscss.add("/bc/template/templateView.js");
	}
}
