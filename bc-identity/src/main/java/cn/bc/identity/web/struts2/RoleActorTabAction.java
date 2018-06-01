package cn.bc.identity.web.struts2;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.domain.Actor;
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
 * 角色表单的权限分配页签
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class RoleActorTabAction extends ViewAction<Map<String, Object>> {
  @Autowired
  private RoleService roleService;

  public Long roleId;

  @Override
  protected SqlObject<Map<String, Object>> getSqlObject() {
    SqlObject<Map<String, Object>> sqlObject = new SqlObject<>();

    // 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
    sqlObject.setSql("select a.id , a.type_, a.name, a.pname, a.order_"
      + " from bc_identity_role r"
      + " inner join bc_identity_role_actor ra on r.id = ra.rid"
      + " inner join bc_identity_actor a on a.id = ra.aid");

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
    return new OrderCondition("a.type_", Direction.Asc).add("a.order_", Direction.Asc);
  }

  @Override
  protected List<Column> getGridColumns() {
    List<Column> columns = new ArrayList<>();
    columns.add(new IdColumn4MapKey("a.id", "id"));
    columns.add(new TextColumn4MapKey("a.type_", "type", getText("role.actor.type"), 60).setSortable(true)
      .setValueFormater(new KeyValueFormater(getActorTypes())));
    columns.add(new TextColumn4MapKey("a.name", "name", getText("role.actor.name"), 160).setSortable(true));
    columns.add(new TextColumn4MapKey("a.pname", "pname", getText("role.actor.parent")).setSortable(true));
    return columns;
  }

  /**
   * 获取Actor类型值转换列表
   */
  private Map<String, String> getActorTypes() {
    Map<String, String> types = new LinkedHashMap<>();
    types.put(String.valueOf(Actor.TYPE_UNDEFINED), getText("role.actor.type.undefined"));
    types.put(String.valueOf(Actor.TYPE_UNIT), getText("role.actor.type.unit"));
    types.put(String.valueOf(Actor.TYPE_DEPARTMENT), getText("role.actor.type.department"));
    types.put(String.valueOf(Actor.TYPE_GROUP), getText("role.actor.type.group"));
    types.put(String.valueOf(Actor.TYPE_USER), getText("role.actor.type.user"));
    return types;
  }

  @Override
  protected String getGridRowLabelExpression() {
    return "['name']";
  }

  @Override
  protected String[] getGridSearchFields() {
    return new String[]{"a.name", "a.code", "a.pname"};
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
    Toolbar tb = new Toolbar();
    if (isReadonly()) {
      // 查看按钮
      tb.addButton(getDefaultOpenToolbarButton());
    } else {
      //添加单位或部门
      tb.addButton(new ToolbarButton().setText(getText("role.actor.addUnitOrDepartment")).setClick("addUnitOrDepartment"));
      //添加岗位
      tb.addButton(new ToolbarButton().setText(getText("role.actor.addGroup")).setClick("addGroup"));
      //添加用户
      tb.addButton(new ToolbarButton().setText(getText("role.actor.addUser")).setClick("addUser"));
      // 删除
      tb.addButton(new ToolbarButton().setText(getText("role.actor.delete")).setClick("deleteActor"));
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
    return getText("role.actor.exportFileName", new String[]{roleService.getRoleNameById(roleId)});
  }

  @Override
  protected boolean isQuirksMode() {
    return false;
  }

  public String actorIds;

  public String addActor() throws Exception {
    JSONObject json = new JSONObject();
    int c = this.roleService.addActor(roleId, StringUtils.stringArray2LongArray(actorIds.split(",")));
    json.put("count", c);
    this.json = json.toString();
    return "json";
  }

  public String deleteActor() throws Exception {
    JSONObject json = new JSONObject();
    int c = this.roleService.deleteActor(roleId, StringUtils.stringArray2LongArray(actorIds.split(",")));
    json.put("count", c);
    this.json = json.toString();
    return "json";
  }
}