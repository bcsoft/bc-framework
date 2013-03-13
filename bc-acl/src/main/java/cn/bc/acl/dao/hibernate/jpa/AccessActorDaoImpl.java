package cn.bc.acl.dao.hibernate.jpa;

import java.util.List;

import org.springframework.util.Assert;

import cn.bc.acl.dao.AccessActorDao;
import cn.bc.acl.domain.AccessActor;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.domain.Actor;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 访问者DAO接口的实现
 * 
 * @author lbj
 * 
 */
public class AccessActorDaoImpl extends HibernateCrudJpaDao<AccessActor> implements AccessActorDao {
	
	public AccessActor load(Long pid, Long aid) {
		Assert.notNull(pid);
		Assert.notNull(aid);
		AndCondition condition = new AndCondition();
		condition.add(new EqualsCondition("accessDoc.id", pid));
		condition.add(new EqualsCondition("actor.id", aid));
		return this.createQuery().condition(condition).singleResult();
	}

	public List<AccessActor> find(Long pid) {
		Assert.notNull(pid);
		AndCondition condition = new AndCondition();
		condition.add(new EqualsCondition("accessDoc.id", pid));
		condition.add(new OrderCondition("orderNo", Direction.Asc));
		return this.createQuery().condition(condition).list();
	}

	public void delete(AccessActor accessActor) {
		this.getJpaTemplate().remove(accessActor);
	}

	public void delete(List<AccessActor> accessActors) {
		if (accessActors != null)
			for (AccessActor aa : accessActors)
				this.getJpaTemplate().remove(aa);
	}

	public List<AccessActor> find(Actor actor) {
		Assert.notNull(actor);
		AndCondition condition = new AndCondition();
		condition.add(new EqualsCondition("actor", actor));
		return this.createQuery().condition(condition).list();
	}

	public List<AccessActor> find(Actor actor, String docType) {
		Assert.notNull(actor);
		Assert.notNull(docType);
		AndCondition condition = new AndCondition();
		condition.add(new EqualsCondition("actor", actor));
		condition.add(new EqualsCondition("accessDoc.docType", docType));
		return this.createQuery().condition(condition).list();
	}
}
