package cn.bc.identity.dao.jpa;

import cn.bc.identity.dao.AuthDataDao;
import cn.bc.identity.domain.AuthData;
import cn.bc.orm.jpa.JpaUtils;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * 认证信息Dao接口的实现
 *
 * @author dragon
 */
@Component
public class AuthDataDaoImpl implements AuthDataDao {
	@PersistenceContext
	private EntityManager entityManager;

	public AuthData load(Long id) {
		return entityManager.find(AuthData.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<AuthData> find(Long[] ids) {
		if (ids == null || ids.length == 0)
			return new ArrayList<>();

		ArrayList<Object> args = new ArrayList<>();
		StringBuffer hql = new StringBuffer();
		hql.append("from AuthData a");

		if (ids.length == 1) {
			hql.append(" where a.id=?");
			args.add(ids[0]);
		} else {
			int i = 0;
			hql.append(" where a.id in (");
			for (Long id : ids) {
				hql.append(i == 0 ? "?" : ",?");
				args.add(id);
				i++;
			}
			hql.append(")");
		}
		return JpaUtils.executeQuery(entityManager, hql.toString(), args);
	}

	public int updatePassword(Long[] ids, String password) {
		if (ids == null || ids.length == 0)
			return 0;

		final List<Object> args = new ArrayList<>();
		final StringBuffer hql = new StringBuffer();
		hql.append("update AuthData a set a.password=?");
		args.add(password);

		// ids
		if (ids.length == 1) {
			hql.append(" where a.id=?");
			args.add(ids[0]);
		} else {
			int i = 0;
			hql.append(" where a.id in (");
			for (Long id : ids) {
				hql.append(i == 0 ? "?" : ",?");
				args.add(id);
				i++;
			}
			hql.append(")");
		}

		return JpaUtils.executeUpdate(entityManager, hql.toString(), args);
	}

	public AuthData save(AuthData authData) {
		if (null != authData) {
			if (authData.getId() > 0) {
				authData = this.entityManager.merge(authData);
			} else {
				this.entityManager.persist(authData);
			}
		}
		return authData;
	}

	public void delete(Long[] ids) {
		if (ids == null || ids.length == 0)
			return;

		List<Object> args = new ArrayList<>();
		StringBuffer hql = new StringBuffer();
		hql.append("delete AuthData a");
		if (ids.length == 1) {
			hql.append(" where a.id=?");
			args.add(ids[0]);
		} else {
			int i = 0;
			hql.append(" where a.id in (");
			for (Long id : ids) {
				hql.append(i == 0 ? "?" : ",?");
				args.add(id);
				i++;
			}
			hql.append(")");
		}
		JpaUtils.executeUpdate(entityManager, hql.toString(), args);
	}

	public void delete(Long id) {
		if (id != null) this.delete(new Long[]{id});
	}
}