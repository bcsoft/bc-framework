package cn.bc.identity.dao.hibernate.jpa;

import cn.bc.db.jdbc.RowMapper;
import cn.bc.identity.dao.RoleDao;
import cn.bc.identity.domain.Role;
import cn.bc.orm.jpa.JpaCrudDao;
import cn.bc.orm.jpa.JpaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色Dao接口的实现
 *
 * @author dragon
 */
public class RoleDaoImpl extends JpaCrudDao<Role> implements RoleDao {
	private static Logger logger = LoggerFactory.getLogger(RoleDaoImpl.class);

	public List<Map<String, String>> find4option(Integer[] actorTypes, Integer[] actorStatues) {
		ArrayList<Object> args = new ArrayList<>();
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
				hql.append(isWhere ? " where" : " and").append(" a.status_=?");
				args.add(actorStatues[0]);
			} else {
				hql.append(isWhere ? " where" : " and").append(" a.status_ in (?");
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

		return executeNativeQuery(hql.toString(), args, new RowMapper<Map<String, String>>() {
			public Map<String, String> mapRow(Object[] rs, int rowNum) {
				Map<String, String> map = new HashMap<>();
				int i = -1;
				map.put("id", rs[++i].toString());
				map.put("type", rs[++i].toString());
				map.put("code", rs[++i].toString());
				map.put("name", rs[++i].toString());
				return map;
			}
		});
	}

	@Override
	public String getRoleNameById(Long roleId) {
		return JpaUtils.getSingleResult(
				JpaUtils.createQuery(getEntityManager(), "select name from Role where id = ?1", new Object[]{roleId})
		);
	}

	@Override
	public int addResource(Long roleId, Long[] resourceIds) {
		int p = 2;
		String sql = "insert into bc_identity_role_resource (rid, sid)\n";
		for (int i = 0; i < resourceIds.length; i++) {
			if (i > 0) sql += "\n  union\n";
			sql += "  select ?1, ?" + p + " from bc_dual where not exists (" +
					"select 0 from bc_identity_role_resource where rid = ?1 and sid = ?" + p +
					")";
			p++;
		}
		Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter(1, roleId);
		for (int j = 0; j < resourceIds.length; j++) {
			query.setParameter(j + 2, resourceIds[j]);
		}

		logger.debug("roleId={}, resourceIds={}, sql={}", roleId, resourceIds, sql);
		return query.executeUpdate();
	}

	@Override
	public int deleteResource(Long roleId, Long[] resourceIds) {
		Assert.notNull(roleId);
		Assert.notEmpty(resourceIds);
		String sql = "delete from bc_identity_role_resource where rid = :rid and sid in :sids";
		logger.debug("roleId={}, resourceIds={}, sql={}", roleId, resourceIds, sql);
		Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("rid", roleId);
		List<Object> sids = new ArrayList<>();
		for (Long sid : resourceIds) sids.add(sid);
		query.setParameter("sids", sids);
		return query.executeUpdate();
	}
}