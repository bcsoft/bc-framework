package cn.bc.core.query.cfg;

import cn.bc.core.query.condition.Condition;

import java.util.List;

/**
 * SQL分页查询语句及参数配置
 * Created by dragon on 2014/6/16.
 */
public interface PagingQueryConfig extends QueryConfig {
  /**
   * 获取包含指定条件的数据列表查询语句
   *
   * @param condition 查询条件
   * @return 数据列表查询语句
   */
  String getQueryString(Condition condition);

  /**
   * 获取数据列表查询语句中应该注入的参数
   *
   * @return 数据列表查询语句的参数
   */
  List<Object> getQueryParams(Condition condition);

  /**
   * 获取分页查询的起始条目索引号（从0开始）
   *
   * @return 分页查询的起始条目索引号
   */
  int getOffset();

  /**
   * 获取每页条目数限制
   *
   * @return 每页条目数限制
   */
  int getLimit();

  /**
   * 获取包含指定条件的总数查询语句
   * 如 select count(*) from ...
   *
   * @param condition 查询条件
   * @return 构建好的计数查询语句
   */
  String getTotalnumQueryString(Condition condition);

  /**
   * 获取总数查询语句中应该注入的参数
   *
   * @return 总数查询语句的参数
   */
  List<Object> getTotalnumQueryParams(Condition condition);
}