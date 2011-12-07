package cn.bc.sync.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.util.StringUtils;

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
	private static Log logger = LogFactory.getLog(SyncBaseDaoImpl.class);

	public boolean hadSync(String syncType, String syncCode) {
		Condition condition = new AndCondition().add(
				new EqualsCondition("syncType", syncType)).add(
				new EqualsCondition("syncCode", syncCode));
		return this.createQuery().condition(condition).count() > 0;
	}

	public SyncBase load(String syncType, String syncCode) {
		Condition condition = new AndCondition().add(
				new EqualsCondition("syncType", syncType)).add(
				new EqualsCondition("syncCode", syncCode));
		return this.createQuery().condition(condition).singleResult();
	}

	public boolean hadGenerate(String syncTo, final Long id) {
		final String hql = "select 1 from " + syncTo + " where sync_id = ?";
		return this.getJpaTemplate().execute(new JpaCallback<Boolean>() {
			public Boolean doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(hql);

				queryObject.setParameter(1, id);// jpa的索引号从1开始
				return !queryObject.getResultList().isEmpty();
			}
		});
	}

	public int updateStatus(Long[] ids, int toStatus) {
		if (ids == null || ids.length == 0)
			return 0;

		// 构建sql
		final StringBuffer hql = new StringBuffer(
				"update BC_SYNC_BASE set status_ = ? where");
		final List<Object> args = new ArrayList<Object>();
		args.add(new Integer(toStatus));
		if (ids.length == 1) {
			hql.append(" id = ?");
			args.add(ids[0]);
		} else {
			hql.append(" id in (?");
			args.add(ids[0]);
			for (int i = 1; i < ids.length; i++) {
				hql.append(",?");
				args.add(ids[i]);
			}
			hql.append(")");
		}
		hql.append(" and status_ != ?");
		args.add(new Integer(toStatus));

		if (logger.isDebugEnabled()) {
			logger.debug("sql=" + hql.toString());
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args));
		}

		// 执行更新
		return this.getJpaTemplate().execute(new JpaCallback<Integer>() {
			public Integer doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(hql.toString());

				// 注入参数
				int i = 0;
				for (Object value : args) {
					queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
					i++;
				}

				return queryObject.executeUpdate();
			}
		});
	}

	public int updateStatus2New(String syncType, List<String> syncCodes) {
		if (syncType == null || syncType.length() == 0)
			return 0;

		final StringBuffer sql = new StringBuffer(
				"update bc_sync_base set STATUS_ = ? where SYNC_TYPE = ? and STATUS_ != ?");
		final List<Object> args = new ArrayList<Object>();
		args.add(SyncBase.STATUS_NEW);
		args.add(syncType);
		args.add(SyncBase.STATUS_NEW);
		if (syncCodes != null && !syncCodes.isEmpty()) {
			if (syncCodes.size() == 1) {
				sql.append(" and SYNC_CODE = ?");
				args.add(syncCodes.get(0));
			} else {
				sql.append(" and SYNC_CODE in (?");
				args.add(syncCodes.get(0));
				for (int i = 1; i < syncCodes.size(); i++) {
					sql.append(",?");
					args.add(syncCodes.get(i));
				}
				sql.append(")");
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("sql=" + sql.toString());
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args));
		}
		return this.getJpaTemplate().execute(new JpaCallback<Integer>() {
			public Integer doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(sql.toString());

				// 注入参数
				int i = 0;
				for (Object value : args) {
					queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
					i++;
				}

				return queryObject.executeUpdate();
			}
		});
	}

	public int updateNewStatus2Done4ExcludeCode(String syncType,
			List<String> excludeSyncCodes) {
		if (syncType == null || syncType.length() == 0)
			return 0;

		final StringBuffer sql = new StringBuffer(
				"update bc_sync_base set STATUS_ = ? where SYNC_TYPE = ? and STATUS_ != ?");
		final List<Object> args = new ArrayList<Object>();
		args.add(SyncBase.STATUS_DONE);
		args.add(syncType);
		args.add(SyncBase.STATUS_DONE);
		if (excludeSyncCodes != null && !excludeSyncCodes.isEmpty()) {
			if (excludeSyncCodes.size() == 1) {
				sql.append(" and SYNC_CODE != ?");
				args.add(excludeSyncCodes.get(0));
			} else {
				sql.append(" and SYNC_CODE not in (?");
				args.add(excludeSyncCodes.get(0));
				for (int i = 1; i < excludeSyncCodes.size(); i++) {
					sql.append(",?");
					args.add(excludeSyncCodes.get(i));
				}
				sql.append(")");
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("sql=" + sql.toString());
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args));
		}
		return this.getJpaTemplate().execute(new JpaCallback<Integer>() {
			public Integer doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em.createNativeQuery(sql.toString());

				// 注入参数
				int i = 0;
				for (Object value : args) {
					queryObject.setParameter(i + 1, value);// jpa的索引号从1开始
					i++;
				}

				return queryObject.executeUpdate();
			}
		});
	}
}
