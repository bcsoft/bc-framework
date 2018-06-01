/**
 *
 */
package cn.bc.scheduler.web.struts2;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.scheduler.service.SchedulerManage;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.DateRangeFormaterEx;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.Grid;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

/**
 * 定时任务调度日志视图
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ScheduleLogViewAction extends ViewAction<Map<String, Object>> {
  private static final long serialVersionUID = 1L;

  @Autowired
  private SchedulerManage schedulerManage;

  @Override
  public boolean isReadonly() {
    return true;
  }

  @Override
  protected String getHtmlPageTitle() {
    return this.getText("scheduleLog.title");
  }

  @Override
  protected String getHtmlPageNamespace() {
    return this.getContextPath() + "/bc/schedule/log";
  }

  @Override
  protected PageOption getHtmlPageOption() {
    return super.getHtmlPageOption().setWidth(700).setMinWidth(300).setHeight(400).setMinHeight(300);
  }

  @Override
  protected OrderCondition getGridDefaultOrderCondition() {
    return new OrderCondition("start_date", Direction.Desc);
  }

  @Override
  protected String[] getGridSearchFields() {
    return new String[]{"cfg_name"};
  }

  @Override
  protected String getGridRowLabelExpression() {
    return "['cfg_name']";
  }

  @Override
  protected Grid getHtmlPageGrid() {
    return super.getHtmlPageGrid().setSingleSelect(true);// 单选
  }

  @Override
  protected SqlObject<Map<String, Object>> getSqlObject() {
    SqlObject<Map<String, Object>> sqlObject = new SqlObject<>();

    // 构建查询语句, where和order by不要包含在sql中(要统一放到condition中)
    sqlObject.setSql("SELECT id, success, cfg_name, start_date, end_date FROM bc_sd_log");

    // 注入参数
    sqlObject.setArgs(null);

    // 数据映射器
    sqlObject.setRowMapper((rs, rowNum) -> {
      Map<String, Object> map = new HashMap<>();
      int i = 0;
      map.put("id", rs[i++]);
      map.put("success", rs[i++]);
      map.put("cfg_name", rs[i++]);
      map.put("start_date", rs[i++]);
      map.put("end_date", rs[i]);
      return map;
    });
    return sqlObject;
  }

  @Override
  protected List<Column> getGridColumns() {
    List<Column> columns = new ArrayList<>();
    columns.add(new IdColumn4MapKey("id", "id"));
    columns.add(new TextColumn4MapKey("success", "success", getText("scheduleLog.success"), 65).setSortable(true)
      .setValueFormater(new BooleanFormater(getText("bc.status.success"), getText("bc.status.fail"))));
    columns.add(new TextColumn4MapKey("cfg_name", "cfg_name", getText("scheduleLog.cfgName"))
      .setSortable(true).setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("start_date", "start_date", getText("scheduleLog.startDate2endDate"), 310)
      .setSortable(true).setDir(Direction.Desc).setValueFormater(new DateRangeFormaterEx() {
        @Override
        @SuppressWarnings("unchecked")
        public Date getToDate(Object context, Object value) {
          return (Date) ((Map<String, Object>) context).get("end_date");
        }
      }));
    return columns;
  }

  @Override
  protected Toolbar getHtmlPageToolbar() {
    Toolbar tb = new Toolbar();

    // 查看
    tb.addButton(getDefaultOpenToolbarButton());
    // 搜索
    tb.addButton(getDefaultSearchToolbarButton());

    return tb;
  }
}