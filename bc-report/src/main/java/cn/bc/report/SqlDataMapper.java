package cn.bc.report;

import java.util.List;
import java.util.Map;

/**
 * sql 数据映射器接口。
 *
 * @param <T> 转换后的数据
 */
public interface SqlDataMapper<T> {
  /**
   * 转换数据。
   *
   * @param originData 原始数据
   * @return 转换后的数据
   */
  T map(List<Map<String, Object>> originData);
}
