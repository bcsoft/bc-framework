/**
 * 
 */
package cn.bc.db.jdbc;

import java.util.List;

/**
 * Sql查询对象的封装
 * 
 * @author dragon
 * 
 */
public class SqlObject<T extends Object> {
	private String sql;// sql查询语句
	private List<Object> args;// 查询参数
	private RowMapper<T> rowMapper;// 数据映射器

	public SqlObject() {
	}

	public SqlObject(String sql, List<Object> args) {
		this.sql = sql;
		this.args = args;
	}

	public String getSql() {
		return sql;
	}

	public SqlObject<T> setSql(String sql) {
		this.sql = sql;
		return this;
	}

	public List<Object> getArgs() {
		return args;
	}

	public SqlObject<T> setArgs(List<Object> args) {
		this.args = args;
		return this;
	}

	public RowMapper<T> getRowMapper() {
		return rowMapper;
	}

	public SqlObject<T> setRowMapper(RowMapper<T> rowMapper) {
		this.rowMapper = rowMapper;
		return this;
	}
}
