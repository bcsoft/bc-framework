package cn.bc.identity.service;

import java.util.List;
import java.util.Map;

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
	private ResourceDao resourceDao;

	public void setResourceDao(ResourceDao resourceDao) {
		this.resourceDao = resourceDao;
		this.setCrudDao(resourceDao);
	}

	public List<Map<String, String>> find4option(Integer[] types,
			Integer[] statues) {
		return this.resourceDao.find4option(types, statues);
	}

	public List<Resource> findAll() {
		return this.createQuery()
				.condition(new OrderCondition("orderNo", Direction.Asc)).list();
	}
}
