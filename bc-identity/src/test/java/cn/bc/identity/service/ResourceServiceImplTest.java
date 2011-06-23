package cn.bc.identity.service;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.identity.domain.Resource;
import cn.bc.identity.service.ResourceService;
import cn.bc.test.AbstractEntityCrudTest;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class ResourceServiceImplTest extends AbstractEntityCrudTest<Long,Resource> {
	ResourceService resourceService;
	@Autowired
	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
		this.crudOperations = resourceService;//赋值基类的crud操作对象
	}

	@Override
	protected Resource createInstance(String config) {
		Resource entity = super.createInstance(config);
		
		//补充一些额外的设置
		entity.setOrderNo("order");
		entity.setName(entity.getOrderNo());
		
		return entity;
	}
}
