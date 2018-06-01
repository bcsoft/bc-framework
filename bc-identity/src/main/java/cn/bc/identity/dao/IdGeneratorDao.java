package cn.bc.identity.dao;

import cn.bc.identity.domain.IdGenerator;

/**
 * IdGeneratorDao接口
 *
 * @author dragon
 */
public interface IdGeneratorDao {
  /**
   * 获取配置
   *
   * @param type
   * @return
   */
  IdGenerator load(String type);

  /**
   * 获取当前的值
   *
   * @param type
   * @return
   */
  Long currentValue(String type);

  /**
   * 更新值
   *
   * @param type
   * @param value
   * @return
   */
  boolean updateValue(String type, Long value);

  /**
   * 插入一条新的记录
   *
   * @param type
   * @param value
   * @param format
   */
  void save(String type, Long value, String format);
}
