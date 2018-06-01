package cn.bc.acl.dao.jpa;

import cn.bc.acl.dao.AccessActorDao;
import cn.bc.acl.domain.AccessActor;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.orm.jpa.JpaCrudDao;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 访问者DAO接口的实现
 *
 * @author lbj
 */
public class AccessActorDaoImpl extends JpaCrudDao<AccessActor> implements AccessActorDao {
  public AccessActor load(Long pid, Long aid) {
    Assert.notNull(pid);
    Assert.notNull(aid);
    AndCondition condition = new AndCondition();
    condition.add(new EqualsCondition("accessDoc.id", pid));
    condition.add(new EqualsCondition("actor.id", aid));
    return this.createQuery().condition(condition).singleResult();
  }

  public List<AccessActor> findByPid(Long id) {
    Assert.notNull(id);
    AndCondition condition = new AndCondition();
    condition.add(new EqualsCondition("accessDoc.id", id));
    condition.add(new OrderCondition("orderNo", Direction.Asc));
    return this.createQuery().condition(condition).list();
  }

  public void delete(AccessActor accessActor) {
    this.getEntityManager().remove(accessActor);
  }

  public void delete(List<AccessActor> accessActors) {
    if (accessActors != null)
      for (AccessActor aa : accessActors)
        this.getEntityManager().remove(aa);
  }

  public List<AccessActor> findByAid(Long id) {
    Assert.notNull(id);
    AndCondition condition = new AndCondition();
    condition.add(new EqualsCondition("actor.id", id));
    return this.createQuery().condition(condition).list();
  }

  public List<AccessActor> findByDocType(Long id, String docType) {
    Assert.notNull(id);
    Assert.notNull(docType);
    AndCondition condition = new AndCondition();
    condition.add(new EqualsCondition("actor.id", id));
    condition.add(new EqualsCondition("accessDoc.docType", docType));
    return this.createQuery().condition(condition).list();
  }

  public List<AccessActor> findByDocId(Long id, String docId) {
    Assert.notNull(id);
    Assert.notNull(docId);
    AndCondition condition = new AndCondition();
    condition.add(new EqualsCondition("actor.id", id));
    condition.add(new EqualsCondition("accessDoc.docId", docId));
    return this.createQuery().condition(condition).list();
  }

  public List<AccessActor> findByDocIdType(Long id, String docId, String docType) {
    Assert.notNull(id);
    Assert.notNull(docId);
    Assert.notNull(docType);
    AndCondition condition = new AndCondition();
    condition.add(new EqualsCondition("actor.id", id));
    condition.add(new EqualsCondition("accessDoc.docId", docId));
    condition.add(new EqualsCondition("accessDoc.docType", docType));
    return this.createQuery().condition(condition).list();
  }
}