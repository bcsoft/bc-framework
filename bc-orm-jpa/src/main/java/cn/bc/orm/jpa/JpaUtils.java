package cn.bc.orm.jpa;

import cn.bc.db.JdbcUtils;
import cn.bc.db.jdbc.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JpaUtils {
	private static Logger logger = LoggerFactory.getLogger(JpaUtils.class);

	/**
	 * 剔除查询语句中的排序语句
	 *
	 * @param ql 查询的语句
	 * @return 如果存在排序语句，则将排序语句剔除，否则返回原查询语句
	 */
	public static String removeOrderBy(String ql) {
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(ql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 剔除查询语句中的选择语句
	 *
	 * @param ql 查询的语句
	 * @return 如果存在选择语句，则将选择语句剔除，否则返回原查询语句
	 */
	public static String removeSelect(String ql) {
		ql = ql.replaceAll("[\\f\\n\\r\\t\\v]", " ");// 替换所有制表符、换页符、换行符、回车符为空格
		String regex = "^select .* from ";
		String[] ss = ql.split(regex);
		if (ss.length > 0) {
			return "from " + ss[1];
		} else {
			return ql;
		}
	}

	/**
	 * 创建查询对象
	 *
	 * @param jpql 查询语句
	 * @param args 查询语句中的参数
	 * @return 构建好的查询对象
	 */
	public static Query createQuery(EntityManager em, String jpql, Object[] args) {
		logger.debug("args={}, jpql={}", args, jpql);
		Query queryObj = em.createQuery(jpql);
		if (null != args && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				queryObj.setParameter(i + 1, args[i]);// jpa索引从1开始
			}
		}
		return queryObj;
	}

	public static Query createNativeQuery(EntityManager em, String sql, Object[] args) {
		return createNativeQuery(em, sql, args, null);
	}

	/**
	 * 创建原生查询对象
	 *
	 * @param sql         查询语句
	 * @param args        查询语句中的参数
	 * @param resultClass the class of the resulting instance(s)
	 * @return 构建好的查询对象
	 */
	public static Query createNativeQuery(EntityManager em, String sql, Object[] args, Class resultClass) {
		logger.debug("args={}, resultClass={}, sql={}", args, resultClass, sql);
		Query queryObj = resultClass == null ? em.createNativeQuery(sql) : em.createNativeQuery(sql, resultClass);
		if (null != args && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				queryObj.setParameter(i + 1, args[i]);// jpa索引从1开始
			}
		}
		return queryObj;
	}

	public static Query createNativeQuery(EntityManager em, String sql, List<Object> args, Class resultClass) {
		return createNativeQuery(em, sql, args != null ? args.toArray() : null, resultClass);
	}

	public static Query createNativeQuery(EntityManager em, String sql, List<Object> args) {
		return createNativeQuery(em, sql, args != null ? args.toArray() : null);
	}

	public static Query createQuery(EntityManager em, String jpql, List<Object> args) {
		return createQuery(em, jpql, args != null ? args.toArray() : null);
	}

	public static int executeUpdate(EntityManager entityManager, String jpql, List<Object> args) {
		return executeUpdate(entityManager, jpql, args != null ? args.toArray() : null);
	}

	public static int executeUpdate(EntityManager entityManager, String jpql, Object[] args) {
		if (logger.isDebugEnabled())
			logger.debug("args={}, jpql={}", StringUtils.arrayToCommaDelimitedString(args), jpql);
		Query query = createQuery(entityManager, jpql, args);
		int c = query.executeUpdate();
		logger.debug("executeUpdate count={}", c);
		return c;
	}

	public static int executeNativeUpdate(EntityManager entityManager, String sql, List<Object> args) {
		return executeNativeUpdate(entityManager, sql, args != null ? args.toArray() : null);
	}

	public static int executeNativeUpdate(EntityManager entityManager, String sql, Object[] args) {
		if (logger.isDebugEnabled())
			logger.debug("args={}, sql={}", StringUtils.arrayToCommaDelimitedString(args), sql);
		Query query = createNativeQuery(entityManager, sql, args);
		int c = query.executeUpdate();
		logger.debug("executeNativeUpdate count={}", c);
		return c;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> executeQuery(EntityManager entityManager, String jpql, Object[] args, RowMapper<T> rowMapper) {
		if (logger.isDebugEnabled())
			logger.debug("args={}, jpql={}", StringUtils.arrayToCommaDelimitedString(args), jpql);
		Query query = createQuery(entityManager, jpql, args);
		return JdbcUtils.mapRows((List<Object[]>) query.getResultList(), rowMapper);
	}

	public static <T> List<T> executeQuery(EntityManager entityManager, String jpql, List<Object> args) {
		return executeQuery(entityManager, jpql, args != null ? args.toArray() : null);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> executeQuery(EntityManager entityManager, String jpql, Object[] args) {
		if (logger.isDebugEnabled())
			logger.debug("args={}, jpql={}", StringUtils.arrayToCommaDelimitedString(args), jpql);
		Query query = createQuery(entityManager, jpql, args);
		return (List<T>) query.getResultList();
	}

	public static <T> List<T> executeQuery(EntityManager entityManager, String jpql, List<Object> args, RowMapper<T> rowMapper) {
		return executeQuery(entityManager, jpql, args != null ? args.toArray() : null, rowMapper);
	}

	public static <T> List<T> executeNativeQuery(EntityManager entityManager, String sql, List<Object> args, RowMapper<T> rowMapper) {
		return executeNativeQuery(entityManager, sql, args != null ? args.toArray() : null, rowMapper);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> executeNativeQuery(EntityManager entityManager, String sql, Object[] args, RowMapper<T> rowMapper) {
		if (logger.isDebugEnabled())
			logger.debug("args={}, sql={}", StringUtils.arrayToCommaDelimitedString(args), sql);
		Query query = createNativeQuery(entityManager, sql, args);
		return JdbcUtils.mapRows((List<Object[]>) query.getResultList(), rowMapper);
	}

	public static <T> List<T> executeNativeQuery(EntityManager entityManager, String sql, List<Object> args) {
		return executeNativeQuery(entityManager, sql, args != null ? args.toArray() : null);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> executeNativeQuery(EntityManager entityManager, String sql, Object[] args) {
		if (logger.isDebugEnabled())
			logger.debug("args={}, sql={}", StringUtils.arrayToCommaDelimitedString(args), sql);
		Query query = createNativeQuery(entityManager, sql, args);
		return (List<T>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public static <T> T getSingleResult(Query query) {
		try {
			return (T) query.getSingleResult();
		} catch (NoResultException | EmptyResultDataAccessException e) {
			return null;
		}
	}
}