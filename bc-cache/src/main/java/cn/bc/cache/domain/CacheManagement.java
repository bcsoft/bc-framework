package cn.bc.cache.domain;

import java.util.Set;

/**
 * 视图管理实体类
 *
 * Created by Action on 2015/1/26.
 */
public class CacheManagement {
    /**
     * 缓存容器名称
     */
    private String containerName;

    /**
     * <p>缓存Key集合，建议使用缓存的时候自定义Key</p>
     * <p>springframework产生的Key是<b>Integer</b>类型，自定义是<b>String</b>类型</p>
     */
    private Set<Object> cacheKeys;

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public Set<Object> getCacheKeys() {
        return cacheKeys;
    }

    public void setCacheKeys(Set<Object> cacheKeys) {
        this.cacheKeys = cacheKeys;
    }
}
