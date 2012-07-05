package cn.bc.desktop.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.util.StringUtils;

import cn.bc.db.JdbcUtils;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.desktop.domain.Personal;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.domain.AuthData;
import cn.bc.identity.domain.Resource;
import cn.bc.identity.service.ResourceService;
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
	private ResourceService resourceService;

	@Autowired
	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public void setJpaTemplate(JpaTemplate jpaTemplate) {
		this.jpaTemplate = jpaTemplate;
	}

	public Map<String, Object> loadActorByCode(final String actorCode) {
		final StringBuffer hql = new StringBuffer(
				"select a.id as id,a.uid_ as uid_,a.type_ as type_,a.code as code,a.name as name");
		hql.append(",a.pcode as pcode,a.pname as pname,t.password as password,h.id as hid");
		hql.append(" from bc_identity_actor as a");
		hql.append(" inner join bc_identity_auth as t on t.id=a.id");
		hql.append(" inner join bc_identity_actor_history as h on h.actor_id=a.id");
		hql.append(" where a.code = ? and h.current='1' order by h.create_date desc");
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
							actor.setUid(rs[i++].toString());
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
							//history.setId(getActorHistoryId(actor.getId()));
							history.setActorId(actor.getId());
							history.setActorType(actor.getType());
							history.setCode(actor.getCode());
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
		} catch (EmptyResultDataAccessException e) {
			return new HashMap<String, Object>(0);
		}
	}

	public List<Map<String, String>> findActorAncestors(Long actorId) {
		if ("oracle".equals(JdbcUtils.dbtype)) {
			StringBuffer hql = new StringBuffer();
			hql.append("select distinct ar.follower_id as fid,ar.master_id as id,m.type_ as type");
			hql.append(",m.code as code,m.name as name,m.pcode as pcode,m.pname as pname");
			hql.append(" from BC_IDENTITY_ACTOR_RELATION as ar");
			hql.append(" inner join BC_IDENTITY_ACTOR as m on m.id = ar.master_id");
			hql.append(" where ar.type_=0");
			hql.append(" start with ar.follower_id = ?");
			hql.append(" connect by prior ar.master_id = ar.follower_id");
			if (logger.isDebugEnabled()) {
				logger.debug("actorId=" + actorId + ",hql=" + hql);
			}
			return HibernateJpaNativeQuery.executeNativeSql(jpaTemplate,
					hql.toString(), new Object[] { actorId },
					new Follower2MasterMapper());
		} else {
			// 使用原始的递归方式获取祖先组织信息
			return this.findActorAncestorsDefault(actorId);
		}
	}

	private List<Map<String, String>> findActorAncestorsDefault(Long actorId) {
		if (actorId == null)
			return null;

		if (logger.isInfoEnabled()) {
			logger.info("findActorAncestorsDefault.actorId=" + actorId);
		}

		List<Map<String, String>> all = new ArrayList<Map<String, String>>();
		Set<Long> followerIds = new HashSet<Long>();
		followerIds.add(actorId);

		// 递归获取祖先组织信息
		recurseFindActorMasters(all, followerIds);

		return all;
	}

	private void recurseFindActorMasters(List<Map<String, String>> all,
			Set<Long> followerIds) {
		List<Map<String, String>> ms = findActorMasters(followerIds
				.toArray(new Long[0]));
		if (ms != null && !ms.isEmpty()) {
			all.addAll(ms);

			// 对所有masterId执行递归查询
			Set<Long> masterIds = new HashSet<Long>();
			for (Map<String, String> m : ms) {
				masterIds.add(new Long(m.get("id")));
			}
			recurseFindActorMasters(all, masterIds);
		}
	}

	private List<Map<String, String>> findActorMasters(Long[] followerIds) {
		if (followerIds == null || followerIds.length == 0)
			return null;

		StringBuffer hql = new StringBuffer();
		hql.append("select distinct ar.follower_id as fid,ar.master_id as id,m.type_ as type");
		hql.append(",m.code as code,m.name as name,m.pcode as pcode,m.pname as pname,m.order_ as orderNo");
		hql.append(" from BC_IDENTITY_ACTOR_RELATION as ar");
		hql.append(" inner join BC_IDENTITY_ACTOR as m on m.id = ar.master_id");
		hql.append(" where ar.type_=0");
		hql.append(" and ar.follower_id");
		if (logger.isDebugEnabled()) {
			logger.debug("findActorMasters.hql=" + hql);
			logger.debug("findActorMasters.args="
					+ StringUtils.arrayToCommaDelimitedString(followerIds));
		}
		if (followerIds.length == 1) {
			hql.append(" = ?");
		} else {
			hql.append(" in (?");
			for (int i = 1; i < followerIds.length; i++) {
				hql.append(",?");
			}
			hql.append(")");
		}
		hql.append(" order by m.order_");
		return HibernateJpaNativeQuery.executeNativeSql(jpaTemplate,
				hql.toString(), followerIds, new Follower2MasterMapper());
	}

	class Follower2MasterMapper implements RowMapper<Map<String, String>> {
		public Map<String, String> mapRow(Object[] rs, int rowNum) {
			Map<String, String> actor = new HashMap<String, String>();
			int i = 0;
			actor.put("fid", rs[i++].toString());
			actor.put("id", rs[i++].toString());
			actor.put("type", rs[i++].toString());
			actor.put("code", rs[i++].toString());
			actor.put("name", rs[i++].toString());
			actor.put("pcode", rs[i] != null ? rs[i].toString() : null);
			i++;
			actor.put("pname", rs[i] != null ? rs[i].toString() : null);
			return actor;
		}
	}

	public List<Map<String, String>> findActorRoles(Long[] actorIds) {
		if (actorIds == null || actorIds.length == 0)
			return new ArrayList<Map<String, String>>();

		StringBuffer hql = new StringBuffer();
		hql.append("select distinct r.id as id,r.code as code,r.name as name,r.order_ as orderNo from BC_IDENTITY_ROLE as r");
		hql.append(" inner join BC_IDENTITY_ROLE_ACTOR as ra on ra.rid=r.id");
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
				hql.toString(), actorIds, new RowMapper<Map<String, String>>() {
					public Map<String, String> mapRow(Object[] rs, int rowNum) {
						Map<String, String> role = new HashMap<String, String>();
						int i = 0;
						role.put("id", rs[i++].toString());
						role.put("code", rs[i++].toString());
						return role;
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

	public List<Map<String, String>> findShortcuts(Long[] actorIds,
			Long[] resourceIds) {
		StringBuffer hql = new StringBuffer();
		hql.append("select s.aid,s.sid,s.id,s.standalone,s.name,s.url,s.iconclass,s.order_");
		hql.append(" from bc_desktop_shortcut s");
		hql.append(" where s.aid in (?");
		List<Object> args = new ArrayList<Object>();
		args.add(new Long(0));
		if (actorIds != null) {
			for (Long id : actorIds) {
				hql.append(",?");
				args.add(id);
			}
		}
		hql.append(") and s.sid in (?");
		args.add(new Long(0));
		if (resourceIds != null) {
			for (Long id : resourceIds) {
				hql.append(",?");
				args.add(id);
			}
		}
		hql.append(") order by s.order_");
		if (logger.isDebugEnabled()) {
			logger.debug("actorIds="
					+ StringUtils.arrayToCommaDelimitedString(actorIds));
			logger.debug("resourceIds="
					+ StringUtils.arrayToCommaDelimitedString(resourceIds));
			logger.debug("hql=" + hql);
		}
		return HibernateJpaNativeQuery.executeNativeSql(jpaTemplate,
				hql.toString(), args.toArray(),
				new RowMapper<Map<String, String>>() {
					public Map<String, String> mapRow(Object[] rs, int rowNum) {
						Map<String, String> s = new HashMap<String, String>();
						int i = 0;
						s.put("aid", rs[i] != null ? rs[i].toString() : null);
						i++;
						s.put("sid", rs[i] != null ? rs[i].toString() : null);
						i++;
						s.put("id", rs[i++].toString());
						s.put("standalone", rs[i++].toString());
						s.put("name", rs[i] != null ? rs[i].toString() : null);
						i++;
						s.put("url", rs[i] != null ? rs[i].toString() : null);
						i++;
						s.put("iconClass", rs[i] != null ? rs[i].toString()
								: null);
						i++;
						s.put("orderNo", rs[i] != null ? rs[i].toString()
								: null);
						return s;
					}
				});
	}

	public Set<Resource> findResources(Long[] roleIds) {
		if (roleIds == null || roleIds.length == 0)
			return new HashSet<Resource>();

		StringBuffer hql = new StringBuffer();
		hql.append("select distinct s.belong,s.id,s.type_,s.name,s.url,s.iconclass,s.order_,s.pname,s.option_");
		hql.append(" from bc_identity_resource s");
		hql.append(" inner join bc_identity_role_resource rs on rs.sid=s.id");
		hql.append(" where rs.rid");
		if (roleIds.length == 1) {
			hql.append(" = ?");
		} else {
			hql.append(" in (?");
			for (int i = 1; i < roleIds.length; i++) {
				hql.append(",?");
			}
			hql.append(")");
		}
		hql.append(" order by s.order_");
		if (logger.isDebugEnabled()) {
			logger.debug("roleIds="
					+ StringUtils.arrayToCommaDelimitedString(roleIds));
			logger.debug("hql=" + hql);
		}
		List<Long> sIds = HibernateJpaNativeQuery.executeNativeSql(jpaTemplate,
				hql.toString(), roleIds, new RowMapper<Long>() {
					public Long mapRow(Object[] rs, int rowNum) {
						// Map<String, String> s = new HashMap<String,
						// String>();
						// int i = 0;
						// s.put("pid", rs[i] != null ? rs[i].toString() :
						// null);
						// i++;
						// s.put("id", rs[i++].toString());
						// s.put("type", rs[i++].toString());
						// s.put("name", rs[i] != null ? rs[i].toString() :
						// null);
						// i++;
						// s.put("url", rs[i] != null ? rs[i].toString() :
						// null);
						// i++;
						// s.put("iconClass", rs[i] != null ? rs[i].toString()
						// : null);
						// i++;
						// s.put("orderNo", rs[i] != null ? rs[i].toString()
						// : null);
						// i++;
						// s.put("pname", rs[i] != null ? rs[i].toString() :
						// null);
						// i++;
						// s.put("option", rs[i] != null ? rs[i].toString() :
						// null);
						return new Long(rs[1].toString());
					}
				});

		Set<Resource> ss = new HashSet<Resource>();
		Map<Long, Resource> allResources = this.resourceService.findAll();
		for (Long sid : sIds) {
			if (allResources.containsKey(sid))
				ss.add(allResources.get(sid));
		}

		return ss;
	}
}
