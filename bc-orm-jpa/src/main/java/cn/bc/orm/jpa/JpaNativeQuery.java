package cn.bc.orm.jpa;

import cn.bc.core.Page;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Condition;
import cn.bc.db.JdbcUtils;
import cn.bc.db.jdbc.SqlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于jpa的原生查询接口实现
 *
 * @param <T> Domain类
 * @author dragon
 */
public class JpaNativeQuery<T> implements cn.bc.core.query.Query<T> {
	private static Logger logger = LoggerFactory.getLogger("cn.bc.orm.jpa.JpaNativeQuery");
	private EntityManager entityManager;
	private Condition condition;
	private SqlObject<T> sqlObject;

	private JpaNativeQuery() {
	}

	/**
	 * 构造一个基于jpa的Query实现
	 */
	public JpaNativeQuery(EntityManager entityManager, SqlObject<T> sqlObject) {
		this();
		this.entityManager = entityManager;
		this.sqlObject = sqlObject;
		Assert.notNull(entityManager, "entityManager is required");
		Assert.notNull(sqlObject, "sqlObject is required");
		//Assert.notNull(sqlObject.getSql(), "sqlObject.getSql() is required");
	}

	private String getSql() {
		return sqlObject.getSql(condition);
	}

	public JpaNativeQuery<T> condition(Condition condition) {
		this.condition = condition;
		return this;
	}

	public int count() {
		String sql = this.sqlObject.getCountSql(condition);

		// 合并参数
		List<Object> args = new ArrayList<>();
		if (sqlObject.getArgs() != null) args.addAll(sqlObject.getArgs());
		if (condition != null) {
			List<Object> values = condition.getValues();
			if (values != null) args.addAll(values);
		}

		Number c = JpaUtils.getSingleResult(JpaUtils.createNativeQuery(entityManager, sql, args));
		return c != null ? c.intValue() : 0;
	}

	public List<T> list() {
		String sql = getSql();

		// 合并参数
		List<Object> args = new ArrayList<>();
		if (sqlObject.getArgs() != null) args.addAll(sqlObject.getArgs());
		if (condition != null) {
			List<Object> values = condition.getValues();
			if (values != null) args.addAll(values);
		}

		return JpaUtils.executeNativeQuery(entityManager, sql, args.toArray(), sqlObject.getRowMapper());
	}

	@SuppressWarnings("unchecked")
	public List<T> list(int pageNo, int pageSize) {
		int _pageSize = pageSize < 1 ? 1 : pageSize;
		String sql = getSql();

		// 合并参数
		List<Object> args = new ArrayList<>();
		if (sqlObject.getArgs() != null) args.addAll(sqlObject.getArgs());
		if (condition != null) {
			List<Object> values = condition.getValues();
			if (values != null) args.addAll(values);
		}

		logger.debug("pageNo={}, pageSize={}", pageNo, _pageSize);
		Query query = JpaUtils.createNativeQuery(entityManager, sql, args);
		query.setFirstResult(Page.getFirstResult(pageNo, _pageSize));
		query.setMaxResults(_pageSize);
		return JdbcUtils.mapRows((List<Object[]>) query.getResultList(), sqlObject.getRowMapper());
	}

	public Page<T> page(int pageNo, int pageSize) {
		return new Page<>(pageNo, pageSize, this.count(), this.list(pageNo, pageSize));
	}

	@SuppressWarnings("unchecked")
	public T singleResult() {
		// 合并参数
		List<Object> args = new ArrayList<>();
		if (sqlObject.getArgs() != null)
			args.addAll(sqlObject.getArgs());
		if (condition != null) {
			List<Object> values = condition.getValues();
			if (values != null) args.addAll(values);
		}

		Query query = JpaUtils.createNativeQuery(entityManager, getSql(), args);
		Object[] r = JpaUtils.getSingleResult(query);
		if (r != null) {
			return sqlObject.getRowMapper() != null ? sqlObject.getRowMapper().mapRow(r, 0) : (T) r;
		} else {
			return null;
		}
	}

	public List<Object> listWithSelect(String select) {
		throw new CoreException("unsupport method.");
	}
}