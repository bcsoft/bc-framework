package cn.bc.identity.service;

import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.dao.ResourceDao;
import cn.bc.identity.domain.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源Service接口的实现
 *
 * @author dragon
 */
@Singleton
@Named("resourceService")
public class ResourceServiceImpl extends DefaultCrudService<Resource> implements ResourceService {
	private static Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);
	private ResourceDao resourceDao;

	@Inject
	public void setResourceDao(ResourceDao resourceDao) {
		this.resourceDao = resourceDao;
		this.setCrudDao(resourceDao);
	}

	public List<Map<String, String>> find4option(Integer[] types, Integer[] statues) {
		return this.resourceDao.find4option(types, statues);
	}

	@Cacheable(value = "identity.resource", key = "'allResources'", unless = "#result == null")
	public Map<Long, Resource> findAll() {
		logger.warn("find all resource from db.");
		List<Resource> all = this.createQuery()
				.condition(new OrderCondition("orderNo", Direction.Asc))
				.list();
		Map<Long, Resource> ss = new LinkedHashMap<>();
		for (Resource resource : all) {
			ss.put(resource.getId(), resource);
		}
		return ss;
	}

	@Override
	@CacheEvict(value = "identity.resource", key = "'allResources'")
	public void delete(Serializable id) {
		super.delete(id);
	}

	@Override
	@CacheEvict(value = "identity.resource", key = "'allResources'")
	public void delete(Serializable[] ids) {
		super.delete(ids);
	}

	@Override
	@CacheEvict(value = "identity.resource", key = "'allResources'")
	public Resource save(Resource entity) {
		return super.save(entity);
	}

	@Override
	@CacheEvict(value = "identity.resource", key = "'allResources'")
	public void save(Collection<Resource> entities) {
		super.save(entities);
	}

	@Override
	@CacheEvict(value = "identity.resource", key = "'allResources'")
	public void update(Serializable id, Map<String, Object> attributes) {
		super.update(id, attributes);
	}

	@Override
	@CacheEvict(value = "identity.resource", key = "'allResources'")
	public void update(Serializable[] ids, Map<String, Object> attributes) {
		super.update(ids, attributes);
	}
}