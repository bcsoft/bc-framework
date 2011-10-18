/**
 * 
 */
package cn.bc.web.struts2.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Controller;

import cn.bc.core.query.Query;
import cn.bc.db.jdbc.SqlObject;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;
import cn.bc.web.struts2.AbstractGridPageAction;

/**
 * 基于JPA原生查询的视图Action封装
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public abstract class ViewActionWithJpa<T extends Object> extends
		AbstractGridPageAction<T> {
	private static final long serialVersionUID = 1L;
	protected JpaTemplate jpaTemplate;

	@Autowired
	public void setJpaTemplate(JpaTemplate jpaTemplate) {
		this.jpaTemplate = jpaTemplate;
	}

	/** 获取sql查询配置对象 */
	protected abstract SqlObject<T> getSqlObject();

	@Override
	protected Query<T> getQuery() {
		return new HibernateJpaNativeQuery<T>(jpaTemplate, getSqlObject());
	}
}
