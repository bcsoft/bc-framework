package cn.bc.sync.dao.hibernate.jpa;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.sync.dao.SyncBaseDao;
import cn.bc.sync.domain.SyncBase;

/**
 * 同步Dao的Hibernate JPA实现
 * 
 * @author rongjih
 * 
 */
public class SyncBaseDaoImpl extends HibernateCrudJpaDao<SyncBase> implements
		SyncBaseDao {

	public boolean hadSync(String syncType, String syncId) {
		Condition condition = new AndCondition().add(
				new EqualsCondition("syncType", syncType)).add(
				new EqualsCondition("syncId", syncId));
		return this.createQuery().condition(condition).count() > 0;
	}

	public SyncBase load(String syncType, String syncId) {
		Condition condition = new AndCondition().add(
				new EqualsCondition("syncType", syncType)).add(
				new EqualsCondition("syncId", syncId));
		return this.createQuery().condition(condition).singleResult();
	}
}
