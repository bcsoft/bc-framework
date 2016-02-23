package cn.bc.db.jdbc.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import cn.bc.core.Page;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Condition;

/**
 * 基于jdbc的查询接口实现
 * 
 * @author dragon
 * 
 * @param <T>
 */
public class SpringJdbcQuery<T extends Object> implements cn.bc.core.query.Query<T> {
	private JdbcTemplate jdbcTemplate;
	private Condition condition;
	private String sql;
	private List<Object> sqlArgs = new ArrayList<>();
	private RowMapper<T> rowMapper;// 行包装器

	public void setSqlArgs(List<Object> sqlArgs) {
		this.sqlArgs = sqlArgs;
	}

	private String getSql() {
		String sql_ = sql;
		if (condition != null)
			sql_ += " where " + this.condition.getExpression();
		return sql_;
	}

	public SpringJdbcQuery<T> setSql(String sql) {
		this.sql = sql;
		return this;
	}

	private SpringJdbcQuery() {
	}

	/**
	 * 构造一个基于jdbc的Query实现
	 */
	public SpringJdbcQuery(DataSource dataSource, RowMapper<T> rowMapper) {
		this();
		Assert.notNull(dataSource, "dataSource is required");
		Assert.notNull(rowMapper, "rowMapper is required");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.rowMapper = rowMapper;
	}

	public cn.bc.core.query.Query<T> condition(
			cn.bc.core.query.condition.Condition condition) {
		this.condition = condition;
		if (this.condition != null)
			sqlArgs.addAll(this.condition.getValues());
		return this;
	}

	public int count() {
		String queryTemp = removeOrderBy(getSql());
		if (!queryTemp.startsWith("select")) {
			if(queryTemp.startsWith("!!with")){
				int i = queryTemp.indexOf("!!select");
				int y=queryTemp.indexOf("!!from");
				String toReplace=queryTemp.substring(i,y);
				queryTemp=queryTemp.replace(toReplace,"select count(*) ").replace("!!","");
			}else {
				queryTemp = "select count(*) " + queryTemp;
			}
		} else {
			queryTemp = "select count(*) " + removeSelect(queryTemp);
		}
		return this.jdbcTemplate.queryForObject(queryTemp, this.sqlArgs.toArray(), Integer.class);
	}

	public T singleResult() {
		try {
			return this.jdbcTemplate.queryForObject(this.getSql(),
					this.sqlArgs.toArray(), this.rowMapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<T> list() {
		return this.jdbcTemplate.query(getSql().replace("!!",""), this.sqlArgs.toArray(),
				this.rowMapper);
	}

	public List<T> list(int pageNo, int pageSize) {
		return this.jdbcTemplate.query(getSql().replace("!!",""), new SplitPageRowMapperResultSetExtractor<>(rowMapper, pageNo, pageSize));
	}

	public Page<T> page(int pageNo, int pageSize) {
		return new Page<>(pageNo, pageSize, this.count(), this.list(pageNo, pageSize));
	}

	public List<Object> listWithSelect(String select) {
		throw new CoreException("unsupport method.");
	}

	private static String removeOrderBy(String queryString) {
		if(!queryString.startsWith("!!")) {
			Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*",
					Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(queryString);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				m.appendReplacement(sb, "");
			}
			m.appendTail(sb);
			return sb.toString();
		}else{
			int i = queryString.indexOf("!!order by");
			return queryString.substring(0, i);
		}
	}

	private static String removeSelect(String queryString) {
		int beginPos = queryString.toLowerCase().indexOf("from");
		return beginPos != -1 ? queryString.substring(beginPos) : queryString;
	}

}
