/**
 *
 */
package cn.bc.scheduler.web.struts2;

import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.scheduler.service.SchedulerManage;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.formater.EntityStatusFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.Grid;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import cn.bc.web.ui.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时任务视图
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ScheduleJobViewAction extends ViewAction<Map<String, Object>> {
  private static final long serialVersionUID = 1L;
  public String ids; // 批量处理的id，多个id间用逗号连接

  @Autowired
  private SchedulerManage schedulerManage;

  @Override
  public boolean isReadonly() {
    // 组织架构管理或系统管理员
    SystemContext context = (SystemContext) this.getContext();
    return schedulerManage.isDisabled() || !context.hasAnyRole("BC_ORGANIZE_MANAGE", "BC_ADMIN");
  }

  @Override
  protected String getHtmlPageTitle() {
    return this.getText("scheduleJob.title");
  }

  @Override
  protected String getHtmlPageNamespace() {
    return this.getContextPath() + "/bc/schedule/job";
  }

  @Override
  protected PageOption getHtmlPageOption() {
    return super.getHtmlPageOption().setWidth(700).setMinWidth(300).setHeight(400).setMinHeight(300);
  }

  @Override
  protected void addJsCss(List<String> container) {
    container.add("bc/schedule/job/view.js");
  }

  @Override
  protected OrderCondition getGridDefaultOrderCondition() {
    return new OrderCondition("j.status_", Direction.Asc).add("j.order_", Direction.Asc);
  }

  @Override
  protected String[] getGridSearchFields() {
    return new String[]{"j.name", "j.bean", "j.method"};
  }

  @Override
  protected String getGridRowLabelExpression() {
    return "['name']";
  }

  @Override
  protected Grid getHtmlPageGrid() {
    return super.getHtmlPageGrid().setSingleSelect(true);// 单选
  }

  @Override
  protected SqlObject<Map<String, Object>> getSqlObject() {
    SqlObject<Map<String, Object>> sqlObject = new SqlObject<>();

    // 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
    sqlObject.setSql("SELECT id, status_, name, cron, bean, method, order_, ignore_error FROM bc_sd_job j");

    // 注入参数
    sqlObject.setArgs(null);

    // 数据映射器
    sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
      public Map<String, Object> mapRow(Object[] rs, int rowNum) {
        Map<String, Object> map = new HashMap<>();
        int i = 0;
        map.put("id", rs[i++]);
        map.put("status_", rs[i++]);
        map.put("name", rs[i++]);
        map.put("cron", rs[i++]);
        map.put("bean", rs[i++]);
        map.put("method", rs[i++]);
        map.put("order_", rs[i++]);
        map.put("ignore_error", rs[i]);
        return map;
      }
    });
    return sqlObject;
  }

  @Override
  protected List<Column> getGridColumns() {
    List<Column> columns = new ArrayList<>();
    columns.add(new IdColumn4MapKey("j.id", "id"));
    columns.add(new TextColumn4MapKey("j.status_", "status_", getText("scheduleJob.status"), 60)
      .setValueFormater(new EntityStatusFormater(getEntityStatuses())));
    columns.add(new TextColumn4MapKey("j.name", "name", getText("scheduleJob.name")).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("j.cron", "cron", getText("scheduleJob.cron"), 150).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("j.ignore_error", "ignore_error", getText("scheduleJob.ignoreError"), 65)
      .setSortable(true).setValueFormater(new BooleanFormater()));
    columns.add(new TextColumn4MapKey("j.bean", "bean", getText("scheduleJob.bean"), 150)
      .setSortable(true).setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("j.method", "method", getText("scheduleJob.method"), 150)
      .setSortable(true).setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("j.order_", "order_", getText("label.order"), 40).setSortable(true));
    return columns;
  }

  @Override
  protected Toolbar getHtmlPageToolbar() {
    Toolbar tb = new Toolbar();

    if (!this.isReadonly()) {
      // 新建
      tb.addButton(getDefaultCreateToolbarButton());
      // 编辑
      tb.addButton(getDefaultEditToolbarButton());
      // 启动/重置
      tb.addButton(new ToolbarButton().setIcon("ui-icon ui-icon-lightbulb")
        .setText(getText("scheduleJob.button.start")).setClick("start"));
      // 停止/禁用
      tb.addButton(new ToolbarButton().setIcon("ui-icon-cancel")
        .setText(getText("scheduleJob.button.stop")).setClick("stop"));
    } else {
      // 查看
      tb.addButton(getDefaultOpenToolbarButton());
    }

    // 查看调度日志
    tb.addButton(new ToolbarButton().setIcon("ui-icon-calendar")
      .setText(getText("scheduleJob.button.showLog"))
      .setClick("showLog"));

    // 搜索
    tb.addButton(getDefaultSearchToolbarButton());

    return tb;
  }


  // 启动/重置
  public String start() throws Exception {
    if (this.ids == null || this.ids.length() == 0) {
      throw new CoreException("must set property ids");
    }

    Long[] ids = cn.bc.core.util.StringUtils.stringArray2LongArray(this
      .ids.split(","));
    for (Long id : ids) {
      this.schedulerManage.scheduleJob(id);
    }

    Json json = new Json();
    json.put("msg", "任务启动/重置成功！");
    this.json = json.toString();
    return "json";
  }

  // 停止/禁用
  public String stop() throws Exception {
    if (this.ids == null || this.ids.length() == 0) {
      throw new CoreException("must set property ids");
    }

    Long[] ids = StringUtils.stringArray2LongArray(this.ids.split(","));
    for (Long id : ids) this.schedulerManage.stopJob(id);

    Json json = new Json();
    json.put("msg", "任务停止/禁用成功！");
    this.json = json.toString();
    return "json";
  }
}