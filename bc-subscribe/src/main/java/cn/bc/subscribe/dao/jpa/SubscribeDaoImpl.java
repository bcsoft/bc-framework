package cn.bc.subscribe.dao.jpa;

import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.NotEqualsCondition;
import cn.bc.orm.jpa.JpaCrudDao;
import cn.bc.subscribe.dao.SubscribeDao;
import cn.bc.subscribe.domain.Subscribe;
import org.springframework.util.Assert;

/**
 * 订阅DAO接口的实现
 *
 * @author lbj
 */
public class SubscribeDaoImpl extends JpaCrudDao<Subscribe> implements SubscribeDao {
	public Subscribe loadByEventCode(String eventCode) {
		Assert.notNull(eventCode);
		EqualsCondition eq = new EqualsCondition("eventCode", eventCode);
		return this.createQuery().condition(eq).singleResult();
	}

	public boolean isUnique(String eventCode, Long id) {
		Assert.notNull(eventCode);
		AndCondition ac = new AndCondition();
		ac.add(new EqualsCondition("eventCode", eventCode));

		if (id != null) {
			ac.add(new NotEqualsCondition("id", id));
		}

		return !(this.createQuery().condition(ac).list().size() > 0);
	}
}