package cn.bc.acl.dao.hibernate.jpa;

import java.util.List;

import org.springframework.util.Assert;

import cn.bc.acl.dao.AccessHistoryDao;
import cn.bc.acl.domain.AccessHistory;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 访问历史DAO接口的实现
 * 
 * @author lbj
 * 
 */
public class AccessHistoryDaoImpl extends HibernateCrudJpaDao<AccessHistory> implements AccessHistoryDao {

	public List<AccessHistory> findByDoc(Long pid) {
		Assert.notNull(pid);
		AndCondition condition = new AndCondition();
		condition.add(new EqualsCondition("accessDoc.id", pid));
		return this.createQuery().condition(condition).list();
	}

	public void delete(List<AccessHistory> accessHistorys) {
		if (accessHistorys != null)
			for (AccessHistory ah : accessHistorys)
				this.getJpaTemplate().remove(ah);
	}

}
