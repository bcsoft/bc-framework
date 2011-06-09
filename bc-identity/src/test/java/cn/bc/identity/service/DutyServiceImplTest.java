package cn.bc.identity.service;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.identity.domain.Duty;
import cn.bc.identity.service.DutyService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.test.AbstractEntityCrudTest;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class DutyServiceImplTest extends AbstractEntityCrudTest<Long,Duty> {
	DutyService dutyService;
	IdGeneratorService idGeneratorService;
	@Autowired
	public void setDutyService(DutyService dutyService) {
		this.dutyService = dutyService;
		this.crudOperations = dutyService;//赋值基类的crud操作对象
	}
	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	@Override
	protected Duty createInstance(String config) {
		Duty entity = super.createInstance(config);
		//System.out.println("----"+entity.getStatus());
		//补充一些额外的设置
		entity.setCode(this.idGeneratorService.next("duty.code"));
		entity.setName(entity.getCode());
		
		return entity;
	}
}
