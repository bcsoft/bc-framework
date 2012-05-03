package cn.bc.report.dao.hibernate.jpa;

import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.report.dao.ReportTemplateDao;
import cn.bc.report.domain.ReportTemplate;

/**
 * 报表模板Dao接口的实现
 * 
 * @author dragon
 * 
 */
public class ReportTemplateDaoImpl extends HibernateCrudJpaDao<ReportTemplate>
		implements ReportTemplateDao {

	public ReportTemplate loadByCode(String code) {
		return this.createQuery().condition(new EqualsCondition("code", code))
				.singleResult();
	}
}
