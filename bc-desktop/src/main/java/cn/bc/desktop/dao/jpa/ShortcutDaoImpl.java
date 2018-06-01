package cn.bc.desktop.dao.jpa;

import cn.bc.desktop.dao.ShortcutDao;
import cn.bc.desktop.domain.Shortcut;
import cn.bc.identity.dao.ActorDao;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.Resource;
import cn.bc.identity.domain.Role;
import cn.bc.orm.jpa.JpaCrudDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * ShortcutDao接口的实现
 *
 * @author dragon
 */
public class ShortcutDaoImpl extends JpaCrudDao<Shortcut> implements ShortcutDao {
  private static Log logger = LogFactory.getLog(ShortcutDaoImpl.class);
  private ActorDao actorDao;

  @Autowired
  public void setActorDao(ActorDao actorDao) {
    this.actorDao = actorDao;
  }

  public List<Shortcut> findByActor(Long actorId, boolean includeAncestor, boolean includeCommon) {
    return this.findByActor(actorId, includeAncestor, includeCommon, null, null, null);
  }

  @SuppressWarnings("unchecked")
  public List<Shortcut> findByActor(Long actorId, boolean includeAncestor, boolean includeCommon
    , Set<Actor> ancestorOrganizations, Set<Role> roles, Set<Resource> resources) {
    ArrayList<Object> args = new ArrayList<>();
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
        if (resources == null)
          resources = new LinkedHashSet<>();
        if (roles == null)
          roles = new LinkedHashSet<>();

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
          if (r.getResources() != null)
            resources.addAll(r.getResources());
        }

        // --模块的id列表
        for (Resource m : resources) {
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
          hql.append(" left join s.resource sm");

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

        // resourceIds--有权限访问的资源对应的快捷方式
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
      if (includeCommon) {
        // 不要使用sa is null：a.id is null
        // 全系统通用的快捷方式
        hql.append(" or (s.actor is null and s.resource is null)");
      }
      hql.append(" order by s.order");
    }
    if (logger.isDebugEnabled()) {
      logger.debug("hql=" + hql.toString());
      logger.debug("args="
        + StringUtils.collectionToCommaDelimitedString(args));
    }
    return executeQuery(hql.toString(), args);
  }
}
