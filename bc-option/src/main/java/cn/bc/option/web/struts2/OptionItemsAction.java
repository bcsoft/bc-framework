/**
 *
 */
package cn.bc.option.web.struts2;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.FooterButton;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

/**
 * 选项条目视图Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class OptionItemsAction extends ViewAction<Map<String, Object>> {
  public String status = String.valueOf(BCConstants.STATUS_ENABLED);

  private static final long serialVersionUID = 1L;

  @Override
  protected String getFormActionName() {
    return "optionItem";
  }

  @Override
  public boolean isReadonly() {
    // 选项管理或系统管理员
    SystemContext context = (SystemContext) this.getContext();
    return !context.hasAnyRole(getText("key.role.bc.option"),
      getText("key.role.bc.admin"));
  }

  @Override
  protected OrderCondition getGridDefaultOrderCondition() {
    return new OrderCondition("g.order_", Direction.Asc).add("i.order_",
      Direction.Asc);
  }

  @Override
  protected SqlObject<Map<String, Object>> getSqlObject() {
    SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

    // 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
    StringBuffer sql = new StringBuffer();
    sql.append("select g.id as pid,g.value_ as pvalue,i.id as id,i.status_ as status,i.key_ as key");
    sql.append(",i.value_ as value,i.order_ as orderNo,i.icon as icon,i.desc_ as desc");
    sql.append(" from bc_option_item as i");
    sql.append(" inner join bc_option_group as g on g.id=i.pid");
    sqlObject.setSql(sql.toString());

    // 注入参数
    sqlObject.setArgs(null);

    // 数据映射器
    sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
      public Map<String, Object> mapRow(Object[] rs, int rowNum) {
        Map<String, Object> map = new HashMap<String, Object>();
        int i = 0;
        map.put("pid", rs[i++]);
        map.put("pvalue", rs[i++]);
        map.put("id", rs[i++]);
        map.put("status", rs[i++]);
        map.put("key", rs[i++]);
        map.put("value", rs[i++]);
        map.put("orderNo", rs[i++]);
        map.put("icon", rs[i++]);
        map.put("desc", rs[i++]);
        return map;
      }
    });
    return sqlObject;
  }

  @Override
  protected List<Column> getGridColumns() {
    List<Column> columns = new ArrayList<Column>();
    columns.add(new IdColumn4MapKey("i.id", "id"));
    columns.add(new TextColumn4MapKey("i.status_", "status",
      getText("label.status"), 60).setSortable(true)
      .setValueFormater(new KeyValueFormater(this.getStatuses())));
    columns.add(new TextColumn4MapKey("i.order_", "orderNo",
      getText("label.order"), 70).setSortable(true));
    columns.add(new TextColumn4MapKey("g.value_", "pvalue",
      getText("option.optionGroup"), 120).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("i.key_", "key",
      getText("option.key")).setSortable(true).setUseTitleFromLabel(
      true));
    columns.add(new TextColumn4MapKey("i.value_", "value",
      getText("option.value"), 200).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("i.desc_", "desc",
      getText("option.desc"), 200).setSortable(false));

    return columns;
  }

  // 状态键值转换
  private Map<String, String> getStatuses() {
    Map<String, String> statuses = new LinkedHashMap<String, String>();
    statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
      getText("optionItem.status.normal"));
    statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
      getText("optionItem.status.disabled"));
    statuses.put("", getText("optionItem.status.all"));
    return statuses;
  }

  @Override
  protected Toolbar getHtmlPageToolbar() {
    Toolbar tb = new Toolbar();

    if (this.isReadonly()) {
      // 查看按钮
      tb.addButton(getDefaultOpenToolbarButton());
    } else {
      // 新建按钮
      tb.addButton(getDefaultCreateToolbarButton());

      // 编辑按钮
      tb.addButton(getDefaultEditToolbarButton());

      // 删除按钮
      tb.addButton(getDefaultDeleteToolbarButton());
    }

    // 状态按钮组
    tb.addButton(Toolbar.getDefaultToolbarRadioGroup(this.getStatuses(),
      "status", 0, getText("optionItem.status.tips")));

    // 搜索按钮
    tb.addButton(getDefaultSearchToolbarButton());

    return tb;
  }

  @Override
  protected Condition getGridSpecalCondition() {
    // 状态条件
    Condition condition = null;
    if (status != null && status.length() > 0) {
      condition = new AndCondition(new EqualsCondition("i.status_",
        Integer.parseInt(status)));
    }

    return condition;
  }

  @Override
  protected String getGridRowLabelExpression() {
    return "['value']";
  }

  @Override
  protected String[] getGridSearchFields() {
    return new String[]{"g.key_", "g.value_", "i.order_", "i.value_",
      "i.key_", "i.icon"};
  }

  @Override
  protected PageOption getHtmlPageOption() {
    return super.getHtmlPageOption().setWidth(650).setHeight(350)
      .setMinWidth(200).setMinHeight(200);
  }

  @Override
  protected FooterButton getGridFooterImportButton() {
    // 获取默认的导入按钮设置
    FooterButton fb = this.getDefaultGridFooterImportButton();

    // 配置特殊参数
    JsonObject cfg = new JsonObject();
    cfg.addProperty("tplCode", "IMPORT_OPTION");// 模板编码
    cfg.addProperty("importAction", "bc/option/import");// 导入数据的action路径(使用相对路径)
    cfg.addProperty("headerRowIndex", 1);// 列标题所在行的索引号(0-based)
    fb.setAttr("data-cfg", cfg.toString());

    // 返回导入按钮
    return fb;
  }
}
