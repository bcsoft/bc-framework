package cn.bc.db.jdbc;

/**
 * Object[] 数组值到对象的映射器
 *
 * @param <T>
 * @author dragon
 */
public interface RowMapper<T> {
  T mapRow(Object[] rs, int rowNum);
}
