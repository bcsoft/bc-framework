package cn.bc.identity.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import cn.bc.db.jdbc.RowMapper;
import cn.bc.identity.dao.RoleDao;
import cn.bc.identity.domain.Role;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;

/**
 * 角色Dao接口的实现
 * 
 * @author dragon
 * 
 */
public class RoleDaoImpl extends HibernateCrudJpaDao<Role> implements RoleDao {
	private static Log logger = LogFactory.getLog(RoleDaoImpl.class);

	public List<Map<String, String>> find4option(Integer[] actorTypes,
			Integer[] actorStatues) {
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select a.id,a.type_,a.code,a.name");
		hql.append(" from BC_IDENTITY_ROLE a");

		boolean isWhere = true;
		// 类型
		if (actorTypes != null && actorTypes.length > 0) {
			isWhere = false;
			if (actorTypes.length == 1) {
				hql.append(" where a.type_=?");
				args.add(actorTypes[0]);
			} else {
				hql.append(" where a.type_ in (?");
				args.add(actorTypes[0]);
				for (int i = 1; i < actorTypes.length; i++) {
					hql.append(",?");
					args.add(actorTypes[i]);
				}
				hql.append(")");
			}
		}

		// 状态
		if (actorStatues != null && actorStatues.length > 0) {
			if (actorStatues.length == 1) {
				hql.append((isWhere ? " where" : " and") + " a.status_=?");
				args.add(actorStatues[0]);
			} else {
				hql.append((isWhere ? " where" : " and") + " a.status_ in (?");
				args.add(actorStatues[0]);
				for (int i = 1; i < actorStatues.length; i++) {
					hql.append(",?");
					args.add(actorStatues[i]);
				}
				hql.append(")");
			}
		}

		// 排序
		hql.append(" order by a.order_");
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql.toString());
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args));
		}

		return HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(),
				hql.toString(), args.toArray(),
				new RowMapper<Map<String, String>>() {
					public Map<String, String> mapRow(Object[] rs, int rowNum) {
						Map<String, String> map = new HashMap<String, String>();
						int i = 0;
						map.put("id", rs[i++].toString());
						map.put("type", rs[i++].toString());
						map.put("code", rs[i++].toString());
						map.put("name", rs[i++].toString());
						return map;
					}
				});
	}
}
