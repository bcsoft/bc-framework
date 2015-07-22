package cn.bc.spider.dao.jpa;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.orm.jpa.JpaCrudDao;
import cn.bc.spider.dao.SpiderConfigDao;
import cn.bc.spider.domain.SpiderConfig;

/**
 * 抓取配置Dao接口的实现
 *
 * @author dragon
 */
public class SpiderConfigDaoImpl extends JpaCrudDao<SpiderConfig> implements SpiderConfigDao {
	public SpiderConfig loadByCode(String code) {
		if (code == null)
			return null;
		AndCondition c = new AndCondition();
		c.add(new EqualsCondition("code", code));
		c.add(new EqualsCondition("status", BCConstants.STATUS_ENABLED));
		return this.createQuery().condition(c).singleResult();
	}
}