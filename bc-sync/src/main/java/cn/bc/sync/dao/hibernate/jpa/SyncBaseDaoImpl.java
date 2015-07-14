package cn.bc.sync.dao.hibernate.jpa;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.orm.jpa.JpaCrudDao;
import cn.bc.sync.dao.SyncBaseDao;
import cn.bc.sync.domain.SyncBase;

import java.util.ArrayList;
import java.util.List;

/**
 * 同步Dao的Hibernate JPA实现
 *
 * @author rongjih
 */
public class SyncBaseDaoImpl extends JpaCrudDao<SyncBase> implements SyncBaseDao {
	public boolean hadSync(String syncType, String syncCode) {
		Condition condition = new AndCondition()
				.add(new EqualsCondition("syncType", syncType))
				.add(new EqualsCondition("syncCode", syncCode));
		return this.createQuery().condition(condition).count() > 0;
	}

	public SyncBase load(String syncType, String syncCode) {
		Condition condition = new AndCondition()
				.add(new EqualsCondition("syncType", syncType))
				.add(new EqualsCondition("syncCode", syncCode));
		return this.createQuery().condition(condition).singleResult();
	}

	public boolean hadGenerate(String syncTo, final Long id) {
		final String hql = "select 1 from " + syncTo + " where sync_id = ?";
		return !executeNativeQuery(hql, new Object[]{id}).isEmpty();
	}

	public int updateStatus(Long[] ids, int toStatus) {
		if (ids == null || ids.length == 0)
			return 0;

		// 构建sql
		final StringBuffer sql = new StringBuffer("update BC_SYNC_BASE set status_ = ? where");
		final List<Object> args = new ArrayList<>();
		args.add(toStatus);
		if (ids.length == 1) {
			sql.append(" id = ?");
			args.add(ids[0]);
		} else {
			sql.append(" id in (?");
			args.add(ids[0]);
			for (int i = 1; i < ids.length; i++) {
				sql.append(",?");
				args.add(ids[i]);
			}
			sql.append(")");
		}
		sql.append(" and status_ != ?");
		args.add(toStatus);

		// 执行更新
		return executeNativeUpdate(sql.toString(), args);
	}

	public int updateStatus2New(String syncType, List<String> syncCodes) {
		if (syncType == null || syncType.length() == 0)
			return 0;

		final StringBuffer sql = new StringBuffer(
				"update bc_sync_base set STATUS_ = ? where SYNC_TYPE = ? and STATUS_ != ?");
		final List<Object> args = new ArrayList<>();
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
		return executeNativeUpdate(sql.toString(), args);
	}

	public int updateNewStatus2Done4ExcludeCode(String syncType, List<String> excludeSyncCodes) {
		if (syncType == null || syncType.length() == 0)
			return 0;

		final StringBuffer sql = new StringBuffer(
				"update bc_sync_base set STATUS_ = ? where SYNC_TYPE = ? and STATUS_ != ?");
		final List<Object> args = new ArrayList<>();
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
		return executeNativeUpdate(sql.toString(), args);
	}
}