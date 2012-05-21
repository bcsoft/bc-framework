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

	public boolean isUniqueCodeAndVersion(Long currentId, String code,
			String version) {
		Condition c;
		if (currentId == null) {
			c = new AndCondition().add(new EqualsCondition("code", code)).add(
					new EqualsCondition("version", version));

		} else {
			c = new AndCondition().add(new EqualsCondition("code", code))
					.add(new NotEqualsCondition("id", currentId))
					.add(new EqualsCondition("version", version));
		}
		return this.createQuery().condition(c).count() > 0;
	}
	
	
	public Template loadByCodeAndId(String code,Long id){
		if(code == null || id == null)
			return null;
		AndCondition c = new AndCondition();
		c.add(new EqualsCondition("code", code));
		//状态正常
		c.add(new EqualsCondition("status", BCConstants.STATUS_ENABLED));
		
		//id不等于本对象
		c.add(new NotEqualsCondition("id",id));
		return this.createQuery().condition(c).singleResult();
	}
	
	//模板分类
	public List<Map<String, String>> findCategoryOption() {
		String hql="SELECT a.category,1";
		   hql+=" FROM bc_template a";
		   hql+=" GROUP a.category";
		 return	HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), hql,null
		 	,new RowMapper<Map<String, String>>() {
				public Map<String, String> mapRow(Object[] rs, int rowNum) {
					Map<String, String> oi = new HashMap<String, String>();
					int i = 0;
					oi.put("value", rs[i++].toString());
					return oi;
				}
		});
	}
}
