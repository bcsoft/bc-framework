package cn.bc.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.core.CrudOperations;
import cn.bc.core.Entity;
import cn.bc.core.Page;
import cn.bc.core.query.condition.impl.EqualsCondition;

/**
 * CrudDao和CrudService实现类的测试基类,测试相关的CURD操作
 * 
 * @author dragon
 * 
 * @param <K> 主键类型
 * @param <E> 实体类型
 */
@Transactional
// 基类也要声明这个
public abstract class AbstractEntityCrudTest<K extends Serializable, E extends Entity<K>> {
	protected CrudOperations<E> crudOperations;

	/**
	 * 创建实体对象的一个新实例
	 * 
	 * @param config
	 *            传入的一些配置信息
	 * @return
	 */
	protected E createInstance(String config) {
		E entity = this.crudOperations.create();
		
		//补充一些必填域的设置
		entity.setInner(false);
		entity.setStatus(Entity.STATUS_DISABLED);
		entity.setUid(UUID.randomUUID().toString());
		
		return entity;
	}

	/**
	 * @return 实体对象对应的数据库表名
	 */
	protected String getTableIdName() {
		return "ID";
	}

	protected String getDefaultConfig() {
		return "tT测1";
	}

	/**
	 * @return 保存一条实体数据用于后续测试，返回保存后的实体对象
	 */
	protected E saveOneEntity(E entity) {
		Assert.assertNull(entity.getId());
		crudOperations.save(entity);
		return entity;
	}
	
	/**
	 * 删除所有数据，用于需要时清空测试现场
	 */
	protected void deleteAll() {
		List<E> all = this.crudOperations.createQuery().list();
		Serializable[] ids = new Serializable[all.size()];
		int i = 0;
		for (E e : all) {
			ids[i] = e.getId();
			i++;
		}
		this.crudOperations.delete(ids);
	}

