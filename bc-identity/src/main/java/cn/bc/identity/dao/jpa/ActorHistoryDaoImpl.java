package cn.bc.identity.dao.jpa;

import cn.bc.core.exception.CoreException;
import cn.bc.identity.dao.ActorHistoryDao;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.orm.jpa.JpaCrudDao;
import cn.bc.orm.jpa.JpaUtils;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import cn.bc.identity.dto.DepartmentByActorDto4MiniInfo;


/**
 * Actor隶属信息的变动历史Service接口的实现
 *
 * @author dragon
 */
public class ActorHistoryDaoImpl extends JpaCrudDao<ActorHistory> implements ActorHistoryDao {
	public ActorHistory loadCurrent(final Long actorId) {
		final String hql = "from ActorHistory a where a.actorId=? order by a.createDate desc";
		Query query = JpaUtils.createQuery(getEntityManager(), hql, new Object[]{actorId});
		query.setFirstResult(0);
		query.setMaxResults(1);
		return JpaUtils.getSingleResult(query);
	}

	public ActorHistory loadByCode(String actorCode) {
		String hql = "from ActorHistory h where h.code=? and h.current=?";
		List<ActorHistory> all = executeQuery(hql, new Object[]{actorCode, true});
		if (all == null || all.isEmpty())
			return null;
		else if (all.size() == 1)
			return all.get(0);
		else
			throw new CoreException("return more than one result! actorCode=" + actorCode);
	}

	@SuppressWarnings("unchecked")
	public List<String> findNames(List<String> actorCodes) {
		if (actorCodes == null || actorCodes.isEmpty())
			return new ArrayList<>();
		final List<Object> args = new ArrayList<>();
		final StringBuffer hql = new StringBuffer(
				"select a.name from Actor a where");
		if (actorCodes.size() == 1) {
			hql.append(" a.code=?");
			args.add(actorCodes.get(0));
		} else {
			hql.append(" a.code in (?,");
			args.add(actorCodes.get(0));
			for (int i = 1; i < actorCodes.size(); i++) {
				hql.append(",?");
				args.add(actorCodes.get(i));
			}
			hql.append(")");
		}
		return executeQuery(hql.toString(), args);
	}

	@Override
	public List<DepartmentByActorDto4MiniInfo> findDepartmentMiniInfoByActors(Long[] actorHistoryIds) {
		String ql =
			"select new cn.bc.identity.dto.DepartmentByActorDto4MiniInfo(ah.id, ah.upperId, a.code, ah.upperName) \n" +
				"from ActorHistory ah, Actor a \n" +
				"where a.id = ah.upperId  and ah.current = true and ah.id in (:actorHistoryIds)";
		return getEntityManager().createQuery(ql, DepartmentByActorDto4MiniInfo.class)
			.setParameter("actorHistoryIds", Arrays.asList(actorHistoryIds)).getResultList();
	}
}