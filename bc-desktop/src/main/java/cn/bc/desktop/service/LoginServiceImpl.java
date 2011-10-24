package cn.bc.desktop.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.util.StringUtils;

import cn.bc.db.JdbcUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.desktop.domain.Personal;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.domain.AuthData;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;

/**
 * 专为登录设置的Service接口，目的是不使用事务直接加载相关信息
 * 
 * @author dragon
 * 
 */
public class LoginServiceImpl implements LoginService {
	private static Log logger = LogFactory.getLog(LoginServiceImpl.class);
	private JpaTemplate jpaTemplate;

	public void setJpaTemplate(JpaTemplate jpaTemplate) {
		this.jpaTemplate = jpaTemplate;
	}

	public Map<String, Object> loadActorByCode(final String actorCode) {
		final StringBuffer hql = new StringBuffer(
				"select a.id id,a.type_ type_,a.code code,a.name name,a.pcode pcode,a.pname pname,t.password password,h.id hid from bc_identity_actor a");
		hql.append(" inner join bc_identity_auth t on t.id=a.id");
		hql.append(" inner join bc_identity_actor_history h on h.actor_id=a.id");
		hql.append(" where a.code = ? order by h.create_date desc");
		if (logger.isDebugEnabled()) {
			logger.debug("actorCode=" + actorCode + ",hql=" + hql);
		}
		try {
			return jpaTemplate.execute(new JpaCallback<Map<String, Object>>() {
				public Map<String, Object> doInJpa(EntityManager em)
						throws PersistenceException {
					Query queryObject = em.createNativeQuery(hql.toString());
					// jpaTemplate.prepareQuery(queryObject);

					// 注入参数
					queryObject.setParameter(1, actorCode);// jpa的索引号从1开始
					queryObject.setFirstResult(0);
					queryObject.setMaxResults(1);// 仅获取最新的那条（基于bc_identity_actor_history的create_date）

					return new RowMapper<Map<String, Object>>() {
						public Map<String, Object> mapRow(Object[] rs,
								int rowNum) {
							Map<String, Object> map = new HashMap<String, Object>();

							int i = 0;
							// actor
							Actor actor = new Actor();
							actor.setId(new Long(rs[i++].toString()));
							actor.setType(Integer.parseInt(rs[i++].toString()));
							actor.setCode(rs[i++].toString());
							actor.setName(rs[i++].toString());
							actor.setPcode(rs[i++].toString());
							actor.setPname(rs[i++].toString());

							// auth
							AuthData auth = new AuthData();
							auth.setId(actor.getId());
							auth.setPassword(rs[i++].toString());

							// history
							ActorHistory history = new ActorHistory();
							history.setId(new Long(rs[i++].toString()));
							history.setActorId(actor.getId());
							history.setActorType(actor.getType());
							history.setName(actor.getName());
							history.setPcode(actor.getPcode());
							history.setPname(actor.getPname());

							map.put("actor", actor);
							map.put("auth", auth);
							map.put("history", history);
							return map;
						}
					}.mapRow((Object[]) queryObject.getSingleResult(), 0);
				}
			});
		} catch (NoResultException e) {
			return new HashMap<String, Object>(0);
		}
	}

	public List<Map<String, String>> findActorAncestors(Long actorId) {
		if ("oracle".equals(JdbcUtils.dbtype)) {
			final StringBuffer hql = new StringBuffer();
			hql.append("select distinct ar.follower_id fid,ar.master_id id,m.type_ type,m.code code,m.name name,m.pcode pcode,m.pname pname");
			hql.append(" from BC_IDENTITY_ACTOR_RELATION ar");
			hql.append(" inner join BC_IDENTITY_ACTOR m on m.id = ar.master_id");
			hql.append(" where ar.type_=0");
			hql.append(" start with ar.follower_id = ?");
			hql.append(" connect by prior ar.master_id = ar.follower_id");
			if (logger.isDebugEnabled()) {
				logger.debug("actorId=" + actorId + ",hql=" + hql);
			}
			return HibernateJpaNativeQuery.executeNativeSql(jpaTemplate,
					hql.toString(), new Object[] { actorId },
					new RowMapper<Map<String, String>>() {
						public Map<String, String> mapRow(Object[] rs,
								int rowNum) {
							Map<String, String> actor = new HashMap<String, String>();
							int i = 0;
							actor.put("fid", rs[i++].toString());
							actor.put("id", rs[i++].toString());
							actor.put("type", rs[i++].toString());
							actor.put("code", rs[i++].toString());
							actor.put("name", rs[i++].toString());
							actor.put("pcode", rs[i] != null ? rs[i].toString()
									: null);
							i++;
							actor.put("pname", rs[i] != null ? rs[i].toString()
									: null);
							return actor;
						}
					});
		} else {
			return this.findActorAncestorsDefault(actorId);
		}
	}

