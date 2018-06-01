package cn.bc.report.web.struts2;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.*;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.report.service.ReportHistoryService;
import cn.bc.web.formater.DateRangeFormaterEx;
import cn.bc.web.formater.KeyValueFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

/**
 * 历史报表视图Action
 *
 * @author lbj
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ReportHistorysAction extends ViewAction<Map<String, Object>> {
  private static final long serialVersionUID = 1L;
  public String success = String.valueOf(true);
  public String sourceType;
  public Long sourceId;
  public boolean my = false;// 是否从我的报表打开视图

  @Override
  public boolean isReadonly() {
    SystemContext context = (SystemContext) this.getContext();
    // 配置权限：报表管理员，历史报表管理员、超级管理员
    return !context.hasAnyRole(getText("key.role.bc.report"),
      getText("key.role.bc.report.History"),
      getText("key.role.bc.admin"));
  }

  @Override
  protected OrderCondition getGridOrderCondition() {
    return new OrderCondition("a.start_date", Direction.Desc);
  }

  @Override
  protected SqlObject<Map<String, Object>> getSqlObject() {
    SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();
    // 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
    StringBuffer sql = new StringBuffer();
    sql.append("select a.id,a.success,a.start_date,a.end_date,a.category,a.subject,a.path,b.actor_name as uname,a.source_type as sourceType");
    sql.append(" from bc_report_history a");
    sql.append(" inner join bc_identity_actor_history b on b.id=a.author_id");
    if (my) {
      sql.append(" left join bc_report_task c on c.id=a.source_id ");
    }
    sqlObject.setSql(sql.toString());

    // 注入参数
    sqlObject.setArgs(null);

    // 数据映射器
    sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
      public Map<String, Object> mapRow(Object[] rs, int rowNum) {
        Map<String, Object> map = new HashMap<String, Object>();
        int i = 0;
        map.put("id", rs[i++]);
        map.put("success", rs[i++]);
        map.put("start_date", rs[i++]);
        map.put("end_date", rs[i++]);
        map.put("category", rs[i++]);
        map.put("subject", rs[i++]);
        map.put("path", rs[i++]);
        map.put("uname", rs[i++]);
        map.put("sourceType", rs[i++]);
        return map;
      }
    });
    return sqlObject;
  }

  @Override
  protected List<Column> getGridColumns() {
    List<Column> columns = new ArrayList<Column>();
    columns.add(new IdColumn4MapKey("a.id", "id"));
    columns.add(new TextColumn4MapKey("a.success", "success",
      getText("report.status"), 40).setSortable(true)
      .setValueFormater(new KeyValueFormater(this.getStatuses())));
    columns.add(new TextColumn4MapKey("a.start_date", "start_date",
      getText("report.dateRange"), 220).setUseTitleFromLabel(true)
      .setValueFormater(new DateRangeFormaterEx() {
        @SuppressWarnings("unchecked")
        @Override
        public Date getToDate(Object context, Object value) {
          return (Date) ((Map<String, Object>) context)
            .get("end_date");
        }
      }));
    columns.add(new TextColumn4MapKey("a.source_type", "sourceType",
      getText("reportHistory.source"), 65).setSortable(true));
    columns.add(new TextColumn4MapKey("a.category", "category",
      getText("report.category"), 120).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("a.subject", "subject",
      getText("reportHistory.subject")).setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("a.path", "path",
      getText("reportHistory.path"), 200).setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("b.actor_name", "uname",
      getText("report.author"), 65));
    return columns;
  }

  // 状态键值转换
  private Map<String, String> getStatuses() {
    Map<String, String> statuses = new LinkedHashMap<String, String>();
    statuses.put(String.valueOf(true),
      getText("reportHistory.status.success"));
    statuses.put(String.valueOf(false),
      getText("reportHistory.status.lost"));
    statuses.put("", getText("bc.status.all"));
    return statuses;
  }

  @Override
  protected String getGridRowLabelExpression() {
    return my ? "'我的历史报表：'+['subject']" : "['subject']";
  }

  @Override
  protected String[] getGridSearchFields() {
    return new String[]{"a.subject", "b.actor_name", "a.category"};
  }

  @Override
  protected String getFormActionName() {
    return my ? "myReportHistory" : "reportHistory";
  }

  @Override
  protected PageOption getHtmlPageOption() {
    return super.getHtmlPageOption().setWidth(800).setMinWidth(400)
      .setHeight(400).setMinHeight(300);
  }

  @Override
  protected Toolbar getHtmlPageToolbar() {
    Toolbar tb = new Toolbar();

    // 下载
    tb.addButton(new ToolbarButton().setIcon("ui-icon-arrowthickstop-1-s")
      .setText(getText("label.download"))
      .setClick("bc.reportHistoryList.download"));
    // 在线预览
    tb.addButton(new ToolbarButton().setIcon("ui-icon-lightbulb")
      .setText(getText("reportHistory.priview.inline"))
      .setClick("bc.reportHistoryList.inline"));

    // 删除
    tb.addButton(getDefaultDeleteToolbarButton());

    // 状态按钮组
    tb.addButton(Toolbar.getDefaultToolbarRadioGroup(this.getStatuses(),
      "success", 0, getText("report.status.tips")));

    // 搜索按钮
    tb.addButton(this.getDefaultSearchToolbarButton());

    return tb;
  }

  @Override
  protected Condition getGridSpecalCondition() {
    // 状态条件
    AndCondition andCondition = new AndCondition();
    if (success != null && success.length() > 0) {
      andCondition.add(new EqualsCondition("a.success", Boolean
        .valueOf(success)));
    }
    // 报表任务查看历史
    if (sourceId != null && sourceType != null && sourceType.length() > 0
      && sourceType.equals(getText("reportHistory.source2.task"))) {
      andCondition.add(new EqualsCondition("a.source_type", sourceType));
      andCondition.add(new EqualsCondition("a.source_id", sourceId));
    }
    if (my) {
      // ((a.author_id=100729 and a.source_type = '用户生成' )
      // or
      // (a.source_type = '报表任务' and t.pid in (select r.tid from
      // bc_report_template_actor r where r.aid in (100024,1,100024)))
      // or
      // (a.source_type = '报表任务' and c.pid not in (select r.tid from  bc_report_template_actor r));
      //)
      SystemContext context = (SystemContext) this.getContext();

      // 保存的用户id键值集合
      List<Object> ids = new ArrayList<Object>();
      Long[] aids = context.getAttr(SystemContext.KEY_ANCESTORS);
      for (Long id : aids) {
        ids.add(id);
      }
      // 根据集合数量，生成的占位符字符串
      String qlStr = "";
      for (int i = 0; i < ids.size(); i++) {
        if (i + 1 != ids.size()) {
          qlStr += "?,";
        } else {
          qlStr += "?";
        }
      }
      andCondition.add(
        new OrCondition(
          new AndCondition(
            new EqualsCondition("a.author_id", context.getUserHistory().getId()),
            new EqualsCondition("a.source_type", getText("reportHistory.source2.user"))
          ).setAddBracket(true)
          , new AndCondition(
          new EqualsCondition("a.source_type", getText("reportHistory.source2.task")),
          new QlCondition("c.pid in (select r.tid from  bc_report_template_actor r where r.aid in ("
            + qlStr + "))", ids)
        ).setAddBracket(true)
          , new AndCondition(
          new EqualsCondition("a.source_type", getText("reportHistory.source2.task")),
          new QlCondition("c.pid not in (select r.tid from  bc_report_template_actor r)", new Object[]{})
        ).setAddBracket(true)
        ).setAddBracket(true)
      );
    }
    if (andCondition.isEmpty())
      return null;

    return andCondition;
  }

  @Override
  protected String getHtmlPageJs() {
    return this.getModuleContextPath() + "/report/history/list.js";
  }

  @Override
  protected void extendGridExtrasData(JSONObject json) throws JSONException {
    if (success != null && success.length() > 0l) {
      json.put("success", success);
    }
    if (sourceId != null && sourceType != null && sourceType.length() > 0
      && sourceType.equals(getText("reportHistory.source2.task"))) {
      json.put("sourceType", sourceType);
      json.put("sourceId", sourceId);
    }
    if (my) {
      json.put("my", "true");
    }
  }

  // ==高级搜索代码开始==
  @Override
  protected boolean useAdvanceSearch() {
    return true;
  }

  public ReportHistoryService reportHistoryService;

  @Autowired
  public void setReportHistoryService(
    ReportHistoryService reportHistoryService) {
    this.reportHistoryService = reportHistoryService;
  }

  public JSONArray categorys;// 所属分类下拉列表信息
  public JSONArray sources;

  @Override
  protected void initConditionsFrom() throws Exception {
    if (!(sourceId != null && sourceType != null && sourceType.length() > 0 && sourceType
      .equals(getText("reportHistory.source2.task")))) {
      this.categorys = OptionItem.toLabelValues(this.reportHistoryService
        .findCategoryOption());
      this.sources = OptionItem.toLabelValues(this.reportHistoryService
        .findSourceOption());
    }
  }

  @Override
  public String getAdvanceSearchConditionsJspPath() {
    // 报表任务查看历史
    if (sourceId != null && sourceType != null && sourceType.length() > 0
      && sourceType.equals(getText("reportHistory.source2.task"))) {
      return BCConstants.NAMESPACE + "/report/history/task";
    }
    return BCConstants.NAMESPACE + "/report/history";
  }

  // ==高级搜索代码结束==

}
