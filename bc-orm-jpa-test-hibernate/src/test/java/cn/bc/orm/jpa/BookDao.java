package cn.bc.orm.jpa;

import org.hibernate.impl.QueryImpl;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

@Service
public class BookDao {
	private static Logger logger = LoggerFactory.getLogger(BookDao.class);

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public Book loadBook(Long id) {
		Book book = em.find(Book.class, id);
		if (book != null) {
			BookDetail detail = em.find(BookDetail.class, id);
			book.setDetail(detail);
		}
		return book;
	}

	@Transactional
	public void create(Object book) {
		em.persist(book);
	}

	@Transactional
	public void modify(Object book) {
		em.merge(book);
	}

	@Transactional
	public void removeBook(Long id) {
		em.remove(loadBook(id));
	}

	@SuppressWarnings("unchecked")
	public List<Book> findAllBook() {
		Query query = em.createQuery("from Book");
		return (List<Book>) query.getResultList();
	}

	public List find(String jpql, Map<String, Object> params) {
		logger.debug("props={}", em.getProperties());
		Query query = em.createQuery(jpql);
		if (params != null) {
			for (Map.Entry<String, Object> e : params.entrySet())
				query.setParameter(e.getKey(), e.getValue());
		}
		return query.getResultList();
	}

	public <T> List<T> find(String jpql, Map<String, Object> params, Class<T> resultClass) {
		logger.debug("props={}", em.getProperties());
		TypedQuery<T> query = em.createQuery(jpql, resultClass);
		if (params != null) {
			for (Map.Entry<String, Object> e : params.entrySet())
				query.setParameter(e.getKey(), e.getValue());
		}
		return query.getResultList();
	}

	public <T> List<T> find3(String jpql, Map<String, Object> params, Class<T> resultClass) {
		logger.debug("props={}", em.getProperties());
		TypedQuery<T> query = em.createQuery(jpql, resultClass);
		if (params != null) {
			for (Map.Entry<String, Object> e : params.entrySet())
				query.setParameter(e.getKey(), e.getValue());
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findByHibernate(String jpql) {
		/** http://blog.jiucai.org/topic/让jpa的query返回map对象
		 * EclipseLink 的 query.setHint(QueryHints.RESULT_TYPE, ResultType.Map);
		 * Hibernate 的 query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		 * OpenJPA的 QueryImpl> impl = q.unwrap(QueryImpl.class); impl.setResultClass(Map.class);
		 */
		Query query = em.createQuery(jpql);
		query.unwrap(QueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

		return (List<Map<String, Object>>) query.getResultList();
	}
}