package cn.bc.subscribe.dao.jpa;

import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.identity.domain.Actor;
import cn.bc.orm.jpa.JpaCrudDao;
import cn.bc.subscribe.dao.SubscribeActorDao;
import cn.bc.subscribe.domain.Subscribe;
import cn.bc.subscribe.domain.SubscribeActor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 访问者DAO接口的实现
 *
 * @author lbj
 */
public class SubscribeActorDaoImpl extends JpaCrudDao<SubscribeActor> implements SubscribeActorDao {
  private static Log logger = LogFactory.getLog(SubscribeActorDaoImpl.class);

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List<SubscribeActor> findList(Subscribe subscribe) {
    EqualsCondition eq = new EqualsCondition("subscribe", subscribe);
    return this.createQuery().condition(eq).list();
  }

  public List<Actor> findList2Actor(Subscribe subscribe) {
    List<SubscribeActor> sas = this.findList(subscribe);
    List<Actor> actors = new ArrayList<Actor>();
    for (SubscribeActor sa : sas) {
      actors.add(sa.getActor());
    }
    return actors;
  }

  public void delete(Long aid, Long pid) {
    org.springframework.util.Assert.notNull(aid);
    Assert.notNull(pid);

    String sql = "delete from bc_subscribe_actor where"
      + " aid=" + aid + " and pid=" + pid;

    if (logger.isDebugEnabled()) {
      logger.debug("sql := " + sql);
    }

    this.jdbcTemplate.execute(sql);
  }

  public SubscribeActor find4aidpid(Long aid, Long pid) {
    Assert.notNull(aid);
    Assert.notNull(pid);
    AndCondition ac = new AndCondition();
    ac.add(new EqualsCondition("actor.id", aid));
    ac.add(new EqualsCondition("subscribe.id", pid));
    return this.createQuery().condition(ac).singleResult();
  }

  public void delete(SubscribeActor subscribeActor) {
    this.getEntityManager().remove(subscribeActor);
  }

  public void delete(List<SubscribeActor> subscribeActors) {
    if (subscribeActors != null)
      for (SubscribeActor aa : subscribeActors)
        this.getEntityManager().remove(aa);
  }

  public void save(Long aid, Long pid, int type) {
    Assert.notNull(aid);
    Assert.notNull(pid);

    String sql = "insert into bc_subscribe_actor (aid,pid,type_,file_date) values("
      + aid + "," + pid + "," + type + ",now())";

    if (logger.isDebugEnabled()) {
      logger.debug("sql := " + sql);
    }

    this.jdbcTemplate.execute(sql);
  }
}