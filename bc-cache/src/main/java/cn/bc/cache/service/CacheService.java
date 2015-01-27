package cn.bc.cache.service;

import cn.bc.cache.domain.CacheManagement;

import java.util.List;
import java.util.Map;

/**
 * 缓存管理service接口
 *
 * Created by Action on 2015/1/23.
 */
public interface CacheService {
    /**
     * 查找所有存在的缓存
     *
     * @return 缓存管理实体类集合
     */
    List<CacheManagement> findExistCaches();

    /**
     * 查找所有名称相似的缓存
     *
     * @param containerName 缓存容器名称
     * @param cacheKey 缓存Key
     * @return [{containerName:缓存容器名称, cacheKey:缓存Key}, ...]
     */
    List<Map<String, String>> findSimilarCaches(String containerName, String cacheKey);

    /**
     * 删除所有存在的缓存
     */
    void deleteExistCaches();

    /**
     * 删除一个容器内的所有缓存
     *
     * @param containerName 缓存容器名称
     */
    void deleteCachesInContainer(String containerName);

    /**
     * 删除某容器内一条缓存
     *
     * @param containerName 缓存容器名称
     * @param cacheKey 缓存Key
     */
    void deleteCache(String containerName, String cacheKey);
}
