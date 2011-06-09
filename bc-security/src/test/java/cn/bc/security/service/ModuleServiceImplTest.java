package cn.bc.security.service;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.security.domain.Module;
import cn.bc.test.AbstractEntityCrudTest;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class ModuleServiceImplTest extends AbstractEntityCrudTest<Long,Module> {
	ModuleService moduleService;
	@Autowired
	public void setModuleService(ModuleService moduleService) {
		this.moduleService = moduleService;
		this.crudOperations = moduleService;//赋值基类的crud操作对象
	}

	@Override
	protected Module createInstance(String config) {
		Module entity = super.createInstance(config);
		
		//补充一些额外的设置
		entity.setCode("code");
		entity.setName(entity.getCode());
		
		return entity;
	}
}
