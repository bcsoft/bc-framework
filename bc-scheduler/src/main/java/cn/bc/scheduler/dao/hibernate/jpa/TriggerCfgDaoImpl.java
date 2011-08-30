package cn.bc.scheduler.dao.hibernate.jpa;

import java.util.List;

import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.scheduler.dao.TriggerCfgDao;
import cn.bc.scheduler.domain.TriggerCfg;

/**
 * 触发器配置DAO的Hibernate JPA实现
 * 
 * @author dragon
 * @since 2011-08-30
 */
public class TriggerCfgDaoImpl extends HibernateCrudJpaDao<TriggerCfg>
		implements TriggerCfgDao {
	@SuppressWarnings("unchecked")
	public List<TriggerCfg> findAllEnabled() {
		String hql = "from TriggerCfg t where t.status=1 order by t.orderNo";
		return (List<TriggerCfg>) this.getJpaTemplate().find(hql);
	}
}
