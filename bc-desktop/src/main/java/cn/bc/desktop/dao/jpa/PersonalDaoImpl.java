package cn.bc.desktop.dao.jpa;

import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.desktop.dao.PersonalDao;
import cn.bc.desktop.domain.Personal;
import cn.bc.orm.jpa.JpaCrudDao;

/**
 * 个人设置Dao接口的实现
 *
 * @author dragon
 */
public class PersonalDaoImpl extends JpaCrudDao<Personal> implements PersonalDao {
	public Personal loadByActor(Long actorId, boolean autoUseGlobal) {
		Personal pc = this.createQuery()
				.condition(new EqualsCondition("actorId", actorId))
				.singleResult();// 个人配置
		if (pc == null && autoUseGlobal)
			pc = loadGlobalConfig();// 全局配置
		return pc;
	}

	public Personal loadGlobalConfig() {
		return this.createQuery()
				.condition(new EqualsCondition("actorId", new Long(0)))
				.singleResult();
	}
}
