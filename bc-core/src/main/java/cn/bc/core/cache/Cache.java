/**
 *
 */
package cn.bc.core.cache;

/**
 * 简易缓存接口
 *
 * @author dragon
 * @deprecated use spring cache instead
 */
@Deprecated
public interface Cache {
  /**
   * 默认的缓存键
   */
  public final static String KEY = "cn.bc.cache.framework";

  /**
   * 设置指定键值的缓存
   *
   * @param key   键
   * @param value 值
   */
  void put(String key, Object value);

  /**
   * 获取缓存的值
   *
   * @param key 键
   * @return 缓存的值
   */
  <V> V get(String key);
}
