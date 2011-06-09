package cn.bc.identity.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.util.StringUtils;

import cn.bc.identity.dao.AuthDataDao;
import cn.bc.identity.domain.AuthData;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 认证信息Dao接口的实现
 * 
 * @author dragon
 * 
 */
public class AuthDataDaoImpl implements AuthDataDao {
	private static final Log logger = LogFactory.getLog(AuthDataDaoImpl.class);
	private JpaTemplate jpaTemplate;

	public void setEntityManagerFactory(
			EntityManagerFactory entityManagerFactory) {
		this.jpaTemplate = new JpaTemplate(entityManagerFactory);
	}

	public AuthData load(Long id) {
		return jpaTemplate.find(AuthData.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<AuthData> find(Long[] ids) {
		if (ids == null || ids.length == 0)
			return new ArrayList<AuthData>();

		ArrayList<Object> args = new ArrayList<Object>();
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
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql.toString());
			logger.debug("args="
					+ StringUtils.collectionToCommaDelimitedString(args));
		}
		return jpaTemplate.find(hql.toString(), args.toArray());
	}

	public int updatePassword(Long[] ids, String password) {
		if (ids == null || ids.length == 0)
			return 0;

		final List<Object> args = new ArrayList<Object>();
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

		Object obj = this.jpaTemplate.execute(new JpaCallback<Object>() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				javax.persistence.Query query = HibernateCrudJpaDao
						.createQuery(em, hql.toString(), args.toArray());
				jpaTemplate.prepareQuery(query);
				return query.executeUpdate();
			}
		});
		return Integer.parseInt(String.valueOf(obj));
	}

	public AuthData save(AuthData authData) {
		if (null != authData) {
			if (authData.getId() > 0) {
				authData = this.jpaTemplate.merge(authData);
			} else {
				this.jpaTemplate.persist(authData);
			}
		}
		return authData;
	}

	public void delete(Long[] ids) {
		if (ids == null || ids.length == 0)
			return;

		final List<Object> args = new ArrayList<Object>();
		final StringBuffer hql = new StringBuffer();
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
		this.jpaTemplate.execute(new JpaCallback<Object>() {
			public Object doInJpa(EntityManager em) throws PersistenceException {
				javax.persistence.Query query = HibernateCrudJpaDao
						.createQuery(em, hql.toString(), args.toArray());
				jpaTemplate.prepareQuery(query);
				return query.executeUpdate();
			}
		});
	}

	public void delete(Long id) {
		if (id != null)
			this.delete(new Long[] { id });
	}
}
