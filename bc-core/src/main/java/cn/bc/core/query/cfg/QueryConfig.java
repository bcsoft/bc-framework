package cn.bc.core.query.cfg;

import cn.bc.core.query.condition.Condition;

import java.util.List;

/**
 * SQL查询语句及参数配置
 * Created by dragon on 2014/6/16.
 */
public interface QueryConfig {
    /**
     * 获取包含指定条件的查询语句
     *
     * @param condition 查询条件
     * @return 条件查询语句
     */
    String getQueryString(Condition condition);

    /**
     * 获取查询语句中应该注入的参数
     *
     * @return 条件查询语句的参数
     */
    List<Object> getQueryParams(Condition condition);
}