	private List<Map<String, String>> findActorAncestorsDefault(Long actorId) {
		logger.fatal("TODO: loadActorUpperDefault");
		return null;
	}

	public List<String> findActorRoles(Long[] actorIds) {
		if (actorIds == null || actorIds.length == 0)
			return new ArrayList<String>();

		final StringBuffer hql = new StringBuffer();
		hql.append("select distinct r.id id,r.code code,r.name name,r.order_ orderNo from BC_IDENTITY_ROLE r");
		hql.append(" inner join BC_IDENTITY_ROLE_ACTOR ra on ra.rid=r.id");
		hql.append(" where r.status_ = 0 and ra.aid");
		if (actorIds.length == 1) {
			hql.append(" = ?");
		} else {
			hql.append(" in (?");
			for (int i = 1; i < actorIds.length; i++) {
				hql.append(",?");
			}
			hql.append(")");
		}
		hql.append(" order by r.order_");
		if (logger.isDebugEnabled()) {
			logger.debug("actorIds="
					+ StringUtils.arrayToCommaDelimitedString(actorIds));
			logger.debug("hql=" + hql);
		}
		return HibernateJpaNativeQuery.executeNativeSql(jpaTemplate,
				hql.toString(), actorIds, new RowMapper<String>() {
					public String mapRow(Object[] rs, int rowNum) {
						return rs[1].toString();
					}
				});
	}

	public Personal loadPersonal(final Long actorId) {
		final StringBuffer hql = new StringBuffer(
				"select p.id,p.uid_,p.status_,p.font,p.theme,p.aid,p.inner_");
		hql.append(" from BC_DESKTOP_PERSONAL p where p.aid=0 or p.aid=? order by p.id desc");
		if (logger.isDebugEnabled()) {
			logger.debug("actorId=" + actorId + ",hql=" + hql);
		}
		try {
			return jpaTemplate.execute(new JpaCallback<Personal>() {
				public Personal doInJpa(EntityManager em)
						throws PersistenceException {
					Query queryObject = em.createNativeQuery(hql.toString());
					// jpaTemplate.prepareQuery(queryObject);

					// 注入参数
					queryObject.setParameter(1, actorId);// jpa的索引号从1开始
					queryObject.setFirstResult(0);
					queryObject.setMaxResults(1);// 仅获取1条

					Personal p = new RowMapper<Personal>() {
						public Personal mapRow(Object[] rs, int rowNum) {
							Personal map = new Personal();
							int i = 0;
							map.setId(new Long(rs[i++].toString()));
							map.setUid(rs[i] != null ? rs[i].toString() : null);
							i++;
							map.setStatus(Integer.parseInt(rs[i++].toString()));
							map.setFont(rs[i++].toString());
							map.setTheme(rs[i++].toString());
							map.setActorId(new Long(rs[i++].toString()));
							map.setInner("1".equals(rs[i++].toString()));

							return map;
						}
					}.mapRow((Object[]) queryObject.getSingleResult(), 0);

					// 如果是全局配置就将其改为当前用户的配置
					if (p.getActorId().equals(0)) {
						p.setActorId(actorId);

					}
					return p;
				}
			});
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Map<String, Object>> findShortcuts(Long[] actorIds) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, Object>> findResources(Long[] actorIds) {
		// TODO Auto-generated method stub
		return null;
	}
}
