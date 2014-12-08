package cn.bc.identity.service;

import java.util.UUID;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class GeneratorServiceImplTest {
	protected IdGeneratorService generatorService;

	@Autowired
	public void setGeneratorService(IdGeneratorService generatorService) {
		this.generatorService = generatorService;
	}

	// 先插入一条数据，并返回类型的值
	private String insertTestData(Long value, String format) {
		String uuid = UUID.randomUUID().toString();// 使用uuid避免与现有数据产生冲突
		generatorService.currentValue(uuid);
		return uuid;
	}

	@Test
	public void testGetCurrentValue() {
		// 先插入一条数据
		String uuid = insertTestData(new Long(1), null);

		// 验证
		Assert.assertEquals(new Long(1), generatorService.currentValue(uuid));
		Assert.assertEquals("1", generatorService.current(uuid));
	}

	@Test
	public void testGetCurrentWithFormat() {
		// 先插入一条数据
		String uuid = insertTestData(new Long(1), "${T}-${V}");

		// 验证
		Assert.assertEquals(uuid + "-1", generatorService.current(uuid));
	}

	@Test
	public void testGetNextValue() {
		// 先插入一条数据
		String uuid = insertTestData(new Long(1), null);

		// 验证
		Assert.assertEquals(new Long(2), generatorService.nextValue(uuid));
		Assert.assertEquals("3", generatorService.next(uuid));
	}

	@Test
	public void testGetNextWithFormat() {
		// 先插入一条数据
		String uuid = insertTestData(new Long(1), "${T}-${V}");

		// 验证
		Assert.assertEquals(uuid + "-2", generatorService.next(uuid));
	}
}
