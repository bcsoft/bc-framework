package cn.bc.identity.dao.hibernate.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.InCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.dao.ActorRelationDao;
import cn.bc.identity.domain.ActorRelation;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 参与者Service接口的实现
 * 
 * @author dragon
 * 
 */
public class ActorRelationDaoImpl extends HibernateCrudJpaDao<ActorRelation>
		implements ActorRelationDao {
	public List<ActorRelation> findByMaster(Integer type, Long masterId) {
		Assert.notNull(type);
		Assert.notNull(masterId);
		AndCondition condition = new AndCondition();
		condition.add(new EqualsCondition("type", type));
		condition.add(new EqualsCondition("master.id", masterId));
		condition.add(new OrderCondition("order", Direction.Asc));
		return this.createQuery().condition(condition).list();
	}

	public ActorRelation load(Integer type, Long masterId, Long followerId) {
		Assert.notNull(type);
		Assert.notNull(masterId);
		Assert.notNull(followerId);
		AndCondition condition = new AndCondition();
		condition.add(new EqualsCondition("type", type));
		condition.add(new EqualsCondition("master.id", masterId));
		condition.add(new EqualsCondition("follower.id", followerId));
		return this.createQuery().condition(condition).singleResult();
	}

	public List<ActorRelation> findByFollower(Integer type, Long followerId) {
		return findByFollower(type, followerId, null);
	}

	public List<ActorRelation> findByFollower(Integer type, Long followerId,
			Integer[] masterTypes) {
		Assert.notNull(type);
		Assert.notNull(followerId);
		AndCondition condition = new AndCondition();
		condition.add(new EqualsCondition("type", type));
		condition.add(new EqualsCondition("follower.id", followerId));

		if (masterTypes != null && masterTypes.length > 0) {
			if (masterTypes.length == 1) {
				condition
						.add(new EqualsCondition("master.type", masterTypes[0]));
			} else {
				condition.add(new InCondition("master.type", masterTypes));
			}
		}

		OrderCondition oc = new OrderCondition("master.order", Direction.Asc);
		oc.add("order", Direction.Asc);
		condition.add(oc);

		return this.createQuery().condition(condition).list();
	}

	@SuppressWarnings("unchecked")
	public List<ActorRelation> findByMasterOrFollower(Serializable[] mfIds) {
		if (mfIds == null || mfIds.length == 0)
			return new ArrayList<ActorRelation>();

		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("select f from ActorRelation ar");

		if (mfIds.length == 1) {
			hql.append(" where ar.master.id=? or ar.follower.id=?");
			args.add(mfIds[0]);
			args.add(mfIds[0]);
		} else {
			StringBuffer t = new StringBuffer();
			t.append("(?");
			for (int i = 1; i < mfIds.length; i++) {
				t.append(",?");
			}
			t.append(")");
			hql.append(" where ar.master.id in ");
			hql.append(t.toString());
			for (int i = 0; i < mfIds.length; i++) {
				args.add(mfIds[i]);
			}
			hql.append(" or ar.follower.id in ");
			for (int i = 0; i < mfIds.length; i++) {
				args.add(mfIds[i]);
			}
			hql.append(t.toString());
		}
		return (List<ActorRelation>) this.getJpaTemplate().find(hql.toString(),
				args.toArray());
	}

	public void deleteByMasterOrFollower(Serializable[] mfIds) {
		if (mfIds == null || mfIds.length == 0)
			return;

		ArrayList<Object> args = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append("delete ActorRelation ar");

		if (mfIds.length == 1) {
			hql.append(" where ar.master.id=? or ar.follower.id=?");
			args.add(mfIds[0]);
			args.add(mfIds[0]);
		} else {
			StringBuffer t = new StringBuffer();
			t.append("(?");
			for (int i = 1; i < mfIds.length; i++) {
				t.append(",?");
			}
			t.append(")");
			hql.append(" where ar.master.id in ");
			hql.append(t.toString());
			for (int i = 0; i < mfIds.length; i++) {
				args.add(mfIds[i]);
			}
			hql.append(" or ar.follower.id in ");
			for (int i = 0; i < mfIds.length; i++) {
				args.add(mfIds[i]);
			}
			hql.append(t.toString());
		}
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql.toString());
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args));
		}
		this.executeUpdate(hql.toString(), args);
	}

	public ActorRelation load4Belong(Long followerId, Integer[] masterTypes) {
		List<ActorRelation> ars = this.findByFollower(
				ActorRelation.TYPE_BELONG, followerId, masterTypes);
		if (ars != null && !ars.isEmpty()) {
			if (ars.size() > 1) {
				throw new CoreException("no unique for load4Belong!");
			} else {
				return ars.get(0);
			}
		} else {
			return null;
		}
	}

	public void delete(ActorRelation actorRelation) {
		this.getJpaTemplate().remove(actorRelation);
	}

	public void delete(List<ActorRelation> actorRelations) {
		if (actorRelations != null)
			for (ActorRelation ar : actorRelations)
				this.getJpaTemplate().remove(ar);
	}
}
