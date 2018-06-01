/**
 *
 */
package cn.bc.template.web.struts2.select;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.web.formater.BooleanFormater;
import cn.bc.web.struts2.AbstractSelectPageAction;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.HiddenColumn4MapKey;
import cn.bc.web.ui.html.grid.IdColumn4MapKey;
import cn.bc.web.ui.html.grid.TextColumn4MapKey;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板接口Action
 *
 * @author lbj
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectTemplateAction extends
  AbstractSelectPageAction<Map<String, Object>> {
  private static final long serialVersionUID = 1L;
  public String status = String.valueOf(BCConstants.STATUS_ENABLED); //状态
  public String category;//所属分类(格式为：xxx,xxx,...)

  @Override
  protected OrderCondition getGridDefaultOrderCondition() {
    // 默认排序方向：排序号
    return new OrderCondition("c.full_sn", Direction.Asc).add("t.order_", Direction.Asc);
  }

  @Override
  protected SqlObject<Map<String, Object>> getSqlObject() {
    SqlObject<Map<String, Object>> sqlObject = new SqlObject<>();

    // 构建查询语句,where和order by不要包含在sql中(要统一放到condition中)
    StringBuffer sql = new StringBuffer();
    // 递归获取分类及其后代分类的信息
    sql.append("with recursive c(id, name, full_name, full_sn) as (");
    sql.append(" select c1.id, c1.name_, CAST(c1.name_ AS TEXT), CAST(c1.sn AS TEXT)");// 父节点
    sql.append(" from bc_category c1");
    sql.append(" where c1.id in (");
    sql.append("  select category_get_id_by_full_code(full_code)");
    sql.append("   from unnest(string_to_array('" + this.category + "', ',')) full_code");
    sql.append(" ) union all");
    sql.append(" select c2.id, c2.name_, c.full_name || '/' || c2.name_, c.full_sn || c2.sn");// 后代节点
    sql.append(" from bc_category c2 inner join c on c.id = c2.pid");
    sql.append(")");

    sql.append(" !!select t.id, c.name category_name, t.subject, t.version_, t.formatted, tt.name type_name, t.desc_, t.path, t.size_, t.code");
    sql.append(",(select string_agg(b.pid||'',',') from bc_template_template_param b where b.tid=t.id) as params");// 模板参数
    sql.append(" !!from bc_template t");
    sql.append(" inner join bc_template_type tt on tt.id = t.type_id");
    sql.append(" inner join bc_template_template_category tc on tc.tid = t.id");
    sql.append(" inner join c on c.id = tc.cid");
    sqlObject.setSql(sql.toString());

    // 注入参数
    sqlObject.setArgs(null);

    // 数据映射器
    sqlObject.setRowMapper(new RowMapper<Map<String, Object>>() {
      public Map<String, Object> mapRow(Object[] rs, int rowNum) {
        Map<String, Object> map = new HashMap<>();
        int i = 0;
        map.put("id", rs[i++]);
        map.put("category_name", rs[i++]);
        map.put("subject", rs[i++]);
        map.put("version", rs[i++]);
        map.put("formatted", rs[i++]);
        map.put("typeName", rs[i++]);
        map.put("desc_", rs[i++]);
        map.put("path", rs[i++]);
        map.put("size", rs[i++]);
        map.put("code", rs[i++]);
        map.put("params", rs[i++]);
        return map;
      }
    });
    return sqlObject;
  }

  @Override
  protected List<Column> getGridColumns() {
    List<Column> columns = new ArrayList<>();
    columns.add(new IdColumn4MapKey("t.id", "id"));

    columns.add(new TextColumn4MapKey("c.name", "category_name", getText("template.category"), 150).setUseTitleFromLabel(true));// 所属分类
    columns.add(new TextColumn4MapKey("t.subject", "subject", getText("template.tfsubject"), 300).setUseTitleFromLabel(true));// 主题
    columns.add(new TextColumn4MapKey("t.desc_", "desc_", getText("template.desc")).setUseTitleFromLabel(true));// 描述
    columns.add(new TextColumn4MapKey("t.version_", "version", getText("template.version"), 200).setUseTitleFromLabel(true));// 版本
    columns.add(new TextColumn4MapKey("tt.name", "typeName", getText("template.format"), 130).setUseTitleFromLabel(true));// 模板格式
    columns.add(new TextColumn4MapKey("t.formatted", "formatted", getText("template.file.formatted"), 80).setValueFormater(new BooleanFormater()));// 格式化

    columns.add(new HiddenColumn4MapKey("code", "code"));// 模板编码
    columns.add(new HiddenColumn4MapKey("path", "path"));// 模板路径
    columns.add(new HiddenColumn4MapKey("size", "size"));// 模板size
    columns.add(new HiddenColumn4MapKey("formatted", "formatted"));// 格式化
    columns.add(new HiddenColumn4MapKey("params", "params"));// 模板参数
    return columns;
  }

  @Override
  protected String[] getGridSearchFields() {
    return new String[]{"t.subject", "t.version_", "c.name", "c.full_name"};
  }

  @Override
  protected String getHtmlPageTitle() {
    return this.getText("template.title.selectTemplate");
  }

  @Override
  protected PageOption getHtmlPageOption() {
    return super.getHtmlPageOption().setWidth(600).setHeight(480);
  }

  @Override
  protected String getGridRowLabelExpression() {
    return "['subject']";
  }

  @Override
  protected HtmlPage buildHtmlPage() {
    return super.buildHtmlPage().setNamespace(
      this.getHtmlPageNamespace() + "/selectTemplate");
  }

  @Override
  protected String getHtmlPageJs() {
    return this.getHtmlPageNamespace() + "/template/select.js";
  }

  @Override
  protected Condition getGridSpecalCondition() {
    AndCondition andCondition = new AndCondition();
    if (status != null && status.length() > 0) {
      andCondition.add(new EqualsCondition("t.status_", Integer.parseInt(status)));
    }
    return andCondition;
  }

  @Override
  protected void extendGridExtrasData(JSONObject json) throws JSONException {
    if (status != null && status.length() > 0) {
      json.put("status", status);
    }
    if (category != null && category.length() > 0) {
      json.put("category", category);
    }
  }

  @Override
  protected String getClickOkMethod() {
    return "bc.templateSelectDialog.clickOk";
  }

  @Override
  protected String getHtmlPageNamespace() {
    return this.getContextPath() + BCConstants.NAMESPACE;
  }
}
