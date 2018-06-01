package cn.bc.device.web.struts2;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.DateUtils;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.formater.AbstractFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

/**
 * 设备Action
 *
 * @author luliang
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class DevicesAction extends ViewAction<Map<String, Object>> {
  private static final long serialVersionUID = 1L;
  public String status = String.valueOf(BCConstants.STATUS_ENABLED);// 设备的状态

  @Override
  public boolean isReadonly() {
    // 设备管理或系统管理员
    SystemContext context = (SystemContext) this.getContext();
    return !context.hasAnyRole("BC_DEVICE_MANAGE",
      getText("key.role.bc.admin"));
  }

  @Override
  protected SqlObject<Map<String, Object>> getSqlObject() {
    SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

    // 构建查询sql语句
    StringBuffer sql = new StringBuffer(
      "select d.id,d.status_,d.code code,d.model,d.name,d.purpose,d.buy_date");
    sql.append(",d.sn sn,md.actor_name modifier,d.modified_date");
    sql.append(" from bc_device d");
    sql.append(" inner join bc_identity_actor_history md on md.id = d.modifier_id");
    sql.append(" inner join bc_identity_actor_history ad on ad.id = d.author_id");
    sqlObject.setSql(sql.toString());
    // 数据映射器
    sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
      public Map<String, Object> mapRow(Object[] rs, int rowNum) {
        Map<String, Object> map = new HashMap<String, Object>();
        int i = 0;
        map.put("id", rs[i++]);
        map.put("status", rs[i++]);
        map.put("code", rs[i++]);
        map.put("model", rs[i++]);
        map.put("name", rs[i++]);
        map.put("purpose", rs[i++]);
        map.put("buy_date", rs[i++]);
        map.put("sn", rs[i++]);
        map.put("modifier", rs[i++]);
        map.put("modified_date", rs[i++]);
        return map;
      }
    });

    return sqlObject;
  }

  @Override
  protected List<Column> getGridColumns() {
    List<Column> columns = new ArrayList<Column>();
    columns.add(new IdColumn4MapKey("d.id", "id"));

    columns.add(new TextColumn4MapKey("d.status_", "status",
      getText("device.status"), 60).setSortable(true)
      .setValueFormater(new EntityStatusFormater(getDeviceStatus()))
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("d.code", "code",
      getText("device.code"), 80).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("d.model", "model",
      getText("device.model"), 80).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("d.name", "name",
      getText("device.name"), 150).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("d.purpose", "purpose",
      getText("device.purpose")).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("d.buy_date", "buy_date",
      getText("device.buyDate"), 100).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("d.sn", "sn", getText("device.sn"),
      150).setSortable(true).setUseTitleFromLabel(true));
    // 最后修改人信息
    columns.add(new TextColumn4MapKey("md.actor_name", "modifier",
      getText("device.modifier"), 200).setSortable(true)
      .setValueFormater(new AbstractFormater<Object>() {
        @Override
        public Object format(Object context, Object value) {
          @SuppressWarnings("unchecked")
          Map<String, Object> map = (Map<String, Object>) context;
          return value
            + " ("
            + DateUtils.formatDateTime2Minute((Date) map
            .get("modified_date")) + ")";
        }
      }).setUseTitleFromLabel(true));

    return columns;
  }

  @Override
  protected String getGridRowLabelExpression() {
    return "['model'] +'型号'+['name']";
  }

  @Override
  protected Condition getGridSpecalCondition() {
    // 状态条件
    Condition statusCondition = null;
    if (status != null && status.length() > 0) {
      String[] ss = status.split(",");
      if (ss.length == 1) {
        statusCondition = new EqualsCondition("d.status_", new Integer(
          ss[0]));
      } else {
        statusCondition = new InCondition("d.status_",
          StringUtils.stringArray2IntegerArray(ss));
      }
    } else {
      return null;
    }

    return statusCondition;
  }

  @Override
  protected String[] getGridSearchFields() {
    // 按“型号”、“名称”、“编码”、“序列号”、“用途”查找
    return new String[]{"d.model", "d.name", "d.code", "d.sn",
      "d.purpose"};
  }

  @Override
  protected OrderCondition getGridDefaultOrderCondition() {
    // 默认排序方向：状态|购买日期|创建时间
    return new OrderCondition("d.status_", Direction.Asc).add("d.buy_date",
      Direction.Desc).add("d.file_date", Direction.Desc);
  }

  @Override
  protected String getHtmlPageTitle() {
    return this.getText("device.title");
  }

  @Override
  protected PageOption getHtmlPageOption() {
    return super.getHtmlPageOption().setWidth(800).setHeight(400)
      .setMinWidth(300).setMinHeight(200);
  }

  @Override
  protected String getModuleContextPath() {
    return this.getContextPath() + "/bc";
  }

  @Override
  protected String getFormActionName() {
    return "device";
  }

  @Override
  protected Toolbar getHtmlPageToolbar() {
    Toolbar t = new Toolbar();
    if (!isReadonly()) {
      // 新建
      t.addButton(getDefaultCreateToolbarButton());
      // 编辑
      t.addButton(getDefaultEditToolbarButton());
      // 删除
      t.addButton(getDefaultDeleteToolbarButton());
      // 状态
      t.addButton(Toolbar.getDefaultToolbarRadioGroup(getDeviceStatus(),
        "status", 0, getText("title.click2changeSearchStatus")));
    } else {
      // 查看
      t.addButton(getDefaultOpenToolbarButton());
    }
    // 搜索
    t.addButton(getDefaultSearchToolbarButton());
    return t;
  }

  // 设备状态列表
  private Map<String, String> getDeviceStatus() {
    Map<String, String> statuses = new LinkedHashMap<String, String>();
    // 使用中
    statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
      getText("device.status.enabled"));
    // 已禁用
    statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
      getText("device.status.disabled"));
    // 全部
    statuses.put("", getText("bc.status.all"));
    return statuses;
  }

}
