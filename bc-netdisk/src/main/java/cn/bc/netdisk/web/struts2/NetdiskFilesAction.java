package cn.bc.netdisk.web.struts2;

import cn.bc.BCConstants;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.*;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.netdisk.domain.NetdiskFile;
import cn.bc.netdisk.service.NetdiskFileService;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.formater.FileSizeFormater;
import cn.bc.web.formater.LinkFormater;
import cn.bc.web.struts2.TreeViewAction;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.Serializable;
import java.util.*;

/**
 * 网络文件Action
 *
 * @author zxr
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class NetdiskFilesAction extends TreeViewAction<Map<String, Object>> {
  private final static Log logger = LogFactory
    .getLog(NetdiskFilesAction.class);
  private static final long serialVersionUID = 1L;
  public String status = String.valueOf(BCConstants.STATUS_ENABLED);
  private NetdiskFileService netdiskFileService;
  public long pid = PID_ROOT;// 上级节点的ID:0-代表根节点,-1-我创建的,-2-共享给我的
  private static final long PID_ROOT = 0;// 根节点
  private static final long PID_MINE = -1;// 我创建的
  private static final long PID_PUBLIC = -2;// 公共硬盘
  private static final long PID_SHARE = -3;// 共享硬盘
  public boolean isSelectFolders = false;// 是否选择文件夹 默认为否
  public String tree4SelectFolder;// 选择文件夹界面的树

  @Autowired
  public void setNetdiskFileService(NetdiskFileService netdiskFileService) {
    this.netdiskFileService = netdiskFileService;
  }

  @Override
  public boolean isReadonly() {
    // 默认为false个人硬盘管理权限
    return false;
  }

  // 公共硬盘管理权限
  public boolean isPublicHardDiskManagement() {
    // 模板管理员或系统管理员
    SystemContext context = (SystemContext) this.getContext();
    // 配置权限：公共管理员
    return context.hasAnyRole(getText("key.role.bc.netdisk.public"),
      getText("key.role.bc.admin"));

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
    sql.append(",f.path,f.pid,f2.name folder,f.type_,f.ext from bc_netdisk_file f");
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
        map.put("ext", rs[i++]);
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
      getText("netdisk.folder"), 80).setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("f.name", "name",
      getText("netdisk.name")).setUseTitleFromLabel(true)
      .setValueFormater(new LinkFormater() {

        @Override
        public String format(Object context, Object value) {
          @SuppressWarnings("unchecked")
          Map<String, Object> file = (Map<String, Object>) context;

          if (file.get("type").equals(NetdiskFile.TYPE_FOLDER)) {// 显示图片标识为文件夹
            return "<div title=\""
              + file.get("name")
              + "\">"
              + "<span style=\"float: left;\" title=\"文件夹\" class=\"ui-icon ui-icon-folder-collapsed\"></span>"
              + file.get("name") + "</div>";
          } else {
            return (String) file.get("name");
          }
        }

        @Override
        public String getLinkText(Object context, Object value) {
          @SuppressWarnings("unchecked")
          Map<String, Object> map = (Map<String, Object>) context;
          if (map.get("name") != null) {
            return map.get("name").toString();
          } else {
            return "";
          }
        }

        @Override
        public Object[] getParams(Object context, Object value) {
          // TODO Auto-generated method stub
          return null;
        }

      }));
    columns.add(new TextColumn4MapKey("f.ext", "ext",
      getText("netdisk.ext"), 50));
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
    return new String[]{"f.name"};
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
            + " multiple=\"true\" data-cfg=\"{&quot;beforeUploadFileIsAsync&quot;:true,&quot;beforeUploadFile&quot;:&quot;bc.netdiskFileForm.beforeUploadfile&quot;,&quot;callback&quot;:&quot;bc.netdiskFileForm.afterUploadfile&quot;,&quot;subdir&quot;:&quot;netdisk&quot;"
            + ",&quot;ptype&quot;:&quot;Netdisk&quot;,&quot;puid&quot;:&quot;Template.mt.6378&quot;}\" style=\"position: absolute;"
            + " left: 0;top: 0;width: 100%;height: 100%;filter: alpha(opacity = 10);opacity: 0;cursor: pointer;\"/></div>",
          "shangchuanwenjian")
        .addMenuItem(
          "<div style=\"position:relative;width:100%;height:100%;white-space:nowrap;\">上传文件夹<input type=\"file\" class=\"auto uploadFile\" id=\"uploadFolder\" name=\"uploadFolder\" title=\"点击上传文件夹\""
            + " multiple=\"true\" directory=\"true\" webkitdirectory=\"true\" data-cfg=\"{&quot;beforeUploadFileIsAsync&quot;:true,&quot;beforeUploadFile&quot;:&quot;bc.netdiskFileForm.beforeUploadfile&quot;,&quot;needConfirm&quot;:true,&quot;callback&quot;:&quot;bc.netdiskFileForm.afterUploadfolder&quot;,&quot;subdir&quot;:&quot;netdisk&quot;"
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
      // 如果拥有公共硬盘管理权限才能新建公共文件夹
      if (this.isPublicHardDiskManagement()) {
        tb.addButton(new ToolbarMenuButton("更多")
          .addMenuItem("下载", "xiazai")
          .addMenuItem("新建个人文件夹", "xinjiangerenwenjianjia")
          .addMenuItem("新建公共文件夹", "xinjiangonggongwenjianjia")
          .setChange("bc.netdiskFileView.selectMenuButtonItem"));
      } else {
        tb.addButton(new ToolbarMenuButton("更多")
          .addMenuItem("下载", "xiazai")
          .addMenuItem("新建个人文件夹", "xinjiangerenwenjianjia")
          .setChange("bc.netdiskFileView.selectMenuButtonItem"));
      }

    } else {
      tb.addButton(this.getDefaultOpenToolbarButton());
    }

    // 搜索按钮
    tb.addButton(this.getDefaultSearchToolbarButton());

    return tb;
  }

  @Override
  protected Condition getGridSpecalCondition() {
    SystemContext context = (SystemContext) this.getContext();
    OrCondition orCondition = new OrCondition();
    AndCondition andCondition = new AndCondition();
    // 状态条件
    Condition statusCondition = null;
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

    // 当前用户自己上传的文件
    Condition userCondition = null;
    if (pid == PID_ROOT || pid == PID_MINE) {
      userCondition = new EqualsCondition("f.author_id", context
        .getUserHistory().getId());
    }

    // 点击我的硬盘||共公硬盘时只查询子文件
    Condition nodeCondition = null;
    if (pid == PID_MINE || pid == PID_PUBLIC) {
      nodeCondition = new IsNullCondition("f.pid");
    }
    // 点击我的硬盘不应该看到我新建的公共文件
    Condition notPublic2MeCondition = null;
    if (pid == PID_MINE) {
      notPublic2MeCondition = new NotEqualsCondition("f.folder_type",
        NetdiskFile.FOLDER_TYPE_PUBLIC);
    }
    // 当前用户有权限查看的文件
    Condition shareCondition = null;
    if (pid == PID_ROOT || pid == PID_SHARE) {
      Serializable[] ids = this.netdiskFileService
        .getUserSharFileId(context.getUser().getId());
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
      shareCondition = (ids != null ? new QlCondition("f.id in ("
        + qlStr4File + ")", ids) : null);
    }

    // 合并
    orCondition.add(userCondition, shareCondition).setAddBracket(true);

    // 公共硬盘
    OrCondition publicCondition = new OrCondition();
    if (pid == PID_ROOT || pid == PID_PUBLIC) {
      Long[] publicFileIds = this.netdiskFileService
        .getUserPublicFileId();
      String qlStr4File4Public = "";
      if (publicFileIds != null) {
        for (int i = 0; i < publicFileIds.length; i++) {
          if (i + 1 != publicFileIds.length) {
            qlStr4File4Public += "?,";
          } else {
            qlStr4File4Public += "?";
          }
        }
      }
      publicCondition.add(
        (publicFileIds != null ? new QlCondition("f.id in ("
          + qlStr4File4Public + ")", publicFileIds) : null))
        .setAddBracket(true);
    }
    // 共享给我的硬盘
    Condition share2meCondition = null;
    if (pid == PID_SHARE) {
      share2meCondition = new QlCondition(
        "f.id in ( select pid from bc_netdisk_share where aid ="
          + context.getUser().getId()
          + "  and not exists(select 1 from bc_netdisk_file f1 where f.id=f1.id and f1.pid in (select pid from bc_netdisk_share where aid ="
          + context.getUser().getId() + ")))");
    }

    // 指定节点条件
    Condition parentCondition = null;
    if (this.pid > 0) {
      // parentCondition = new QlCondition("( f.pid =" + this.pid
      // + " or f.id=" + this.pid + ")");
      parentCondition = new EqualsCondition("f.pid", this.pid);
    }
    // 如果当前用户有权限查看的文件和公共硬盘条件都为空就返回空
    if (orCondition.isEmpty() && publicCondition.isEmpty()
      && !(this.pid > 0)) {
      return null;
      // 公共硬盘查询条件
    } else if (pid == PID_PUBLIC) {
      return ConditionUtils.mix2AndCondition(
        (publicCondition.isEmpty() ? null : publicCondition),
        nodeCondition);
    } // 节点的查询条件
    else if (this.pid > 0) {
      return ConditionUtils.mix2AndCondition(parentCondition,
        statusCondition);
    } else {
      return ConditionUtils.mix2OrCondition(
        andCondition.add(statusCondition, parentCondition,
          nodeCondition, share2meCondition,
          notPublic2MeCondition,
          orCondition.isEmpty() ? null : orCondition)
          .setAddBracket(true),
        (publicCondition.isEmpty() ? null : publicCondition));
    }
  }

  @Override
  protected Page<Map<String, Object>> findPage() {
    // 如果返回为null则不进行查询
    if (this.getGridSpecalCondition() != null) {
      return super.findPage();
    } else {
      return new Page<Map<String, Object>>(1, 1, 0,
        new ArrayList<Map<String, Object>>());
    }
  }

  @Override
  protected void extendGridExtrasData(JSONObject json) throws JSONException {
    super.extendGridExtrasData(json);

    // 状态条件
    if (this.status != null && this.status.trim().length() > 0) {
      json.put("status", status);
    }

    // 父节点条件
    json.put("pid", this.pid);

    // 是否是调用选择文件夹方法
    json.put("isSelectFolders", isSelectFolders);
  }

  @Override
  protected String getHtmlPageJs() {
    return this.getContextPath() + "/bc/netdiskFile/view.js,"
      + this.getContextPath() + "/bc/netdiskFile/form.js";
  }

  /**
   * 页面加载后调用的js初始化方法
   */
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

  // 构建左侧的导航树
  @Override
  protected Tree getHtmlPageTree() {
    Tree tree = new Tree();
    tree.setNodeId(PID_ROOT + "");
    TreeNode node;

    // 获取树数据的url
    // 选择文件夹界面的url
    if (isSelectFolders) {
      tree.setUrl(this.getContextPath()
        + "/bc/netdiskFiles/loadTreeData4selectFolder");
    } else {
      tree.setUrl(this.getContextPath() + "/bc/netdiskFiles/loadTreeData");
    }
    // 树的特殊配置参数
    Json cfg = new Json();
    // 点击视图中节点调用的js
    if (!isSelectFolders) {
      cfg.put("clickNode", "bc.netdiskFileView.clickTreeNode");
    }
    tree.setCfg(cfg);

    // 创建"我的硬盘"节点
    TreeNode mineNode = new TreeNode(PID_MINE + "", "我的硬盘");
    mineNode.setLeaf(false);
    mineNode.setOpen(true);
    tree.addSubNode(mineNode);

    // 创建"我的硬盘"节点的子节点
    List<Map<String, Object>> ownerFiles = this.netdiskFileService
      .findOwnerFolder(SystemContextHolder.get().getUserHistory()
        .getId(), this.pid > 0 ? new Long(pid) : null);
    for (Map<String, Object> f : ownerFiles) {
      node = new TreeNode(f.get("id").toString(), f.get("name")
        .toString(), f.get("type").toString()
        .equals(String.valueOf(NetdiskFile.TYPE_FILE)));
      mineNode.addSubNode(node);
    }

    // 如果调用的是选择文件夹方法就要根据是否拥有公共文件夹权限来创建
    if (isSelectFolders) {
      if (isPublicHardDiskManagement()) {
        // 创建"公共硬盘"节点
        TreeNode publicNode = new TreeNode(PID_PUBLIC + "", "公共硬盘");
        publicNode.setLeaf(false);
        publicNode.setOpen(true);
        tree.addSubNode(publicNode);
        // 创建"公共硬盘"节点的子节点
        List<Map<String, Object>> publcRootFiles = this.netdiskFileService
          .findPublicRootFolder();
        for (Map<String, Object> f : publcRootFiles) {
          node = new TreeNode(f.get("id").toString(), f.get("name")
            .toString(), f.get("type").toString()
            .equals(String.valueOf(NetdiskFile.TYPE_FILE)));
          publicNode.addSubNode(node);
        }
      }
    } else {
      // 创建"公共硬盘"节点
      TreeNode publicNode = new TreeNode(PID_PUBLIC + "", "公共硬盘");
      publicNode.setLeaf(false);
      publicNode.setOpen(true);
      tree.addSubNode(publicNode);
      // 创建"公共硬盘"节点的子节点
      List<Map<String, Object>> publcRootFiles = this.netdiskFileService
        .findPublicRootFolder();
      for (Map<String, Object> f : publcRootFiles) {
        node = new TreeNode(f.get("id").toString(), f.get("name")
          .toString(), f.get("type").toString()
          .equals(String.valueOf(NetdiskFile.TYPE_FILE)));
        publicNode.addSubNode(node);
      }
    }

    // 创建"共享硬盘"节点
    TreeNode shareNode = new TreeNode(PID_SHARE + "", "共享给我的");
    shareNode.setLeaf(false);
    shareNode.setOpen(true);
    tree.addSubNode(shareNode);
    List<Map<String, Object>> shareRootFolders = null;
    // 创建"共享硬盘"节点的子节点
    // 选择文件夹时查找有编辑权限的
    if (isSelectFolders) {
      shareRootFolders = this.netdiskFileService.findShareRootFolders(
        SystemContextHolder.get().getUser().getId(), true);

    } else {
      shareRootFolders = this.netdiskFileService.findShareRootFolders(
        SystemContextHolder.get().getUser().getId(), false);
    }
    for (Map<String, Object> f : shareRootFolders) {
      node = new TreeNode(f.get("id").toString(), f.get("name")
        .toString(), f.get("type").toString()
        .equals(String.valueOf(NetdiskFile.TYPE_FILE)));
      shareNode.addSubNode(node);
    }
    return tree;
  }

  // 导出表格的数据为excel文件
  public String loadTreeData() throws Exception {
    JSONObject json = new JSONObject();
    List<Map<String, Object>> ownerFiles = null;
    try {
      // 如果点击我的硬盘pid条件为
      if (this.pid == PID_MINE) {
        ownerFiles = this.netdiskFileService.findOwnerFolder(
          SystemContextHolder.get().getUserHistory().getId(),
          null);
        // 如果点击共享硬盘pid条件为
      } else if (this.pid == PID_SHARE) {
        // 选择文件夹时查找有编辑权限的
        if (isSelectFolders) {
          ownerFiles = this.netdiskFileService.findShareRootFolders(
            SystemContextHolder.get().getUser().getId(), true);

        } else {
          ownerFiles = this.netdiskFileService.findShareRootFolders(
            SystemContextHolder.get().getUser().getId(), false);
        }
        // 如果点击公共硬盘pid条件为
      } else if (this.pid == PID_PUBLIC) {
        ownerFiles = this.netdiskFileService.findPublicRootFolder();
      } else {
        // 选择文件夹时查找有编辑权限的
        if (isSelectFolders) {
          ownerFiles = this.netdiskFileService.findChildFolder(
            this.pid, true, SystemContextHolder.get().getUser()
              .getId());
        } else {
          ownerFiles = this.netdiskFileService.findChildFolder(
            this.pid, false, null);
        }

      }
      List<TreeNode> subNodes = new ArrayList<TreeNode>();
      TreeNode node;
      for (Map<String, Object> f : ownerFiles) {
        node = new TreeNode(f.get("id").toString(), f.get("name")
          .toString(), f.get("type").toString()
          .equals(String.valueOf(NetdiskFile.TYPE_FILE)));
        subNodes.add(node);
      }

      json.put("success", true);
      json.put("subNodesCount", ownerFiles.size());// 子节点的数量
      json.put("html", TreeNode.buildSubNodes(subNodes));// 子节点的html代码
    } catch (Exception e) {
      logger.warn(e.getMessage(), e);
      json.put("success", false);
      json.put("msg", e.getMessage());
    }

    this.json = json.toString();
    return "json";
  }

  // 选择文件夹
  public String selectFolders() {
    tree4SelectFolder = this.getHtmlPageTree().addStyle("border", "0")
      .toString();
    return "foldersTree";
  }
}