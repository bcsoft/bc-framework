package cn.bc.identity.dao;

import cn.bc.identity.domain.IdGenerator;
import cn.bc.orm.jpa.JpaUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * IdGeneratorDao接口的jpa实现
 *
 * @author dragon
 */
public class IdGeneratorDaoImpl implements IdGeneratorDao {
    @PersistenceContext
    private EntityManager entityManager;

    public IdGenerator load(String type) {
        String sql = "select type_,value_,format from bc_identity_idgenerator where type_=?";
        Query query = JpaUtils.createNativeQuery(entityManager, sql, new Object[]{type});
        Object[] r = JpaUtils.getSingleResult(query);
        if (r == null) return null;

        IdGenerator id = new IdGenerator();
        id.setType(type);
        id.setValue(new Long(r[1].toString()));
        id.setFormat(r[2] != null ? r[2].toString() : null);
        return id;
    }

    public Long currentValue(String type) {
        String sql = "select value_ from bc_identity_idgenerator where type_=?";
        Number v = JpaUtils.getSingleResult(JpaUtils.createNativeQuery(entityManager, sql, new Object[]{type}));
        if (v == null) return null;
        return v.longValue();
    }

    public boolean updateValue(String type, Long value) {
        String sql = "update bc_identity_idgenerator set value_=? where type_=?";
        int c = JpaUtils.executeNativeUpdate(entityManager, sql, new Object[]{value, type});
        return c > 0;
    }

    public void save(String type, Long value, String format) {
        String sql = "insert into bc_identity_idgenerator(type_,value_,format) values(?,?,?)";
        JpaUtils.executeNativeUpdate(entityManager, sql, new Object[]{type, value, format});
    }

    public Long getNextval() {
        String sql = "select NEXTVAL('HIBERNATE_SEQUENCE') from bc_dual";
        Number v = JpaUtils.getSingleResult(JpaUtils.createNativeQuery(entityManager, sql, (Object[]) null));
        if (v == null) return null;
        return v.longValue();
    }
}