package cn.bc.template.dao.hibernate.jpa;

import cn.bc.BCConstants;
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
		return this.createQuery().condition(new AndCondition().add(
				new EqualsCondition("code", code),new EqualsCondition("status", BCConstants.STATUS_ENABLED)))
				.singleResult();
	}

	public boolean isUniqueCodeAndVersion(Long currentId, String code,String version) {
		Condition c;
		if (currentId == null) {
			c =new AndCondition().add(new EqualsCondition("code", code))
					.add(new EqualsCondition("version", version));
			
		} else {
			c = new AndCondition().add(new EqualsCondition("code", code)).add(
					new NotEqualsCondition("id", currentId)).add(new EqualsCondition("version", version));
		}
		return this.createQuery().condition(c).count() > 0;
	}



}
