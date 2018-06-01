/**
 *
 */
package cn.bc.identity.web.struts2;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色视图Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class RoleViewAction extends ViewAction<Map<String, Object>> {
  private static final long serialVersionUID = 1L;

  @Override
  public boolean isReadonly() {
    // 角色管理或系统管理员
    SystemContext context = (SystemContext) this.getContext();
    return !context.hasAnyRole("BC_ROLE_MANAGE", "BC_ADMIN");
  }

  @Override
  protected OrderCondition getGridDefaultOrderCondition() {
    return new OrderCondition("r.order_", Direction.Asc);
  }

  @Override
  protected SqlObject<Map<String, Object>> getSqlObject() {
    SqlObject<Map<String, Object>> sqlObject = new SqlObject<>();

    // 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
    sqlObject.setSql("select r.id as id,r.name as name,r.code as code,r.order_ as orderNo" +
      " from bc_identity_role r");

    // 数据映射器
    sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
      public Map<String, Object> mapRow(Object[] rs, int rowNum) {
        Map<String, Object> map = new HashMap<>();
        int i = -1;
        map.put("id", rs[++i]);
        map.put("name", rs[++i]);
        map.put("code", rs[++i]);
        map.put("orderNo", rs[++i]);
        return map;
      }
    });
    return sqlObject;
  }

  @Override
  protected List<Column> getGridColumns() {
    List<Column> columns = new ArrayList<>();
    columns.add(new IdColumn4MapKey("r.id", "id"));
    columns.add(new TextColumn4MapKey("r.order_", "orderNo", getText("label.order"), 80).setSortable(true)
      .setDir(Direction.Asc));
    columns.add(new TextColumn4MapKey("r.code", "code", getText("label.code"), 300).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("r.name", "name", getText("label.name")).setSortable(true)
      .setUseTitleFromLabel(true));
    return columns;
  }

  @Override
  protected String getGridRowLabelExpression() {
    return "['name']";
  }

  @Override
  protected String[] getGridSearchFields() {
    return new String[]{"r.order_", "r.name", "r.code"};
  }

  @Override
  protected PageOption getHtmlPageOption() {
    return super.getHtmlPageOption().setWidth(600).setHeight(400).setMinWidth(300).setMinHeight(200);
  }

  @Override
  protected boolean isQuirksMode() {
    return false;
  }
}