package cn.bc.desktop.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import cn.bc.desktop.dao.ShortcutDao;
import cn.bc.desktop.domain.Shortcut;
import cn.bc.identity.dao.ActorDao;
import cn.bc.identity.domain.Actor;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.security.domain.Module;
import cn.bc.security.domain.Role;

/**
 * ShortcutDao接口的实现
 * 
 * @author dragon
 * 
 */
public class ShortcutDaoImpl extends HibernateCrudJpaDao<Shortcut> implements
		ShortcutDao {
	private ActorDao actorDao;

	@Autowired
	public void setActorDao(ActorDao actorDao) {
		this.actorDao = actorDao;
	}

	public List<Shortcut> findByActor(Long actorId, boolean includeAncestor,
			boolean includeCommon) {
		return this.findByActor(actorId, includeAncestor, includeCommon, null,
				null, null);
	}

	@SuppressWarnings("unchecked")
	public List<Shortcut> findByActor(Long actorId, boolean includeAncestor,
			boolean includeCommon, Set<Actor> ancestorOrganizations,
			Set<Role> roles, Set<Module> modules) {
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select s from Shortcut s");

		if (actorId == null) {//获取通用的快捷方式
			hql.append(" where s.actor is null");
			hql.append(" order by s.order");
		} else {
			Actor actor = this.actorDao.load(actorId);
			if (includeAncestor) {//包含父组织的处理
				List<Long> mids = new ArrayList<Long>();
				List<Long> aids = new ArrayList<Long>();

				// 获取actor隶属的所有上级组织，包括上级的上级，单位+部门+岗位
				List<Actor> parents = this.actorDao
						.findAncestorOrganization(actorId);
				if (ancestorOrganizations != null && parents != null)
					ancestorOrganizations.addAll(parents);

				// 汇总所有可以访问的角色、模块列表
				if (modules == null)
					modules = new LinkedHashSet<Module>();
				if (roles == null)
					roles = new LinkedHashSet<Role>();
				
				// --隶属的父组织拥有的角色
				for (Actor a : parents) {
					if (a.getRoles() != null)
						roles.addAll(a.getRoles());
				}
				
				// --自己拥有的角色
				if (actor.getRoles() != null)
					roles.addAll(actor.getRoles());
				
				// --角色中包含的模块
				for (Role r : roles) {
					if (r.getModules() != null)
						modules.addAll(r.getModules());
				}
				
				// --模块的id列表
				for (Module m : modules) {
					mids.add(m.getId());
				}
				
				// --父组织及自己的id列表
				for (Actor a : parents) {
					aids.add(a.getId());
				}
				aids.add(actorId);

				//hql
				hql.append(" left join s.actor sa");
				if (mids != null && !mids.isEmpty())
					hql.append(" left join s.module sm");

				// actorIds -- 自己或上级组织定义的快捷方式
				if (aids.size() == 1) {
					hql.append(" where sa.id=?");
					args.add(aids.get(0));
				} else {
					hql.append(" where sa.id in (?");
					args.add(aids.get(0));
					for (int i = 1; i < aids.size(); i++) {
						hql.append(",?");
						args.add(aids.get(i));
					}
					hql.append(")");
				}

				// moduleIds--有权限访问的资源对应的快捷方式
				if (mids != null && !mids.isEmpty()) {
					if (mids.size() == 1) {
						hql.append(" or sm.id=?");
						args.add(mids.get(0));
					} else {
						hql.append(" or sm.id in (?");
						args.add(mids.get(0));
						for (int i = 1; i < mids.size(); i++) {
							hql.append(",?");
							args.add(mids.get(i));
						}
						hql.append(")");
					}
				}
			} else {//不包含父组织的处理
				hql.append(" left join s.actor sa");
				hql.append(" where sa.id=?");
				args.add(actorId);
			}
			if (includeCommon){
				// 不要使用sa is null：a.id is null
				// 全系统通用的快捷方式
				 hql.append(" or (s.actor is null and s.module is null)");
			}
			hql.append(" order by s.order");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql.toString());
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args));
		}
		return this.getJpaTemplate().find(hql.toString(), args.toArray());
	}
}
