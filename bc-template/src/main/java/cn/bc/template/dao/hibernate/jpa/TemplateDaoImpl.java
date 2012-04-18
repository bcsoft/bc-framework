package cn.bc.template.dao.hibernate.jpa;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.NotEqualsCondition;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.template.dao.TemplateDao;
import cn.bc.template.domain.Template;

/**
 * DAO接口的实现
 * 
 * @author lbj
 * 
 */
public class TemplateDaoImpl extends HibernateCrudJpaDao<Template> implements
		TemplateDao {

	public Template loadByCode(String code) {
		return this.createQuery().condition(new EqualsCondition("code", code))
				.singleResult();
	}

	public boolean isUnique(Long currentId, String code) {
		Condition c;
		if (currentId == null) {
			c = new EqualsCondition("code", code);
		} else {
			c = new AndCondition().add(new EqualsCondition("code", code)).add(
					new NotEqualsCondition("id", currentId));
		}
		return this.createQuery().condition(c).count() > 0;
	}



}
