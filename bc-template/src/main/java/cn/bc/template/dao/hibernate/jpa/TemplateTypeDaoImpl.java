package cn.bc.template.dao.hibernate.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.NotEqualsCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;
import cn.bc.template.dao.TemplateTypeDao;
import cn.bc.template.domain.TemplateType;

/**
 * 模板类型DAO接口的实现
 * 
 * @author lbj
 * 
 */
public class TemplateTypeDaoImpl extends HibernateCrudJpaDao<TemplateType> implements
	TemplateTypeDao {

	public boolean isUniqueCode(Long currentId, String code) {
		Condition c;
		if (currentId == null) {
			c = new AndCondition().add(new EqualsCondition("code", code));

		} else {
			c = new AndCondition().add(new EqualsCondition("code", code))
					.add(new NotEqualsCondition("id", currentId));
		}
		return this.createQuery().condition(c).count() > 0;
	}

	public List<Map<String, String>> findTemplateTypeOption() {
		String hql="SELECT a.id,a.name";
		   hql+=" FROM bc_template_type a";
		   hql+=" WHERE a.status_="+BCConstants.STATUS_ENABLED; 
		 return	HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), hql,null
		 	,new RowMapper<Map<String, String>>() {
				public Map<String, String> mapRow(Object[] rs, int rowNum) {
					Map<String, String> oi = new HashMap<String, String>();
					int i = 0;
					oi.put("id", rs[i++].toString());
					oi.put("name", rs[i++].toString());
					return oi;
				}
		});
	}
}
