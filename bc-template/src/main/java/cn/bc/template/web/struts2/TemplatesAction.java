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
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.template.service.TemplateService;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.Icon;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.TreeViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.html.tree.Tree;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

/**
 * 模板视图Action
 *
 * @author lbj
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class TemplatesAction extends TreeViewAction<Map<String, Object>> {
  private static final long serialVersionUID = 1L;
  private TemplateService templateService;
  private CategoryService categoryService;

  public String status = String.valueOf(BCConstants.STATUS_ENABLED);
  public String code;
  public long cid;
  public String category;
  /**
   * 根节点全编码
   */
  public String rootNode;
  /**
   * 当前节点所属分类ID
   */
  private Long pid;

  public void setPid(Long pid) {
    this.pid = pid;
  }

  public Long getPid() {
    if (this.pid == null) {
      return this.categoryService.getIdByFullCode(this.rootNode);
    } else {
      return this.pid;
    }
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
  protected OrderCondition getGridDefaultOrderCondition() {
    return new OrderCondition("t.order_");
  }

  @Override
  protected SqlObject<Map<String, Object>> getSqlObject() {
    SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

    // 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
    StringBuffer sql = new StringBuffer();
    // 模板所属分类临时表
    sql.append("with tc_json(tid, c) as (");
    sql.append(" select t.id, row_to_json(row(string_agg(cast(c.id as text), ','), string_agg(c.name_, ',')))");
    sql.append(" from bc_category c");
    sql.append(" inner join bc_template_template_category tc on tc.cid = c.id");
    sql.append(" inner join bc_template t on t.id = tc.tid");
    sql.append(" group by t.id)");

    sql.append(" !!select t.id,t.uid_,t.order_ as orderNo,t.code,a.name as type,t.desc_,t.path,t.subject");
    sql.append(",au.actor_name as uname,t.file_date,am.actor_name as mname");
    sql.append(",t.modified_date,t.inner_ as inner,t.status_ as status,t.version_ as version");
    sql.append(" ,j.c->>'f1' as cid, j.c->>'f2' as category");// 模板所属分类
    sql.append(",(select template_get_acl_by_id_actorid(t.id, '"
      + this.getSystemContext().getUser().getCode() + "') as acl)");// 模板ACL
    sql.append(",a.code as typeCode,t.size_ as size,t.formatted,t.content");
    sql.append(" !!from bc_template t");
    sql.append(" inner join bc_template_type a on a.id=t.type_id ");
    sql.append(" inner join bc_identity_actor_history au on au.id=t.author_id ");
    sql.append(" left join bc_identity_actor_history am on am.id=t.modifier_id");
    sql.append(" left join tc_json j on j.tid = t.id");
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
        map.put("cid", rs[i++]);
        map.put("category", rs[i++]);
        map.put("acl", rs[i++]);
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
      getText("template.order"), 80).setSortable(true));
    // 所属分类
    columns.add(new TextColumn4MapKey("category", "category",
      getText("template.category"), 150)
      .setValueFormater(new AbstractFormater<Object>() {
        @SuppressWarnings("unchecked")
        @Override
        public String format(Object context, Object value) {
          Map<String, Object> map = (Map<String, Object>) context;
          return isReadonly() && isManageACL(map) // 只读状态，且有ACL管理权限，显示操作按钮
            ? buildColumnIcon(createIcon()) + map.get("category")
            : (String) map.get("category");
        }

        @Override
        public String getExportText(Object context, Object value) {
          @SuppressWarnings("unchecked")
          Map<String, Object> map = (Map<String, Object>) context;
          return (String) map.get("category");
        }
      }).setSortable(true).setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("a.name", "type",
      getText("template.format"), 140).setSortable(true)
      .setUseTitleFromLabel(true));
    // 标题
    columns.add(new TextColumn4MapKey("t.subject", "subject",
      getText("template.tfsubject"), 370)
      .setValueFormater(new AbstractFormater<Object>() {
        @SuppressWarnings("unchecked")
        @Override
        public String format(Object context, Object value) {
          Map<String, Object> map = (Map<String, Object>) context;
          return isReadonly() && isManageACL(map) // 只读状态，且有ACL管理权限，显示操作按钮
            ? buildColumnIcon(delIcon()) + map.get("subject")
            : (String) map.get("subject");
        }

        @Override
        public String getExportText(Object context, Object value) {
          @SuppressWarnings("unchecked")
          Map<String, Object> map = (Map<String, Object>) context;
          return (String) map.get("subject");
        }
      }).setSortable(true).setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("t.code", "code",
      getText("template.code"), 300).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("t.version_", "version",
      getText("template.version"), 240).setSortable(true)
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
    //空列
    columns.add(new TextColumn4MapKey("", "", ""));

    columns.add(new HiddenColumn4MapKey("typeCode", "typeCode"));
    columns.add(new HiddenColumn4MapKey("uid", "uid"));
    columns.add(new HiddenColumn4MapKey("cid", "cid"));
    columns.add(new HiddenColumn4MapKey("acl", "acl"));
    columns.add(new HiddenColumn4MapKey("isContent", "isContent"));
    columns.add(new HiddenColumn4MapKey("path", "path"));
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

  /**
   * 判断ACL权限
   *
   * @param m 查询返回的map
   * @return
   */
  private boolean isManageACL(Map<String, Object> m) {
    String acl = (String) m.get("acl");
    return !(acl == null || "".equals(acl) || "01".equals(acl));
  }

  /**
   * 构建视图列的图标
   *
   * @param icons
   * @return
   */
  private String buildColumnIcon(Icon... icons) {
    // 返回自定义图标
    String icon = "";
    for (Icon i : icons)
      icon += i.wrap();

    return icon;
  }

  /**
   * 新建图标
   *
   * @return
   */
  private Icon createIcon() {
    Icon icon = new Icon();
    icon.setClazz("ui-icon ui-icon-plusthick");
    icon.setTitle("在此分类下新建模板");// 鼠标提示信息
    icon.setClick("bc.templateList.createByIcon");// 点击函数
    return icon;
  }

  /**
   * 删除图标
   *
   * @return
   */
  private Icon delIcon() {
    Icon icon = new Icon();
    icon.setClazz("ui-icon ui-icon-close");
    icon.setTitle("删除此模板");// 鼠标提示信息
    icon.setClick("bc.templateList.deleteByIcon");// 点击函数
    return icon;
  }

  @Override
  protected String getGridRowLabelExpression() {
    return "['subject']";
  }

  @Override
  protected String[] getGridSearchFields() {
    return new String[]{"t.code", "am.actor_name", "t.path", "t.subject",
      "t.version_", "a.name", "j.c->>'f2'"// 模板所属分类
    };
  }

  @Override
  protected String getFormActionName() {
    return "template";
  }

  @Override
  protected PageOption getHtmlPageOption() {
    return super.getHtmlPageOption().setWidth(920).setMinWidth(450)
      .setHeight(450).setMinHeight(300).setHelp("mubanguanli");
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
    }

    // 下载
    tb.addButton(new ToolbarButton().setIcon("ui-icon-arrowthickstop-1-s")
      .setText(getText("label.download"))
      .setClick("bc.templateList.download"));

    // 在线查看
    tb.addButton(new ToolbarButton().setIcon("ui-icon-lightbulb")
      .setText(getText("template.preview.inline"))
      .setClick("bc.templateList.inline"));

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
    if (this.getPid() != this.categoryService.getIdByFullCode(rootNode)
      .longValue()) {
      List<Long> pids = this.templateService
        .findTemplateIdsByCategoryIdForList(this.getPid());
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
    // 是否只读
    json.put("isReadonly", this.isReadonly());
  }

  @Override
  protected Integer getTreeWith() {
    return 200;
  }

  @Override
  protected Tree getHtmlPageTree() {
    return CategoryViewAction.getCategoryTree(this.getPid(),
      this.isReadonly(), SystemContextHolder.get().getUser()
        .getCode(), this.getHtmlPageNamespace()
        + "/loadTreeData", "bc.template.view.clickTreeNode",
      this.categoryService);
  }

  /**
   * 展开树形菜单的子节点
   *
   * @return
   */
  public String loadTreeData() {
    // JSONObject json = new JSONObject();
    // try {
    // List<Map<String, Object>> data = this.categoryService
    // .findSubNodesData(this.pid, this.getSystemContext()
    // .getUser().getCode(), !this.getSystemContext()
    // .hasAnyRole(getText("key.role.bc.admin")));
    // json.put("success", true);
    // json.put("subNodesCount", data.size());
    // json.put("html", TreeNode.buildSubNodes(this.buildTreeNodes(data)));
    // } catch (java.lang.Exception e) {
    // logger.error(e.getMessage());
    // }

    this.json = this.categoryService.getLoadTreeData(this.isReadonly(),
      this.getPid());
    return "json";
  }

  @Override
  protected String getHtmlPageNamespace() {
    return getModuleContextPath() + "/templates";
  }

  @Override
  protected String getHtmlPageJs() {
    return this.getContextPath() + "/bc/template/list.js,"
      + this.getContextPath() + "/bc/template/templateView.js";
  }

  @Override
  protected String getGridDblRowMethod() {
    return "bc.templateList.doubleClick";
  }

  /**
   * 获取系统上下文
   *
   * @return
   */
  private SystemContext getSystemContext() {
    return (SystemContext) this.getContext();
  }

}
