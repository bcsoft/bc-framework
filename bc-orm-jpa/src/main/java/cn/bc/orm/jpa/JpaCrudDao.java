package cn.bc.orm.jpa;

import cn.bc.core.SetEntityClass;
import cn.bc.core.dao.CrudDao;
import cn.bc.core.exception.CoreException;
import cn.bc.db.jdbc.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * CrudDao的JPA实现
 *
 * @param <T> Domain类
 * @author dragon
 */
public class JpaCrudDao<T extends Object> implements CrudDao<T>, SetEntityClass<T> {
    private static Logger logger = LoggerFactory.getLogger("cn.bc.orm.jpa.JpaCrudDao");
    protected Class<T> entityClass;
    protected String pkName = "id";// 主键名称
    private EntityManager entityManager;

    /**
     * 通过构造函数注入实体对象的类型, 因为Java的泛型无法使用T.class的缘故
     *
     * @param clazz
     */
    public JpaCrudDao(Class<T> clazz) {
        this.entityClass = clazz;
    }

    /**
     * 如果子类将泛型参数具体化了，这个构造函数用于自动侦测实体对象的类型
     */
    @SuppressWarnings("unchecked")
    public JpaCrudDao() {
        // 这个需要子类中指定T为实际的类才有效
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (type instanceof Class) this.entityClass = (Class<T>) type;
        }
    }

    public void setPkName(String pkName) {
        this.pkName = pkName;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<T> persistentClass) {
        this.entityClass = persistentClass;
    }

    /**
     * 返回默认的基于jpa实现的查询器
     *
     * @see cn.bc.core.dao.CrudDao#createQuery()
     */
    public cn.bc.core.query.Query<T> createQuery() {
        return defaultQuery(this.entityClass);
    }

    protected cn.bc.core.query.Query<T> defaultQuery(Class<T> persistentClass) {
        if (persistentClass == null)
            return new JpaQuery<>(this.entityManager);
        else
            return new JpaQuery<>(this.entityManager, persistentClass);
    }

    public void delete(Serializable pk) {
        if (pk == null)
            return;

        // 1)hibernate会自动组合find、delete的语句，
        // 最终只执行一句,如：Hibernate: delete from QC_EXAMPLE where ID=?
        // 2)如果要删除的对象为null，会抛异常：nested exception is
        // java.lang.IllegalArgumentException: attempt to create delete event
        // with null entity
        T e = this.entityManager.find(entityClass, pk);
        if (e != null)
            this.entityManager.remove(e);
    }

    public void delete(Serializable[] pks) {
        if (pks == null || pks.length == 0)
            return;

        List<Object> args = new ArrayList<>();
        StringBuffer jpql = new StringBuffer();
        jpql.append("delete ").append(this.getEntityClass().getSimpleName());
        if (pks.length == 1) {
            jpql.append(" where ").append(pkName).append("=?");
            args.add(pks[0]);
        } else {
            int i = 0;
            jpql.append(" where ").append(pkName).append(" in (");
            for (Serializable pk : pks) {
                jpql.append(i == 0 ? "?" : ",?");
                args.add(pk);
                i++;
            }
            jpql.append(")");
        }
        this.executeUpdate(jpql.toString(), args);
    }

    public T load(Serializable pk) {
        return this.entityManager.find(this.entityClass, pk);
    }

    public T forceLoad(Serializable pk) {
        T e = this.entityManager.find(this.entityClass, pk);
        this.entityManager.refresh(e);
        return e;
    }

    @SuppressWarnings("rawtypes")
    public T save(T entity) {
        if (null != entity) {
            if (entity instanceof cn.bc.core.Entity) {
                if (((cn.bc.core.Entity) entity).isNew()) {
                    this.entityManager.persist(entity);
                } else {
                    // 执行完merge后entity还是detached, merge后返回的新对象才是Persistent
                    entity = this.entityManager.merge(entity);
                }
            } else {
                this.entityManager.persist(entity);// may be error if had set the
                // id!
            }
        }
        return entity;
    }

    public void save(Collection<T> entities) {
        if (null != entities && !entities.isEmpty()) {
            for (T entity : entities) this.entityManager.persist(entity);
        }
    }

    public void update(Serializable pk, Map<String, Object> attributes) {
        if (pk == null || attributes == null || attributes.isEmpty())
            return;
        this.update(new Serializable[]{pk}, attributes);
    }

    public void update(Serializable[] pks, Map<String, Object> attributes) {
        if (pks == null || pks.length == 0 || attributes == null || attributes.isEmpty())
            return;

        List<Object> args = new ArrayList<>();
        StringBuffer jpql = new StringBuffer();
        jpql.append("update ").append(this.getEntityClass().getSimpleName()).append(" _alias");

        // set
        int i = 0;
        Object value;
        for (String key : attributes.keySet()) {
            value = attributes.get(key);
            if (value != null) {
                if (i > 0)
                    jpql.append(",_alias.").append(key).append("=?");
                else
                    jpql.append(" set _alias.").append(key).append("=?");
                args.add(attributes.get(key));
            } else {
                if (i > 0)
                    jpql.append(",_alias.").append(key).append("=null");
                else
                    jpql.append(" set _alias.").append(key).append("=null");
            }
            i++;
        }

        // pks
        if (pks.length == 1) {
            jpql.append(" where _alias.").append(pkName).append("=?");
            args.add(pks[0]);
        } else {
            i = 0;
            jpql.append(" where _alias.").append(pkName).append(" in (");
            for (Serializable pk : pks) {
                jpql.append(i == 0 ? "?" : ",?");
                args.add(pk);
                i++;
            }
            jpql.append(")");
        }

        this.executeUpdate(jpql.toString(), args);
    }

    public T create() {
        try {
            T e = this.getEntityClass().newInstance();
            logger.debug("initialize entity in JpaCrudDao.");
            return e;
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(e.getMessage(), e);
            throw new CoreException(e);
        }
    }

    protected int executeUpdate(String jpql, List<Object> args) {
        return JpaUtils.executeUpdate(getEntityManager(), jpql, args);
    }

    protected int executeUpdate(String jpql, Object[] args) {
        return JpaUtils.executeUpdate(getEntityManager(), jpql, args);
    }

    protected int executeNativeUpdate(String sql, Object[] args) {
        return JpaUtils.executeNativeUpdate(getEntityManager(), sql, args);
    }

    protected int executeNativeUpdate(String sql) {
        return JpaUtils.executeNativeUpdate(getEntityManager(), sql, (Object[]) null);
    }

    protected int executeNativeUpdate(String sql, List<Object> args) {
        return JpaUtils.executeNativeUpdate(getEntityManager(), sql, args);
    }

    protected <C> List<C> executeQuery(String jpql, Object[] args) {
        return JpaUtils.executeQuery(getEntityManager(), jpql, args);
    }

    protected <C> List<C> executeQuery(String jpql, List<Object> args) {
        return JpaUtils.executeQuery(getEntityManager(), jpql, args);
    }

    protected <C> List<C> executeQuery(String jpql, Object[] args, RowMapper<C> rowMapper) {
        return JpaUtils.executeQuery(getEntityManager(), jpql, args, rowMapper);
    }

    protected <C> List<C> executeQuery(String jpql, List<Object> args, RowMapper<C> rowMapper) {
        return JpaUtils.executeQuery(getEntityManager(), jpql, args, rowMapper);
    }

    protected <C> List<C> executeNativeQuery(String sql, Object[] args) {
        return JpaUtils.executeNativeQuery(getEntityManager(), sql, args);
    }

    protected <C> List<C> executeNativeQuery(String sql, List<Object> args) {
        return JpaUtils.executeNativeQuery(getEntityManager(), sql, args);
    }

    protected <C> List<C> executeNativeQuery(String sql, Object[] args, RowMapper<C> rowMapper) {
        return JpaUtils.executeNativeQuery(getEntityManager(), sql, args, rowMapper);
    }

    protected <C> List<C> executeNativeQuery(String sql, List<Object> args, RowMapper<C> rowMapper) {
        return JpaUtils.executeNativeQuery(getEntityManager(), sql, args, rowMapper);
    }

    /**
     * @see JpaUtils#queryForMap(Query, String[])
     */
    protected Map<String, Object> queryForMap(Query query, String[] keys) {
        return JpaUtils.queryForMap(query, keys);
    }

    /**
     * @see JpaUtils#queryForObject(Query, Class)
     */
    protected <T> T queryForObject(Query query, Class<T> requiredType) {
        return JpaUtils.queryForObject(query, requiredType);
    }

    protected <T> T executeQueryToObject(String jpql, Class<T> requiredType) {
        return JpaUtils.queryForObject(JpaUtils.createQuery(getEntityManager(), jpql), requiredType);
    }

    protected <T> T executeQueryToObject(String jpql, Object[] args, Class<T> requiredType) {
        return JpaUtils.queryForObject(JpaUtils.createQuery(getEntityManager(), jpql, args), requiredType);
    }

    protected <T> T executeQueryToObject(String jpql, List<Object> args, Class<T> requiredType) {
        return JpaUtils.queryForObject(JpaUtils.createQuery(getEntityManager(), jpql, args), requiredType);
    }

    protected <T> T executeNativeQueryToObject(String sql, Class<T> requiredType) {
        return JpaUtils.queryForObject(JpaUtils.createNativeQuery(getEntityManager(), sql), requiredType);
    }

    protected <T> T executeNativeQueryToObject(String sql, Object[] args, Class<T> requiredType) {
        return JpaUtils.queryForObject(JpaUtils.createNativeQuery(getEntityManager(), sql, args), requiredType);
    }

    protected <T> T executeNativeQueryToObject(String sql, List<Object> args, Class<T> requiredType) {
        return JpaUtils.queryForObject(JpaUtils.createNativeQuery(getEntityManager(), sql, args), requiredType);
    }
}