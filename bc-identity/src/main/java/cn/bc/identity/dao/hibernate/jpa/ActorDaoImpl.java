package cn.bc.identity.dao.hibernate.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.dao.ActorDao;
import cn.bc.identity.dao.ActorRelationDao;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.domain.ActorRelation;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.security.domain.Module;

/**
 * 参与者Service接口的实现
 * 
 * @author dragon
 * 
 */
public class ActorDaoImpl extends HibernateCrudJpaDao<Actor> implements
		ActorDao {
	private ActorRelationDao actorRelationDao;

	@Autowired
	public void setActorRelationDao(ActorRelationDao actorRelationDao) {
		this.actorRelationDao = actorRelationDao;
	}

	public Actor loadByCode(String actorCode) {
		String hql = "from Actor a where a.code=?";
		@SuppressWarnings("rawtypes")
		List all = this.getJpaTemplate().find(hql, actorCode);
		if (all == null || all.isEmpty())
			return null;
		else if (all.size() == 1)
			return (Actor) all.get(0);
		else
			throw new CoreException("return more than one result! actorCode="
					+ actorCode);

		// return this.createQuery().condition(new EqualsCondition("code",
		// actorCode)).singleResult();
	}

	public Actor loadBelong(Long followerId, Integer[] masterTypes) {
		List<Actor> ms = this.findMaster(followerId,
				new Integer[] { ActorRelation.TYPE_BELONG }, masterTypes);
		if (ms != null && !ms.isEmpty()) {
			if (ms.size() > 1) {
				throw new CoreException("no unique for loadBelong!");
			} else {
				return ms.get(0);
			}
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Actor> findMaster(Long followerId, Integer[] relationTypes,
			Integer[] masterTypes) {
		if (followerId == null)
			return new ArrayList<Actor>();// TODO: 如顶层单位、底层叶子

		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select m from Actor m,ActorRelation ar,Actor f");
		hql.append(" where m.id=ar.master.id");
		hql.append(" and f.id=ar.follower.id and f.id=?");
		args.add(followerId);

		// 关联的类型，对应ActorRelation的type属性
		if (relationTypes != null && relationTypes.length > 0) {
			if (relationTypes.length == 1) {
				hql.append(" and ar.type=?");
				args.add(relationTypes[0]);
			} else {
				hql.append(" and ar.type in (?");
				args.add(relationTypes[0]);
				for (int i = 1; i < relationTypes.length; i++) {
					hql.append(",?");
					args.add(relationTypes[i]);
				}
				hql.append(")");
			}
		}

		// 主控方的类型，对应Actor的type属性
		if (masterTypes != null && masterTypes.length > 0) {
			if (masterTypes.length == 1) {
				hql.append(" and m.type=?");
				args.add(masterTypes[0]);
			} else {
				hql.append(" and m.type in (?");
				args.add(masterTypes[0]);
				for (int i = 1; i < masterTypes.length; i++) {
					hql.append(",?");
					args.add(masterTypes[i]);
				}
				hql.append(")");
			}
		}

		// 排序
		hql.append(" order by ar.type,m.type,m.code");
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql.toString());
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args));
		}
		return this.getJpaTemplate().find(hql.toString(), args.toArray());
	}

	@SuppressWarnings("unchecked")
	//
	public List<Actor> findFollower(Long masterId, Integer[] relationTypes,
			Integer[] followerTypes) {
		if (masterId == null)
			return new ArrayList<Actor>();

		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select f from Actor f,ActorRelation ar,Actor m");
		hql.append(" where f.id=ar.follower.id");
		hql.append(" and m.id=ar.master.id and m.id=?");
		args.add(masterId);

		// 关联的类型，对应ActorRelation的type属性
		if (relationTypes != null && relationTypes.length > 0) {
			if (relationTypes.length == 1) {
				hql.append(" and ar.type=?");
				args.add(relationTypes[0]);
			} else {
				hql.append(" and ar.type in (?");
				args.add(relationTypes[0]);
				for (int i = 1; i < relationTypes.length; i++) {
					hql.append(",?");
					args.add(relationTypes[i]);
				}
				hql.append(")");
			}
		}

		// 从属方的类型，对应Actor的type属性
		if (followerTypes != null && followerTypes.length > 0) {
			if (followerTypes.length == 1) {
				hql.append(" and f.type=?");
				args.add(followerTypes[0]);
			} else {
				hql.append(" and f.type in (?");
				args.add(followerTypes[0]);
				for (int i = 1; i < followerTypes.length; i++) {
					hql.append(",?");
					args.add(followerTypes[i]);
				}
				hql.append(")");
			}
		}

		// 排序
		hql.append(" order by ar.type,ar.order,f.type,f.code");
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql.toString());
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args));
		}
		return this.getJpaTemplate().find(hql.toString(), args.toArray());
	}

	public List<Actor> findLowerOrganization(Long higherOrganizationId,
			Integer... lowerOrganizationTypes) {
		// 默认为单位+部门+岗位
		if (lowerOrganizationTypes == null)
			lowerOrganizationTypes = new Integer[] { Actor.TYPE_UNIT,
					Actor.TYPE_DEPARTMENT, Actor.TYPE_GROUP };
		return this.findFollower(higherOrganizationId,
				new Integer[] { ActorRelation.TYPE_BELONG },
				lowerOrganizationTypes);
	}

	public List<Actor> findHigherOrganization(Long lowerOrganizationId,
			Integer... higherOrganizationTypes) {
		// 默认为单位+部门+岗位
		if (higherOrganizationTypes == null)
			higherOrganizationTypes = new Integer[] { Actor.TYPE_UNIT,
					Actor.TYPE_DEPARTMENT, Actor.TYPE_GROUP };
		return this.findMaster(lowerOrganizationId,
				new Integer[] { ActorRelation.TYPE_BELONG },
				higherOrganizationTypes);
	}

	@SuppressWarnings("unchecked")
	public List<Actor> findTopUnit() {
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select m from Actor m where m.type=? and m.id not in (");
		args.add(Actor.TYPE_UNIT);
		hql.append("select f.id from Actor f,ActorRelation ar");
		hql.append(" where f.id=ar.follower.id");
		hql.append(" and f.type=? and ar.type=?");
		args.add(Actor.TYPE_UNIT);
		args.add(ActorRelation.TYPE_BELONG);
		hql.append(")");

		// 排序
		hql.append(" order by m.code");
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql.toString());
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args));
		}
		return this.getJpaTemplate().find(hql.toString(), args.toArray());
	}

	public List<Actor> findAllUnit(Integer... statues) {
		AndCondition c = new AndCondition();
		c.add(new EqualsCondition("type", new Integer(Actor.TYPE_UNIT))).add(
				new OrderCondition("order", Direction.Asc).add("code",
						Direction.Asc));
		if (statues != null && statues.length > 0) {
			if (statues.length == 1) {
				c.add(new EqualsCondition("status", statues[0]));
			} else {
				c.add(new InCondition("status", statues));
			}
		}
		return this.createQuery().condition(c).list();
	}

	public List<Actor> findUser(Long organizationId) {
		return this.findFollower(organizationId,
				new Integer[] { ActorRelation.TYPE_BELONG },
				new Integer[] { Actor.TYPE_USER });
	}

	public List<Actor> findAncestorOrganization(Long lowerOrganizationId,
			Integer... ancestorOrganizationTypes) {
		// 默认为单位+部门+岗位
		if (ancestorOrganizationTypes == null || ancestorOrganizationTypes.length == 0)
			ancestorOrganizationTypes = new Integer[] { Actor.TYPE_UNIT,
					Actor.TYPE_DEPARTMENT, Actor.TYPE_GROUP };

		// TODO 性能优化，以下只是使用了递归查找
		Set<Actor> ancestors = new HashSet<Actor>();//使用Set避免重复
		this.recursiveFindHigherOrganization(ancestors, lowerOrganizationId,
				ancestorOrganizationTypes);
		return new ArrayList<Actor>(ancestors);
	}

	// 递归查找祖先组织
	private void recursiveFindHigherOrganization(Set<Actor> ancestors,
			Long lowerId, Integer... ancestorOrganizationTypes) {
		List<Actor> highers = this.findHigherOrganization(lowerId,
				ancestorOrganizationTypes);
		if (highers != null && !highers.isEmpty()) {
			for (Actor higher : highers) {
				this.recursiveFindHigherOrganization(ancestors, higher.getId(),
						ancestorOrganizationTypes);
				ancestors.add(higher);
			}
		}
	}

	public List<Actor> findDescendantOrganization(Long higherOrganizationId,
			Integer... descendantOrganizationTypes) {
		// 默认为单位+部门+岗位
		if (descendantOrganizationTypes == null)
			descendantOrganizationTypes = new Integer[] { Actor.TYPE_UNIT,
					Actor.TYPE_DEPARTMENT, Actor.TYPE_GROUP };

		// TODO 性能优化，以下只是使用了递归查找
		List<Actor> descendants = new ArrayList<Actor>();
		this.recursiveFindDescendantOrganization(descendants,
				higherOrganizationId, descendantOrganizationTypes);
		return descendants;
	}

	// 递归查找后代组织
	private void recursiveFindDescendantOrganization(List<Actor> descendants,
			Long higherOrganizationId, Integer[] descendantOrganizationTypes) {
		List<Actor> lowers = this.findLowerOrganization(higherOrganizationId,
				descendantOrganizationTypes);
		if (lowers != null && !lowers.isEmpty()) {
			for (Actor lower : lowers) {
				descendants.add(lower);
				this.recursiveFindDescendantOrganization(descendants,
						lower.getId(), descendantOrganizationTypes);
			}
		}
	}

	public List<Actor> findDescendantUser(Long organizationId,
			Integer... descendantOrganizationTypes) {
		// 查找直接隶属的人员信息
		List<Actor> users = new ArrayList<Actor>();
		List<Actor> _users = this.findUser(organizationId);
		if (_users != null && !_users.isEmpty()) {
			users.addAll(_users);
		}

		// 获取所有后代组织
		List<Actor> descendantOrganizations = this.findDescendantOrganization(
				organizationId, descendantOrganizationTypes);

		// 循环每个组织查找人员信息
		if (descendantOrganizations != null
				&& !descendantOrganizations.isEmpty()) {
			for (Actor org : descendantOrganizations) {
				_users = this.findUser(org.getId());
				if (_users != null && !_users.isEmpty()) {
					users.addAll(_users);
				}
			}
		}
		return users;
	}

	public List<Module> findCanUseModules(Long actorId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Serializable pk) {
		// 先删除关联关系
		if (pk != null)
			this.actorRelationDao
					.deleteByMasterOrFollower(new Serializable[] { pk });
		// 再删除自身
		super.delete(pk);
	}

	@Override
	public void delete(Serializable[] pks) {
		// 先删除关联关系
		this.actorRelationDao.deleteByMasterOrFollower(pks);
		// 再删除自身
		super.delete(pks);
	}

	public Actor save4belong(Actor follower, Actor belong) {
		follower = this.save(follower);

		// 处理与上级的隶属
		ActorRelation curAr;
		curAr = this.actorRelationDao.load4Belong(follower.getId(),
				new Integer[] { Actor.TYPE_UNIT, Actor.TYPE_DEPARTMENT });
		if (belong != null && !belong.isNew()) {
			belong = this.load(belong.getId());// 重新加载一下belong
			if (curAr != null
					&& !curAr.getMaster().getId().equals(belong.getId())) {
				// 删除原来的隶属关系（因为使用的是联合主键）
				this.actorRelationDao.delete(curAr);
			}
			if (curAr == null
					|| !curAr.getMaster().getId().equals(belong.getId())) {
				// 创建新的隶属关系
				curAr = new ActorRelation();
				curAr.setFollower(follower);
				curAr.setMaster(belong);
				curAr.setType(ActorRelation.TYPE_BELONG);
				this.actorRelationDao.save(curAr);
			}
		} else {
			// 删除存在的隶属关系
			if (curAr != null)
				this.actorRelationDao.delete(curAr);
		}

		return follower;
	}

	@SuppressWarnings("unchecked")
	public List<Actor> find(Integer[] actorTypes, Integer[] actorStatues) {
		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("from Actor a");

		boolean isWhere = true;
		// 类型
		if (actorTypes != null && actorTypes.length > 0) {
			isWhere = false;
			if (actorTypes.length == 1) {
				hql.append(" where a.type=?");
				args.add(actorTypes[0]);
			} else {
				hql.append(" where a.type in (?");
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
				hql.append(" " + (isWhere ? "where" : "and") + " a.status=?");
				args.add(actorStatues[0]);
			} else {
				hql.append(" " + (isWhere ? "where" : "and")
						+ " a.status in (?");
				args.add(actorStatues[0]);
				for (int i = 1; i < actorStatues.length; i++) {
					hql.append(",?");
					args.add(actorStatues[i]);
				}
				hql.append(")");
			}
		}

		// 排序
		hql.append(" order by a.order");
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql.toString());
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args));
		}
		return this.getJpaTemplate().find(hql.toString(), args.toArray());
	}
}
