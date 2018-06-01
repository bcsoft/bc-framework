/**
 *
 */
package cn.bc.web.struts2.jpa;

import cn.bc.core.query.Query;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.orm.jpa.JpaNativeQuery;
import cn.bc.web.struts2.AbstractGridPageWithExportAction;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * 基于JPA原生查询的视图Action封装
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public abstract class ViewActionWithJpa<T extends Object> extends AbstractGridPageWithExportAction<T> {
  private static final long serialVersionUID = 1L;
  private EntityManager entityManager;

  public EntityManager getEntityManager() {
    return entityManager;
  }

  @PersistenceContext
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /**
   * 获取sql查询配置对象
   */
  protected abstract SqlObject<T> getSqlObject();

  @Override
  protected Query<T> getQuery() {
    return new JpaNativeQuery<>(this.getEntityManager(), getSqlObject());
  }
}