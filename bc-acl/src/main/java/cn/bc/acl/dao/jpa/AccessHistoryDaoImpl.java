package cn.bc.acl.dao.jpa;

import cn.bc.acl.dao.AccessHistoryDao;
import cn.bc.acl.domain.AccessHistory;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.orm.jpa.JpaCrudDao;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 访问历史DAO接口的实现
 *
 * @author lbj
 */
public class AccessHistoryDaoImpl extends JpaCrudDao<AccessHistory> implements AccessHistoryDao {
  public List<AccessHistory> findByDoc(Long pid) {
    Assert.notNull(pid);
    AndCondition condition = new AndCondition();
    condition.add(new EqualsCondition("pid", pid));
    return this.createQuery().condition(condition).list();
  }
}