	/**
	 * 生成一个数据库中不存在的id值
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected K buildNonexistentId() {
		return (K) new Long(Integer.MAX_VALUE);
	}

	@Test
	public void testSaveOne() {
		E entity = this.createInstance(getDefaultConfig());
		Assert.assertNull(entity.getId());
		crudOperations.save(entity);
		Assert.assertNotNull(entity.getId());
	}

	@Test
	public void testSaveMultiple() {
		List<E> entities = new ArrayList<E>();
		entities.add(this.createInstance(getDefaultConfig()));
		entities.add(this.createInstance(getDefaultConfig()));
		crudOperations.save(entities);
		Assert.assertNotNull(entities.get(0).getId());
		Assert.assertNotNull(entities.get(1).getId());
		Assert.assertNotSame(entities.get(0).getId(), entities.get(1));
	}

	@Test
	public void testDeleteOne() {
		E entity = this.saveOneEntity(this.createInstance(getDefaultConfig()));
		K id = entity.getId();
		crudOperations.delete(id);
		entity = crudOperations.load(id);
		Assert.assertNull(entity);
	}

	@Test
	public void testDeleteNonexistent() {
		K id = this.buildNonexistentId();
		crudOperations.delete(id);
	}

	@Test
	public void testDeleteMultiple() {
		E entity = this.saveOneEntity(this.createInstance(getDefaultConfig()));
		K id1 = entity.getId();
		entity = this.saveOneEntity(this.createInstance(getDefaultConfig()));
		K id2 = entity.getId();

		crudOperations.delete(new Serializable[] { id1, id2 });
		
		//TODO
//		entity = crudOperations.load(id1);
//		Assert.assertNull(entity);
//		entity = crudOperations.load(id2);
//		Assert.assertNull(entity);
	}

	@Test
	public void testUpdate() {
		E entity = this.saveOneEntity(this.createInstance(getDefaultConfig()));
		K id = entity.getId();
		String uid = UUID.randomUUID().toString();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		crudOperations.update(id, map);
		entity = crudOperations.forceLoad(id);
		Assert.assertNotNull(entity);
		entity = crudOperations.forceLoad(id);
		Assert.assertEquals(uid, entity.getUid());
	}

	@Test
	public void testUpdateMultiple() {
		E entity = this.saveOneEntity(this.createInstance(getDefaultConfig()));
		K id1 = entity.getId();
		entity = this.saveOneEntity(this.createInstance(getDefaultConfig()));
		K id2 = entity.getId();
		String uid = UUID.randomUUID().toString();

		// update
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		crudOperations.update(new Serializable[] { id1, id2 }, map);

		entity = crudOperations.forceLoad(id1);
		Assert.assertNotNull(entity);
		Assert.assertEquals(uid, entity.getUid());

		entity = crudOperations.forceLoad(id2);
		Assert.assertNotNull(entity);
		Assert.assertEquals(uid, entity.getUid());
	}

	@Test
	public void testLoad() {
		E entity = this.saveOneEntity(this.createInstance(getDefaultConfig()));
		K id = entity.getId();
		entity = crudOperations.load(id);
		Assert.assertNotNull(entity);
	}

	@Test
	public void testQueryCount() {
		// 插入0条
		cn.bc.core.query.Query<E> q = crudOperations.createQuery();
		Assert.assertNotNull(q);
		q.condition(new EqualsCondition(this.getTableIdName(), new Long(0)));
		Assert.assertEquals(0, q.count());

		// 插入1条
		E entity = this.saveOneEntity(this.createInstance(getDefaultConfig()));
		K id = entity.getId();
		q = crudOperations.createQuery();
		q.condition(new EqualsCondition(this.getTableIdName(), id));
		Assert.assertEquals(1, q.count());

		// 插入10条
		String uid = UUID.randomUUID().toString();
		for (int i = 0; i < 10; i++) {
			entity = this.createInstance(null);
			entity.setUid(uid);
			this.saveOneEntity(entity);
		}
		q.condition(new EqualsCondition("uid", uid));
		Assert.assertEquals(10, q.count());
	}

	@Test
	public void testQuery4SingleResult() {
		E entity = this.saveOneEntity(this.createInstance(getDefaultConfig()));
		K id = entity.getId();
		cn.bc.core.query.Query<E> q = crudOperations.createQuery();
		q.condition(new EqualsCondition(this.getTableIdName(), id));
		Assert.assertNotNull(q);
		entity = q.singleResult();
		Assert.assertNotNull(entity);
		Assert.assertEquals(id, entity.getId());
	}

	@Test
	public void testQueryList() {
		// 插入0条
		cn.bc.core.query.Query<E> q = crudOperations.createQuery();
		q.condition(new EqualsCondition(this.getTableIdName(), new Long(0)));
		Assert.assertNotNull(q);
		List<E> list = q.list();
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());

		// 插入1条
		E entity = this.saveOneEntity(this.createInstance(getDefaultConfig()));
		K id = entity.getId();
		q = crudOperations.createQuery();
		q.condition(new EqualsCondition(this.getTableIdName(), id));
		list = q.list();
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());

		// 插入10条
		String uid = UUID.randomUUID().toString();
		for (int i = 0; i < 10; i++) {
			entity = this.createInstance(null);
			entity.setUid(uid);
			this.saveOneEntity(entity);
		}
		q.condition(new EqualsCondition("uid", uid));
		list = q.list();
		Assert.assertNotNull(list);
		Assert.assertEquals(10, list.size());
	}

	@Test
	public void testQueryOverList() {
		// 插入10条，然后查询超过这些数据范围的页
		E entity;
		String uid = UUID.randomUUID().toString();
		for (int i = 0; i < 10; i++) {
			entity = this.createInstance(null);
			entity.setUid(uid);
			this.saveOneEntity(entity);
		}
		cn.bc.core.query.Query<E> q = crudOperations.createQuery();
		q.condition(new EqualsCondition("uid", uid));
		List<E> list = q.list(1, 10);
		Assert.assertNotNull(list);
		Assert.assertEquals(10, list.size());

		list = q.list(2, 10);
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());
	}

	@Test
	public void testQueryPage() {
		// 插入0条
		cn.bc.core.query.Query<E> q = crudOperations.createQuery();
		q.condition(new EqualsCondition(this.getTableIdName(), new Long(0)));
		Assert.assertNotNull(q);
		Page<E> page = q.page(1, 100);
		Assert.assertNotNull(page);
		Assert.assertNotNull(page.getData());
		Assert.assertTrue(page.getData().isEmpty());
		Assert.assertEquals(1, page.getPageNo());
		Assert.assertEquals(100, page.getPageSize());
		Assert.assertEquals(0, page.getPageCount());
		Assert.assertEquals(0, page.getTotalCount());
		Assert.assertEquals(0, page.getFirstResult());

		// 插入1条
		E entity = this.saveOneEntity(this.createInstance(getDefaultConfig()));
		K id = entity.getId();
		q = crudOperations.createQuery();
		q.condition(new EqualsCondition(this.getTableIdName(), id));
		page = q.page(1, 100);
		Assert.assertNotNull(page);
		Assert.assertNotNull(page.getData());
		Assert.assertFalse(page.getData().isEmpty());
		Assert.assertEquals(1, page.getPageNo());
		Assert.assertEquals(100, page.getPageSize());
		Assert.assertEquals(1, page.getPageCount());
		Assert.assertEquals(1, page.getTotalCount());
		Assert.assertEquals(0, page.getFirstResult());

		// 插入10条
		String uid = UUID.randomUUID().toString();
		for (int i = 0; i < 10; i++) {
			entity = this.createInstance(null);
			entity.setUid(uid);
			this.saveOneEntity(entity);
		}
		q.condition(new EqualsCondition("uid", uid));
		page = q.page(1, 100);
		Assert.assertNotNull(page.getData());
		Assert.assertEquals(10, page.getData().size());
		Assert.assertEquals(1, page.getPageNo());
		Assert.assertEquals(100, page.getPageSize());
		Assert.assertEquals(1, page.getPageCount());
		Assert.assertEquals(10, page.getTotalCount());
		Assert.assertEquals(0, page.getFirstResult());

		// 第1页
		page = q.page(1, 5);
		Assert.assertEquals(5, page.getData().size());
		Assert.assertEquals(1, page.getPageNo());
		Assert.assertEquals(5, page.getPageSize());
		Assert.assertEquals(2, page.getPageCount());
		Assert.assertEquals(10, page.getTotalCount());
		Assert.assertEquals(0, page.getFirstResult());

		// 第2页
		page = q.page(2, 3);
		Assert.assertEquals(3, page.getData().size());
		Assert.assertEquals(2, page.getPageNo());
		Assert.assertEquals(3, page.getPageSize());
		Assert.assertEquals(4, page.getPageCount());
		Assert.assertEquals(10, page.getTotalCount());
		Assert.assertEquals(3, page.getFirstResult());
	}

	@Test
	public void testQueryOverPage() {
		// 插入10条，然后查询超过这些数据范围的页
		E entity;
		cn.bc.core.query.Query<E> q = crudOperations.createQuery();
		String uid = UUID.randomUUID().toString();
		for (int i = 0; i < 10; i++) {
			entity = this.createInstance(null);
			entity.setUid(uid);
			this.saveOneEntity(entity);
		}
		q.condition(new EqualsCondition("uid", uid));
		Page<E> page = q.page(1, 5);
		Assert.assertNotNull(page.getData());
		Assert.assertEquals(5, page.getData().size());
		Assert.assertEquals(1, page.getPageNo());
		Assert.assertEquals(5, page.getPageSize());
		Assert.assertEquals(2, page.getPageCount());
		Assert.assertEquals(10, page.getTotalCount());
		Assert.assertEquals(0, page.getFirstResult());

		// 第1页
		page = q.page(3, 5);
		Assert.assertEquals(0, page.getData().size());
		Assert.assertEquals(3, page.getPageNo());
		Assert.assertEquals(5, page.getPageSize());
		Assert.assertEquals(2, page.getPageCount());
		Assert.assertEquals(10, page.getTotalCount());
		Assert.assertEquals(10, page.getFirstResult());
	}
}
