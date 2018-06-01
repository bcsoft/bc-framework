/**
 *
 */
package cn.bc.investigate.web.struts2;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.investigate.domain.Question;
import cn.bc.web.formater.CalendarFormater;
import cn.bc.web.struts2.ViewAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import cn.bc.web.ui.html.toolbar.ToolbarButton;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.*;

/**
 * 评分视图 Action
 *
 * @author zxr
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class GradesAction extends ViewAction<Map<String, Object>> {
  private static final long serialVersionUID = 1L;
  public Long id;// 试卷ID
  public String title;// 试卷标题
  public String isGrades = "true";// 默认未评分

  @Override
  protected OrderCondition getGridDefaultOrderCondition() {
    // 默认排序方向：创建日期
    return new OrderCondition("r.file_date", Direction.Asc);

  }

  @Override
  protected SqlObject<Map<String, Object>> getSqlObject() {
    SqlObject<Map<String, Object>> sqlObject = new SqlObject<Map<String, Object>>();

    // 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
    StringBuffer sql = new StringBuffer();
    sql.append("select g.id,q.subject testTitle,qs.subject questionTitle,ad.actor_name answer");
    sql.append(" ,r.file_date answerTime,a.content,ra.actor_name rating,g.file_date ratingTime");
    sql.append(",a.id aid,r.id rid,qs.score,q.id qid,q.status_");
    sql.append(" from bc_ivg_questionary q");
    sql.append(" left join bc_ivg_respond r on r.pid = q.id");
    sql.append(" left join bc_ivg_question qs on qs.pid = q.id");
    sql.append(" left join bc_ivg_question_item i on i.pid = qs.id");
    sql.append(" left join bc_ivg_answer a on (a.qid = i.id and a.rid = r.id)");
    sql.append(" left join bc_ivg_grade g on g.answer_id = a.id");
    sql.append(" left join bc_identity_actor_history ad on ad.id=r.author_id");
    sql.append(" left join bc_identity_actor_history ra on ra.id=g.author_id");
    sqlObject.setSql(sql.toString());

    // 注入参数
    sqlObject.setArgs(null);

    // 数据映射器
    sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
      public Map<String, Object> mapRow(Object[] rs, int rowNum) {
        Map<String, Object> map = new HashMap<String, Object>();
        int i = 0;
        map.put("id", rs[i++]);
        map.put("testTitle", rs[i++]);
        map.put("questionTitle", rs[i++]);
        map.put("answer", rs[i++]);
        map.put("answerTime", rs[i++]);
        map.put("content", rs[i++]);
        map.put("rating", rs[i++]);
        map.put("ratingTime", rs[i++]);
        map.put("aid", rs[i++]);
        map.put("rid", rs[i++]);
        map.put("score", rs[i++]);
        map.put("qid", rs[i++]);
        map.put("status", rs[i++]);
        return map;
      }
    });
    return sqlObject;
  }

  @Override
  protected List<Column> getGridColumns() {
    List<Column> columns = new ArrayList<Column>();
    columns.add(new IdColumn4MapKey("g.id", "id"));
    columns.add(new TextColumn4MapKey("ad.actor_name", "answer",
      getText("checkRespond.answer"), 80).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("r.file_date", "answerTime",
      getText("checkRespond.submitTime"), 120).setSortable(true)
      .setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
    columns.add(new TextColumn4MapKey("qs.subject", "questionTitle",
      getText("grade.questionTitle"), 150).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("a.content", "content",
      getText("grade.answer"), 250).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("ra.actor_name", "rating",
      getText("grade.rating"), 80).setSortable(true)
      .setUseTitleFromLabel(true));
    columns.add(new TextColumn4MapKey("g.file_date", "ratingTime",
      getText("grade.ratingTime"), 120).setSortable(true)
      .setValueFormater(new CalendarFormater("yyyy-MM-dd HH:mm")));
    // 评分表单的内容
    columns.add(new HiddenColumn4MapKey("aid", "aid"));
    columns.add(new HiddenColumn4MapKey("rid", "rid"));
    columns.add(new HiddenColumn4MapKey("score", "score"));
    columns.add(new HiddenColumn4MapKey("testTitle", "testTitle"));
    columns.add(new HiddenColumn4MapKey("qid", "qid"));
    columns.add(new HiddenColumn4MapKey("status", "status"));
    return columns;
  }

  @Override
  protected String[] getGridSearchFields() {
    return new String[]{"ad.actor_name", "qs.subject"};
  }

  @Override
  protected String getFormActionName() {
    return "grade";
  }

  @Override
  protected void extendGridExtrasData(JSONObject json) throws JSONException {
    // 试卷ID
    if (this.id != null) {
      json.put("id", id);
    }
  }

  @Override
  protected Condition getGridSpecalCondition() {
    AndCondition andCondition = new AndCondition();
    // 试卷ID
    Condition idCondition = null;
    idCondition = new EqualsCondition("q.id", new Long(id));
    // 题目类型
    Condition typeCondition = null;
    typeCondition = new EqualsCondition("qs.type_", Question.TYPE_QA);
    andCondition.add(idCondition, typeCondition);
    // 评分
    if (isGrades.equals("false")) {
      Condition gradesCondition = null;
      gradesCondition = new EqualsCondition("a.grade", false);
      andCondition.add(gradesCondition);
    }
    // 未评分
    if (isGrades.equals("true")) {
      Condition onGradesCondition = null;
      onGradesCondition = new EqualsCondition("a.grade", true);
      andCondition.add(onGradesCondition);
    }
    // 全部

    return andCondition;
  }

  @Override
  protected PageOption getHtmlPageOption() {
    return super.getHtmlPageOption().setWidth(900).setMinWidth(100)
      .setHeight(500).setMinHeight(100);
  }

  @Override
  protected String getGridRowLabelExpression() {
    return "['questionTitle']";
  }

  // 视图的标题
  @Override
  protected String getHtmlPageTitle() {
    return "评分：" + this.title;
  }

  @Override
  protected Toolbar getHtmlPageToolbar() {
    Toolbar tb = new Toolbar();
    // 问答题评分按钮
    tb.addButton(new ToolbarButton().setIcon("ui-icon-document")
      .setText("评分").setClick("bc.gradeView.score"));

    // 如果是管理员,可以看到状态按钮组
    tb.addButton(Toolbar.getDefaultToolbarRadioGroup(this.getBSStatuses(),
      "isGrades", 1, getText("title.click2changeGradesStatus")));
    // 搜索按钮
    tb.addButton(this.getDefaultSearchToolbarButton());

    return tb;
  }

  @Override
  protected String getGridDblRowMethod() {
    // 强制为只读表单
    return "bc.gradeView.dblclick";
  }

  @Override
  protected String getHtmlPageJs() {
    return this.getContextPath() + "/bc/grade/view.js";
  }

  /**
   * 状态值转换列表：待评分|已评分|全部
   *
   * @return
   */
  protected Map<String, String> getBSStatuses() {
    Map<String, String> statuses = new LinkedHashMap<String, String>();
    statuses.put("false", getText("questionary.grades.false"));
    statuses.put("true", getText("questionary.grades.true"));
    statuses.put("", getText("questionary.release.all"));
    return statuses;
  }

  // ==高级搜索代码开始==
  @Override
  protected boolean useAdvanceSearch() {
    return false;
  }

  // ==高级搜索代码结束==

}
