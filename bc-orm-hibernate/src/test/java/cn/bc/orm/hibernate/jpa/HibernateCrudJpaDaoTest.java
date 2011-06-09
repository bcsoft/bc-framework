package cn.bc.orm.hibernate.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.orm.hibernate.AbstractSpringManageDaoTest;
import cn.bc.orm.hibernate.Domain;


@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test4jpa.xml")
public class HibernateCrudJpaDaoTest extends AbstractSpringManageDaoTest {
	// 记录在事务开始前预插入的测试数据的id
	private List<Long> ids = new ArrayList<Long>();

	@Override
	protected Long insertOne(String name) {
		Domain entity = new Domain(name);
		crudDao.save(entity);
		Assert.assertFalse(entity.isNew());
		return entity.getId();
	}

	@BeforeTransaction
	public void beforeTransaction() {
		Map<String, Object> parameters = new HashMap<String, Object>(1);
		parameters.put("name", "name");
		Number newId = this.getJdbcInsert().executeAndReturnKey(parameters);
		Long id1 = new Long(newId.longValue());
		Assert.assertTrue(id1 > 0);
		ids.add(id1);
		
		newId = this.getJdbcInsert().executeAndReturnKey(parameters);
		Long id2 = new Long(newId.longValue());
		Assert.assertTrue(id2 > 0);
		Assert.assertTrue(id1 != id2);
		ids.add(id2);
	}

	// 删除插入的测试数据
	@AfterTransaction
	public void afterTransaction() {
		if (ids.isEmpty())
			return;
		String sql = "delete from " + getTableName() + " where id in(";
		for (int i = 0; i < ids.size(); i++) {
			sql += (i == 0 ? "?" : ",?");
		}
		sql += ")";
		this.simpleJdbcTemplate.update(sql, ids.toArray());
	}

	@Test
	@Override
	public void deleteMul() {
		Long id1 = insertOne("name");
		Long id2 = insertOne("name1");

		crudDao.delete(new Long[] { id1, id2 });
		
		//TODO
//		Domain entity = crudDao.load(id1);
//		Assert.assertNull(entity);
//		entity = crudDao.load(id2);
//		Assert.assertNull(entity);
	}

	@Test
	@Override
	public void update() {
		Long id1 = ids.get(0);// insertOne("name");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "newName");
		crudDao.update(id1, map);
		Domain entity = crudDao.load(id1);
		Assert.assertNotNull(entity);
		Assert.assertEquals("newName", entity.getName());
	}

	@Test
	@Override
	public void updateMul() {
		Long id1 = ids.get(0);
		Long id2 = ids.get(1);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "newName");
		crudDao.update(new Long[]{id1,id2}, map);
		
		Domain entity = crudDao.load(id1);
		Assert.assertNotNull(entity);
		Assert.assertEquals("newName", entity.getName());
		
		entity = crudDao.load(id2);
		Assert.assertNotNull(entity);
		Assert.assertEquals("newName", entity.getName());
	}
}
