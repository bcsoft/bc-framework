package cn.bc.template.dao.jpa;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.NotEqualsCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.orm.jpa.JpaCrudDao;
import cn.bc.template.dao.TemplateDao;
import cn.bc.template.domain.Template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO接口的实现
 *
 * @author lbj
 */
public class TemplateDaoImpl extends JpaCrudDao<Template> implements TemplateDao {
  public Template loadByCode(String code) {
    if (code == null)
      return null;
    int i = code.indexOf(":");
    String version = null;
    if (i != -1) {
      version = code.substring(i + 1);
      code = code.substring(0, i);
    }
    AndCondition c = new AndCondition();
    c.add(new EqualsCondition("code", code));
    if (version != null) {
      c.add(new EqualsCondition("version", version));// 获取指定版本
    } else {
      c.add(new EqualsCondition("status", BCConstants.STATUS_ENABLED));// 获取最新版本
    }
    return this.createQuery().condition(c).singleResult();
  }

  public boolean isUniqueCodeAndVersion(Long currentId, String code, String version) {
    Condition c;
    if (currentId == null) {
      c = new AndCondition().add(new EqualsCondition("code", code))
        .add(new EqualsCondition("version", version));

    } else {
      c = new AndCondition().add(new EqualsCondition("code", code))
        .add(new NotEqualsCondition("id", currentId))
        .add(new EqualsCondition("version", version));
    }
    return this.createQuery().condition(c).count() > 0;
  }


  public Template loadByCodeAndId(String code, Long currentId) {
    if (code == null)
      return null;
    AndCondition c = new AndCondition();
    c.add(new EqualsCondition("code", code));
    //状态正常
    c.add(new EqualsCondition("status", BCConstants.STATUS_ENABLED));

    if (currentId != null) {
      //id不等于本对象
      c.add(new NotEqualsCondition("id", currentId));
    }
    return this.createQuery().condition(c).singleResult();
  }

  //模板分类
  public List<Map<String, String>> findCategoryOption() {
    String hql = "SELECT a.category,1";
    hql += " FROM bc_template a";
    hql += " GROUP BY a.category";
    return executeNativeQuery(hql, (Object[]) null, new RowMapper<Map<String, String>>() {
      public Map<String, String> mapRow(Object[] rs, int rowNum) {
        Map<String, String> oi = new HashMap<>();
        oi.put("value", rs[0].toString());
        return oi;
      }
    });
  }

  public List<Long> findTemplateIdsByCategoryIdForList(Long pid) {
    String sql = "select tid from bc_template_template_category where cid = ?";
    return executeNativeQuery(sql, new Object[]{pid});
  }
}