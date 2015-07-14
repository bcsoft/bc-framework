package cn.bc.report.dao.hibernate.jpa;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.NotEqualsCondition;
import cn.bc.orm.jpa.JpaCrudDao;
import cn.bc.report.dao.ReportTemplateDao;
import cn.bc.report.domain.ReportTemplate;

/**
 * 报表模板Dao接口的实现
 *
 * @author dragon
 */
public class ReportTemplateDaoImpl extends JpaCrudDao<ReportTemplate> implements ReportTemplateDao {
	public ReportTemplate loadByCode(String code) {
		return this.createQuery().condition(new EqualsCondition("code", code)).singleResult();
	}

	public boolean isUniqueCode(Long currentId, String code) {
		Condition c;
		if (currentId == null) {
			c = new EqualsCondition("code", code);

		} else {
			c = new AndCondition()
					.add(new EqualsCondition("code", code))
					.add(new NotEqualsCondition("id", currentId));
		}
		return this.createQuery().condition(c).count() > 0;
	}
}