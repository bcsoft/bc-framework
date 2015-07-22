package cn.bc.template.dao.jpa;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.NotEqualsCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.orm.jpa.JpaCrudDao;
import cn.bc.template.dao.TemplateTypeDao;
import cn.bc.template.domain.TemplateType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板类型DAO接口的实现
 *
 * @author lbj
 */
public class TemplateTypeDaoImpl extends JpaCrudDao<TemplateType> implements TemplateTypeDao {
	public TemplateType loadByCode(String code) {
		if (code == null)
			return null;
		return this.createQuery().condition(new EqualsCondition("code", code)).singleResult();
	}

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

	public List<Map<String, String>> findTemplateTypeOption(boolean isEnabled) {
		String hql = "SELECT a.id,a.ext,a.name";
		hql += " FROM bc_template_type a";
		if (isEnabled) {
			hql += " WHERE a.status_=" + BCConstants.STATUS_ENABLED;
		}
		hql += " ORDER BY a.order_";
		return executeNativeQuery(hql, (Object[]) null, new RowMapper<Map<String, String>>() {
			public Map<String, String> mapRow(Object[] rs, int rowNum) {
				Map<String, String> oi = new HashMap<>();
				if (rs[1] != null) {
					oi.put("key", rs[0].toString() + "," + rs[1].toString());
				} else {
					oi.put("key", rs[0].toString());
				}
				oi.put("value", rs[2].toString());
				return oi;
			}
		});
	}

	public List<Map<String, String>> findTemplateTypeOptionRtnId(boolean isEnabled) {
		String hql = "SELECT a.id,a.name";
		hql += " FROM bc_template_type a";
		if (isEnabled) {
			hql += " WHERE a.status_=" + BCConstants.STATUS_ENABLED;
		}
		hql += " ORDER BY a.order_";
		return executeNativeQuery(hql, (Object[]) null, new RowMapper<Map<String, String>>() {
			public Map<String, String> mapRow(Object[] rs, int rowNum) {
				Map<String, String> oi = new HashMap<>();
				int i = 0;
				oi.put("key", rs[i++].toString());
				oi.put("value", rs[i].toString());
				return oi;
			}
		});
	}
}