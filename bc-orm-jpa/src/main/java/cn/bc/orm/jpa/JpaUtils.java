package cn.bc.orm.jpa;

import cn.bc.db.JdbcUtils;
import cn.bc.db.jdbc.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  public static Query createQuery(EntityManager em, String jpql, List<Object> args) {
    return createQuery(em, jpql, args != null ? args.toArray() : null);
  }

  public static Query createQuery(EntityManager em, String jpql) {
    return createQuery(em, jpql, (Object[]) null);
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

  public static Query createNativeQuery(EntityManager em, String sql) {
    return createNativeQuery(em, sql, (Object[]) null);
  }

  public static Query createNativeQuery(EntityManager em, String sql, Object[] args) {
    return createNativeQuery(em, sql, args, null);
  }

  public static Query createNativeQuery(EntityManager em, String sql, List<Object> args, Class resultClass) {
    return createNativeQuery(em, sql, args != null ? args.toArray() : null, resultClass);
  }

  public static Query createNativeQuery(EntityManager em, String sql, List<Object> args) {
    return createNativeQuery(em, sql, args != null ? args.toArray() : null);
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

  public static <T> List<T> executeQuery(EntityManager entityManager, String jpql) {
    return executeQuery(entityManager, jpql, (Object[]) null);
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

  public static <T> List<T> executeNativeQuery(EntityManager entityManager, String sql) {
    return executeNativeQuery(entityManager, sql, (Object[]) null);
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

  /**
   * 获取单个查询结果
   *
   * @return 如果没有就返回null，有1个则返回
   * @throws NonUniqueResultException if more than one result
   */
  @SuppressWarnings("unchecked")
  public static <T> T getSingleResult(Query query) {
    try {
      return (T) query.getSingleResult();
    } catch (NoResultException | EmptyResultDataAccessException e) {
      return null;
    }
  }

  /**
   * 获取单个查询结果
   * <ul>
   * <li>1. 对于 Number 类型，如果为 null，则自动转换为 0</li>
   * <li>2. 对于 Boolean 类型，如果为 null，则自动转换为 false</li>
   * </ul>
   *
   * @throws NonUniqueResultException if more than one result
   */
  @SuppressWarnings("unchecked")
  public static <T> T queryForObject(Query query, Class<T> requiredType) {
    Object result = getSingleResult(query);
    return (T) convertValueToRequiredType(result, requiredType);
  }

  /**
   * this method is ref from {@code org.springframework.jdbc.core.SingleColumnRowMapper}
   *
   * @see org.springframework.jdbc.core.SingleColumnRowMapper#convertValueToRequiredType(Object value, Class requiredType)
   */
  private static Object convertValueToRequiredType(Object value, Class requiredType) {
    if (value != null && requiredType != null && !requiredType.isInstance(value)) {
      if (String.class.equals(requiredType)) {
        return value.toString();
      } else if (Number.class.isAssignableFrom(requiredType)) {
        if (value instanceof Number) {
          // Convert original Number to target Number class.
          return NumberUtils.convertNumberToTargetClass(((Number) value), requiredType);
        } else {
          // Convert stringified value to target Number class.
          return NumberUtils.parseNumber(value.toString(), requiredType);
        }
      } else {
        throw new IllegalArgumentException(
          "Value [" + value + "] is of type [" + value.getClass().getName() +
            "] and cannot be converted to required type [" + requiredType.getName() + "]");
      }
    } else if (value == null && requiredType != null) {
      if (Number.class.isAssignableFrom(requiredType)) {
        // 数值类型时，为空则返回 0 代替
        return NumberUtils.convertNumberToTargetClass(0, requiredType);
      } else if (Boolean.class.isAssignableFrom(requiredType)) {
        // 布尔类型时，为空则返回 false 代替
        return false;
      } else {
        return null;
      }
    }
    return value;
  }

  /**
   * 将查询的单个结果转换为 Map 结构
   *
   * @param keys Map中的key列表
   * @return 如果查询不到结果，返回 null
   * @throws NonUniqueResultException 如果查询结果返回多于1个
   * @throws IllegalArgumentException 如果键列表的长度与查询结果的列数不一致时
   */
  public static Map<String, Object> queryForMap(Query query, String[] keys) {
    if (keys == null || keys.length == 0) throw new IllegalArgumentException("keys could not be null or empty");
    List results = query.getResultList();
    if (results == null || results.isEmpty()) return null;
    if (results.size() > 1) {
      //throw new IncorrectResultSizeDataAccessException(1, results.size());
      throw new NonUniqueResultException("Incorrect result size: expected 1, actual " + results.size());
    }
    Object result = results.iterator().next();
    boolean isSingleColumn = !(result instanceof Object[]);
    Map<String, Object> m = new HashMap<>();
    if (isSingleColumn) {
      if (keys.length != 1)
        throw new IllegalArgumentException("keys length (=" + keys.length + ") not equals to column's length (=1)");
      m.put(keys[0], result);
    } else {
      Object[] oo = (Object[]) result;
      if (oo.length != keys.length)
        throw new IllegalArgumentException("keys length (=" + keys.length + ") not equals to column's length (=" + oo.length + ")");
      for (int i = 0; i < oo.length; i++) {
        m.put(keys[i], oo[i]);
      }
    }
    return m;
  }
}