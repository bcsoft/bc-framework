package cn.bc.identity.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.cache.Cache;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.dao.ResourceDao;
import cn.bc.identity.domain.Resource;

/**
 * 资源Service接口的实现
 * 
 * @author dragon
 * 
 */
public class ResourceServiceImpl extends DefaultCrudService<Resource> implements
		ResourceService {
	private static Log logger = LogFactory.getLog(ResourceServiceImpl.class);
	private final static String CACHE_KEY = "cn.bc.cache.allResources";
	private ResourceDao resourceDao;
	private Cache cache;

	@Autowired
	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public void setResourceDao(ResourceDao resourceDao) {
		this.resourceDao = resourceDao;
		this.setCrudDao(resourceDao);
	}

	public List<Map<String, String>> find4option(Integer[] types,
			Integer[] statues) {
		return this.resourceDao.find4option(types, statues);
	}

	public Map<Long, Resource> findAll() {
		if (cache.get(CACHE_KEY) == null) {
			logger.warn("Find all resource from db and cache them.");
			List<Resource> all = this.createQuery()
					.condition(new OrderCondition("orderNo", Direction.Asc))
					.list();
			Map<Long, Resource> ss = new LinkedHashMap<Long, Resource>();
			for (Resource resource : all) {
				ss.put(resource.getId(), resource);
			}
			cache.put(CACHE_KEY, ss);
			return ss;
		} else {
			if (logger.isDebugEnabled())
				logger.debug("load all resource from cache.");
			return cache.get(CACHE_KEY);
		}
	}

	private void cleanCache() {
		logger.warn("cleanCache.");
		cache.put(CACHE_KEY, null);
	}

	@Override
	public void delete(Serializable id) {
		super.delete(id);
		cleanCache();
	}

	@Override
	public void delete(Serializable[] ids) {
		super.delete(ids);
		cleanCache();
	}

	@Override
	public Resource save(Resource entity) {
		cleanCache();
		return super.save(entity);
	}

	@Override
	public void save(Collection<Resource> entities) {
		super.save(entities);
		cleanCache();
	}

	@Override
	public void update(Serializable id, Map<String, Object> attributes) {
		super.update(id, attributes);
		cleanCache();
	}

	@Override
	public void update(Serializable[] ids, Map<String, Object> attributes) {
		super.update(ids, attributes);
		cleanCache();
	}
}
