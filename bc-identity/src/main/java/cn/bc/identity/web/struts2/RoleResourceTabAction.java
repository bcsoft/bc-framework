package cn.bc.identity.web.struts2;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Resource;
import cn.bc.identity.service.RoleService;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

/**
 * 角色表单的资源分配页签
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class RoleResourceTabAction extends ViewAction<Map<String, Object>> {
  @Autowired
  private RoleService roleService;

  public Long roleId;

  @Override
  protected SqlObject<Map<String, Object>> getSqlObject() {
    SqlObject<Map<String, Object>> sqlObject = new SqlObject<>();

    // 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
    sqlObject.setSql("select s.id sid,s.type_,s.name,p.name pname, r.id rid, s.order_"
      + " from bc_identity_resource as s"
      + " left join bc_identity_resource as p on p.id = s.belong"
      + " inner join bc_identity_role_resource as rs on rs.sid = s.id"
      + " inner join bc_identity_role as r on r.id = rs.rid");

    sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
      @Override
      public Map<String, Object> mapRow(Object[] rs, int rowNum) {
        Map<String, Object> map = new HashMap<>();
        int i = -1;
        map.put("id", rs[++i]);
        map.put("type", rs[++i]);
        map.put("name", rs[++i]);
        map.put("pname", rs[++i]);
        return map;
      }
    });
    return sqlObject;
  }

  @Override
  protected OrderCondition getGridDefaultOrderCondition() {
    return new OrderCondition("s.type_", Direction.Asc).add("s.order_", Direction.Asc);
  }

  @Override
  protected List<Column> getGridColumns() {
    List<Column> columns = new ArrayList<>();
    columns.add(new IdColumn4MapKey("s.id", "id"));
    columns.add(new TextColumn4MapKey("s.type_", "type", getText("role.resource.type"), 60).setSortable(true)
      .setValueFormater(new KeyValueFormater(getResourceTypes())));
    columns.add(new TextColumn4MapKey("s.name", "name", getText("role.resource.name"), 130).setSortable(true));
    columns.add(new TextColumn4MapKey("p.name", "pname", getText("role.resource.belong")).setSortable(true));
    return columns;
  }

  /**
   * 获取资源类型值转换列表
   */
  private Map<String, String> getResourceTypes() {
    Map<String, String> types = new LinkedHashMap<>();
    types.put(String.valueOf(Resource.TYPE_FOLDER), getText("resource.type.folder"));
    types.put(String.valueOf(Resource.TYPE_INNER_LINK), getText("resource.type.innerLink"));
    types.put(String.valueOf(Resource.TYPE_OUTER_LINK), getText("resource.type.outerLink"));
    return types;
  }

  @Override
  protected String getGridRowLabelExpression() {
    return "['name']";
  }

  @Override
  protected String[] getGridSearchFields() {
    return new String[]{"s.name", "p.name"};
  }

  @Override
  protected Condition getGridSpecalCondition() {
    return new EqualsCondition("r.id", roleId);
  }

  @Override
  protected void extendGridExtrasData(JSONObject json) throws JSONException {
    super.extendGridExtrasData(json);
    json.put("roleId", roleId);
  }

  @Override
  protected Toolbar getHtmlPageToolbar() {
    super.getHtmlPageToolbar();
    Toolbar tb = new Toolbar();
    if (isReadonly()) {
      // 查看按钮
      tb.addButton(getDefaultOpenToolbarButton());
    } else {
      // 添加
      tb.addButton(new ToolbarButton().setText(getText("role.resource.add")).setClick("addResource"));
      // 删除
      tb.addButton(new ToolbarButton().setText(getText("role.resource.delete")).setClick("deleteResource"));
    }
    // 搜索按钮
    tb.addButton(this.getDefaultSearchToolbarButton());
    return tb;
  }

  @Override
  protected void addJsCss(List<String> container) {
    container.add(getActionNamespace() + "/view.js");
  }

  @Override
  protected String getDefaultExportFileName() {
    return getText("role.resource.exportFileName", new String[]{roleService.getRoleNameById(roleId)});
  }

  @Override
  protected boolean isQuirksMode() {
    return false;
  }


  public String resourceIds;

  public String addResource() throws Exception {
    JSONObject json = new JSONObject();
    int c = this.roleService.addResource(roleId, StringUtils.stringArray2LongArray(resourceIds.split(",")));
    json.put("count", c);
    this.json = json.toString();
    return "json";
  }

  public String deleteResource() throws Exception {
    JSONObject json = new JSONObject();
    int c = this.roleService.deleteResource(roleId, StringUtils.stringArray2LongArray(resourceIds.split(",")));
    json.put("count", c);
    this.json = json.toString();
    return "json";
  }
}