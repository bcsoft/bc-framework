package cn.bc.db.jdbc.spring;

import cn.bc.core.Page;
import cn.bc.core.query.Query;
import cn.bc.core.query.cfg.PagingQueryConfig;
import cn.bc.core.query.condition.Condition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 基于 JdbcTemplate 的分页查询接口实现
 *
 * @param <T>
 * @author dragon
 */
public class JdbcTemplatePagingQuery<T extends Object> implements cn.bc.core.query.Query<T> {
    protected static final Log logger = LogFactory.getLog("cn.bc.db.jdbc.spring.JdbcTemplatePagingQuery");
    private JdbcTemplate jdbcTemplate;
    private Condition condition;
    private PagingQueryConfig pagingQueryConfig;
    private RowMapper<T> rowMapper;// 行包装器

    private JdbcTemplatePagingQuery() {
    }

    /**
     * @param jdbcTemplate
     * @param pagingQueryConfig
     * @param rowMapper
     */
    @SuppressWarnings("unchecked")
    public JdbcTemplatePagingQuery(JdbcTemplate jdbcTemplate, PagingQueryConfig pagingQueryConfig, RowMapper<T> rowMapper) {
                Assert.notNull(pagingQueryConfig, "pagingQueryConfig is required");
        Assert.notNull(jdbcTemplate, "jdbcTemplate is required");
        this.jdbcTemplate = jdbcTemplate;
        this.pagingQueryConfig = pagingQueryConfig;
        if (rowMapper == null) {
            this.rowMapper = (RowMapper<T>) new ColumnMapRowMapper();
        }
    }

    public Query<T> condition(Condition condition) {
        this.condition = condition;
        return this;
    }

    public int count() {
        String sql = pagingQueryConfig.getTotalnumQueryString(this.condition);
        List<Object> args = pagingQueryConfig.getTotalnumQueryParams(this.condition);
        if(logger.isDebugEnabled()){
            logger.debug("args=" + StringUtils.collectionToCommaDelimitedString(args) + ";sql=" + sql);
        }
        return this.jdbcTemplate.queryForObject(sql, (args != null && !args.isEmpty() ? args.toArray() : null), Integer.class);
    }

    public List<T> list(int pageNo, int pageSize) {
        return list();
    }

    public Page<T> page(int pageNo, int pageSize) {
        return new Page<>(pageNo, pageSize, this.count(), this.list(pageNo, pageSize));
    }

    public T singleResult() {
        throw new UnsupportedOperationException();
    }

    public List<T> list() {
        String sql = pagingQueryConfig.getQueryString(this.condition);
        List<Object> args = pagingQueryConfig.getQueryParams(this.condition);
        if(logger.isDebugEnabled()){
            logger.debug("args=" + StringUtils.collectionToCommaDelimitedString(args) + ";sql=" + sql);
        }
        return this.jdbcTemplate.query(sql, (args != null && !args.isEmpty() ? args.toArray() : null), rowMapper);
    }

    public List<Object> listWithSelect(String select) {
        throw new UnsupportedOperationException();
    }
}