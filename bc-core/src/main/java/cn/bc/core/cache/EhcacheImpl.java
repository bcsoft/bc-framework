/**
 *
 */
package cn.bc.core.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * Ehcache缓存实现
 *
 * @author dragon
 * @deprecated use spring cache instead
 */
@Deprecated
public class EhcacheImpl implements Cache {
  private final String cacheName;
  private final CacheManager cacheManager;

  public EhcacheImpl(final CacheManager cacheManager) {
    this.cacheName = Cache.KEY;
    this.cacheManager = cacheManager;
  }

  public EhcacheImpl(final String cacheName, final CacheManager cacheManager) {
    this.cacheName = cacheName == null ? Cache.KEY : cacheName;
    this.cacheManager = cacheManager;
  }

  public void put(String key, Object value) {
    getCache().put(new Element(key, value));
  }

  @SuppressWarnings("unchecked")
  public <V> V get(String key) {
    Element element = getCache().get(key);
    if (element != null) {
      return (V) element.getObjectValue();
    }
    return null;
  }

  public Ehcache getCache() {
    return cacheManager.getEhcache(cacheName);
  }
}
