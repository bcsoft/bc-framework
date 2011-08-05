package cn.bc.identity.dao.hibernate.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.springframework.orm.jpa.JpaCallback;

import cn.bc.identity.dao.ActorHistoryDao;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * Actor隶属信息的变动历史Service接口的实现
 * 
 * @author dragon
 * 
 */
public class ActorHistoryDaoImpl extends HibernateCrudJpaDao<ActorHistory>
		implements ActorHistoryDao {

	public ActorHistory loadCurrent(final Long actorId) {
		final String hql = "from ActorHistory a where a.actorId=? order by a.createDate desc";
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("actorId=" + actorId);
		}
		List<ActorHistory> list = this.getJpaTemplate().execute(
				new JpaCallback<List<ActorHistory>>() {
					@SuppressWarnings("unchecked")
					public List<ActorHistory> doInJpa(EntityManager em)
							throws PersistenceException {
						Query queryObject = em.createQuery(hql);
						getJpaTemplate().prepareQuery(queryObject);
						queryObject.setParameter(1, actorId);// jpa的索引号从1开始
						queryObject.setFirstResult(0);
						queryObject.setMaxResults(1);
						return (List<ActorHistory>) queryObject.getResultList();
					}
				});

		if (list != null && !list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}
}
