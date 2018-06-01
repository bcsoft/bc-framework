/**
 *
 */
package cn.bc.web.struts2;

import cn.bc.core.query.Query;
import cn.bc.core.query.condition.Condition;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.orm.jpa.JpaNativeQuery;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * 可输入可选下拉框支持的抽象Action（使用Hibernate的JPA执行数据的原生查询）
 *
 * @author dragon
 */
public abstract class AbstractRichInputWithJpaAction<T extends Object> extends AbstractRichInputAction<T> {
  private static final long serialVersionUID = 1L;
  private EntityManager entityManager;

  public EntityManager getEntityManager() {
    return entityManager;
  }

  @PersistenceContext
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  protected List<T> find(String value) {
    // 构建查询对象
    Query<T> query = new JpaNativeQuery<T>(this.getEntityManager(), getSqlObject());

    // 注入查询条件
    query.condition(this.getCondition(value));

    // 返回查询结果
    return query.list();
  }

  /**
   * 获取查询条件
   *
   * @param value 查询条件的原始值
   * @return
   */
  protected abstract Condition getCondition(String value);

  /**
   * 获取原生查询对象的封装
   *
   * @return
   */
  protected abstract SqlObject<T> getSqlObject();
}