package cn.bc.orm.hibernate.jpa;

/**
 * Object[] 数组值到对象的映射器
 * 
 * @author dragon
 * 
 * @param <T>
 */
public interface RowMapper<T> {
	T mapRow(Object[] rs, int rowNum);
}
