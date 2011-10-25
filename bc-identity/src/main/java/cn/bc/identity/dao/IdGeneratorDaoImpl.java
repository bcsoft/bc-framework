package cn.bc.identity.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

import cn.bc.identity.domain.IdGenerator;

/**
 * IdGeneratorDao接口的jpa实现
 * 
 * @author dragon
 * 
 */
public class IdGeneratorDaoImpl implements IdGeneratorDao {
	private JpaTemplate jpaTemplate;

	public void setEntityManagerFactory(
			EntityManagerFactory entityManagerFactory) {
		this.jpaTemplate = new JpaTemplate(entityManagerFactory);
	}

	public IdGenerator load(final String type) {
		try {
			return jpaTemplate.execute(new JpaCallback<IdGenerator>() {
				public IdGenerator doInJpa(EntityManager em)
						throws PersistenceException {
					Query queryObject = em
							.createNativeQuery("select type_,value_,format from bc_identity_idgenerator where type_=?");
					// jpa的索引号从1开始
					queryObject.setParameter(1, type);
					
					Object[] r = (Object[]) queryObject.getSingleResult();
					IdGenerator id = new IdGenerator();
					id.setType(type);
					id.setValue(new Long(r[1].toString()));
					id.setFormat(r[2] != null ? r[2].toString() : null);
					return id;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public Long currentValue(final String type) {
		try {
			return jpaTemplate.execute(new JpaCallback<Long>() {
				public Long doInJpa(EntityManager em)
						throws PersistenceException {
					Query queryObject = em
							.createNativeQuery("select value_ from bc_identity_idgenerator where type_=?");
					// jpa的索引号从1开始
					queryObject.setParameter(1, type);
					return new Long(queryObject.getSingleResult().toString());
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public boolean updateValue(final String type, final Long value) {
		int r = jpaTemplate.execute(new JpaCallback<Integer>() {
			public Integer doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em
						.createNativeQuery("update bc_identity_idgenerator set value_=? where type_=?");
				// jpa的索引号从1开始
				queryObject.setParameter(1, value);
				queryObject.setParameter(2, type);
				return queryObject.executeUpdate();
			}
		});
		return r > 0;
	}

	public void save(final String type, final Long value, final String format) {
		jpaTemplate.execute(new JpaCallback<Integer>() {
			public Integer doInJpa(EntityManager em)
					throws PersistenceException {
				Query queryObject = em
						.createNativeQuery("insert into bc_identity_idgenerator(type_,value_,format) values(?,?,?)");
				// jpa的索引号从1开始
				queryObject.setParameter(1, type);
				queryObject.setParameter(2, value);
				queryObject.setParameter(3, format);
				return queryObject.executeUpdate();
			}
		});
	}
}
