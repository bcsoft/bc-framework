/**
 *
 */
package cn.bc.message.web.struts2;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息管理视图
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class MessageViewAction extends ViewAction<Map<String, Object>> {
  private static final long serialVersionUID = 1L;

  @Override
  public boolean isReadonly() {
    // 系统管理员
    SystemContext context = (SystemContext) this.getContext();
    return !context.hasAnyRole("BC_ADMIN");
  }

  @Override
  protected String getHtmlPageTitle() {
    return this.getText("message.title");
  }

  @Override
  protected String getHtmlPageNamespace() {
    return this.getContextPath() + "/bc/message";
  }

  @Override
  protected PageOption getHtmlPageOption() {
    return super.getHtmlPageOption().setWidth(620).setMinWidth(450).setHeight(400).setMinHeight(200);
  }

  @Override
  protected OrderCondition getGridDefaultOrderCondition() {
    return new OrderCondition("m.send_date", Direction.Desc);
  }

  @Override
  protected String[] getGridSearchFields() {
    return new String[]{"m.subject", "m.content"};
  }

  @Override
  protected String getGridRowLabelExpression() {
    return "['subject']";
  }

  @Override
  protected SqlObject<Map<String, Object>> getSqlObject() {
    SqlObject<Map<String, Object>> sqlObject = new SqlObject<>();

    // 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
    sqlObject.setSql("SELECT m.id, m.read_, m.send_date, m.subject, a.actor_name sender_name\n" +
      " FROM bc_message m\n" +
      " inner join bc_identity_actor_history a on a.id = m.sender_id");

    // 注入参数
    sqlObject.setArgs(null);

    // 数据映射器
    sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
      public Map<String, Object> mapRow(Object[] rs, int rowNum) {
        Map<String, Object> map = new HashMap<>();
        int i = 0;
        map.put("id", rs[i++]);
        map.put("read_", rs[i++]);
        map.put("send_date", rs[i++]);
        map.put("sender_name", rs[i++]);
        map.put("subject", rs[i]);
        return map;
      }
    });
    return sqlObject;
  }

  @Override
  protected List<Column> getGridColumns() {
    List<Column> columns = new ArrayList<>();
    columns.add(new IdColumn4MapKey("m.id", "id"));
    columns.add(new TextColumn4MapKey("m.read_", "read_", getText("message.read"), 40).setSortable(true)
      .setValueFormater(new BooleanFormater()));
    columns.add(new TextColumn4MapKey("m.send_date", "send_date", getText("message.sendDate"), 120)
      .setSortable(true).setDir(Direction.Asc).setValueFormater(new CalendarFormater()));
    columns.add(new TextColumn4MapKey("m.sender_name", "sender_name", getText("message.sender.name"), 100)
      .setSortable(true));
    columns.add(new TextColumn4MapKey("m.subject", "subject", getText("message.subject")).setSortable(true));
    return columns;
  }

  @Override
  protected Toolbar getHtmlPageToolbar() {
    Toolbar tb = new Toolbar();

    if (!isReadonly()) {
      // 删除按钮
      tb.addButton(new ToolbarButton().setIcon("ui-icon-trash").setText(getText("label.delete")).setAction("delete"));
    }

    // 搜索按钮
    tb.addButton(this.getDefaultSearchToolbarButton());

    return tb;
  }
}