package cn.bc.cache.service;

import cn.bc.cache.domain.CacheManagement;
import cn.bc.core.exception.CoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.*;

/**
 * 缓存管理service接口事项类
 *
 * Created by Action on 2015/1/23.
 */
public class CacheServiceImpl implements CacheService {
    @Autowired
    private CacheManager cacheManager;

    @Override
    public List<CacheManagement> findExistCaches() {
        List<CacheManagement> cacheManagements = new ArrayList<>();

        // 缓存容器名称集合
        Set<String> containerNames = (Set<String>) this.cacheManager.getCacheNames();

        // 通过缓存容器名称获得所有存在的缓存
        for (String containerName : containerNames) {
            // 缓存
            Cache cache = this.cacheManager.getCache(containerName);
            // 获得缓存集合
            Object nativeCache = cache.getNativeCache();

            // 根据不同集合类型获得获得缓存Key，目前只有Map类型
            if (nativeCache instanceof Map) {// 缓存集合类型为Map
                Map<String, Object> map = (Map<String, Object>) nativeCache;
                Set<Object> cacheKeys = new HashSet<>();// 缓存key集合
                cacheKeys.addAll(map.keySet());

                CacheManagement cacheManagement = new CacheManagement();
                cacheManagement.setContainerName(containerName);// 设置容器名称
                cacheManagement.setCacheKeys(cacheKeys);// 缓存Key集合
                cacheManagements.add(cacheManagement);
            } else {// 其它集合类型，抛出异常
                throw new CoreException("Cache.getNativeCache 返回的不是Map");
            }
        }

        return cacheManagements;
    }

    @Override
    public List<Map<String, String>> findSimilarCaches(String containerName, String cacheKey) {
        // 缓存管理实体类集合
        List<CacheManagement> cacheManagements = this.findExistCaches();
        // 将实体类集合构造为格式 [{containerName:缓存容器名称, cacheKey:缓存Key}, ...] 并返回
        List<Map<String, String>> caches = new ArrayList<>();

        if (containerName == null && cacheKey == null) {// 参数为空，返回所有存在的缓存
            // 构造 缓存容器名称，缓存Key的List集合
            caches = this.buildNameKeyList(cacheManagements);
        } else {
            List<CacheManagement> tempList = new ArrayList<>();// 临时缓存管理实体类集合
            for (CacheManagement cacheManagement : cacheManagements) {
                String cName = cacheManagement.getContainerName();// 缓存容器名称
                Set<Object> cKeys = cacheManagement.getCacheKeys();// 缓存Key集合

                // 如果容器名称相似则将cacheManagement添加到临时集合内
                if (containerName != null && (cName.indexOf(containerName) != -1)) {
                    tempList.add(cacheManagement);
                    continue;// 容器名称匹配得上，则不判断cacheKey
                }

                // key相似，构造为CacheManagement对象并添加到tempList
                if ((cacheKey != null) && (cKeys != null && cKeys.size() > 0)) {
                    Set<Object> tempSet = new HashSet<>();
                    for (Object key : cKeys) {
                        if (String.valueOf(key).indexOf(cacheKey) != -1) {
                            tempSet.add(key);
                        }
                    }

                    if (tempSet.size() > 0) {
                        CacheManagement cm = new CacheManagement();
                        cm.setContainerName(cName);
                        cm.setCacheKeys(tempSet);
                        tempList.add(cm);
                    }
                }
            }

            if (tempList.size() > 0) {
                caches = this.buildNameKeyList(tempList);
            }
        }

        return caches;
    }

    /**
     * 构造 缓存容器名称，缓存Key的List<Map>集合
     *
     * @param cacheManagements 缓存管理实体类集合
     * @return [{containerName:缓存容器名称, cacheKey:缓存Key}, ...]
     */
    private List<Map<String, String>> buildNameKeyList(List<CacheManagement> cacheManagements) {
        List<Map<String, String>> caches = new ArrayList<>();

        // 将对象集合构造为 [{containerName:缓存容器名称, cacheKey:缓存Key}, ...] 格式并返回
        for (CacheManagement cacheManagement : cacheManagements) {
            String cName = cacheManagement.getContainerName();// 缓存容器名称
            Set<Object> cKeys = cacheManagement.getCacheKeys();// 缓存Key集合

            if (cKeys != null && cKeys.size() > 0) {
                for (Object cKey : cKeys) {
                    Map<String, String> map = new HashMap<>();
                    map.put("containerName", cName);
                    map.put("cacheKey", String.valueOf(cKey));
                    caches.add(map);
                }
            } else {
                Map<String, String> map = new HashMap<>();
                map.put("containerName", cName);
                map.put("cacheKey", null);
                caches.add(map);
            }
        }

        return caches;
    }

    @Override
    public void deleteExistCaches() {
        this.deleteCache(null, null);
    }

    @Override
    public void deleteCachesInContainer(String containerName) {
        this.deleteCache(containerName, null);
    }

    @Override
    public void deleteCache(String containerName, String cacheKey) {
        // 缓存管理实体类集合
        List<CacheManagement> cacheManagements = this.findExistCaches();
        for (CacheManagement cacheManagement : cacheManagements) {
            String cName = cacheManagement.getContainerName();
            Set<Object> cKeys = cacheManagement.getCacheKeys();
            // Cache接口
            Cache cache = this.cacheManager.getCache(cName);

            if (cacheKey != null) { // 删除指定Key的缓存
                if (cName.equals(containerName)) {
                    for (Object o : cKeys) {
                        if (cacheKey.equals(String.valueOf(o)))
                            cache.evict(o);
                    }
                }
            } else if (containerName != null){// 删除指定容器内的缓存
                if (cName.equals(containerName)) {
                    for (Object o : cKeys) {
                        cache.evict(o);
                    }
                }
            } else {// 删除所有缓存
                for (Object o : cKeys) {
                    cache.evict(o);
                }
            }
        }
    }
}
