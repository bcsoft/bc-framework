/**
 *
 */
package cn.bc.identity.web.struts2.select;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.struts2.AbstractSelectPageAction;
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
 * 选择角色Action
 *
 * @author Action
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectRoleAction extends AbstractSelectPageAction<Map<String, Object>> {
  @Override
  protected OrderCondition getGridDefaultOrderCondition() {
    // 默认排序方向：排序号
    return new OrderCondition("r.order_", Direction.Asc);
  }

  @Override
  protected SqlObject<Map<String, Object>> getSqlObject() {
    SqlObject<Map<String, Object>> sqlObject = new SqlObject<>();

    // 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
    sqlObject.setSql("select r.id as id, r.order_ as order, r.name as name, r.code as code"
      + " from BC_IDENTITY_ROLE r");

    // 数据映射
    sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
      public Map<String, Object> mapRow(Object[] rs, int rowNum) {
        Map<String, Object> map = new HashMap<>();
        int i = 0;
        map.put("id", rs[i++]);
        map.put("order", rs[i++]);
        map.put("name", rs[i++]);
        map.put("code", rs[i]);
        return map;
      }
    });
    return sqlObject;
  }

  @Override
  protected List<Column> getGridColumns() {
    List<Column> columns = new ArrayList<>();
    columns.add(new IdColumn4MapKey("id", "id"));
    columns.add(new TextColumn4MapKey("order_", "order", getText("label.order"), 80).setSortable(true));
    columns.add(new TextColumn4MapKey("name", "name", getText("label.name"), 180)
      .setSortable(true).setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("code", "code", getText("label.code"))
      .setSortable(true).setUseTitleFromLabel(true));
    return columns;
  }

  @Override
  protected String getHtmlPageTitle() {
    return this.getText("role.title.select");
  }

  @Override
  protected PageOption getHtmlPageOption() {
    return super.getHtmlPageOption().setWidth(540).setHeight(450);
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
  protected void addJsCss(List<String> container) {
    container.add(this.getActionNamespace() + "/select.js");
  }

  @Override
  protected String getClickOkMethod() {
    return "onOk";
  }

  @Override
  protected boolean isQuirksMode() {
    return false;
  }
}