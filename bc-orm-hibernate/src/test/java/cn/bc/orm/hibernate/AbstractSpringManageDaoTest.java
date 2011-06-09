package cn.bc.orm.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.core.Page;
import cn.bc.core.dao.CrudDao;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.db.jdbc.SimpleJdbcInsertEx;
import cn.bc.test.TestUtils;


@Transactional
// 基类也要声明这个
public abstract class AbstractSpringManageDaoTest implements InitializingBean {
	protected CrudDao<Domain> crudDao;
	protected DataSource dataSource;
	protected SimpleJdbcTemplate simpleJdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;

	protected String getTableName(){
		return "ZTEST_EXAMPLE";
	}
	// ==dependency inject

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Autowired
	public void setCrudDao(CrudDao<Domain> crudDao) {
		this.crudDao = crudDao;
	}

	public void afterPropertiesSet() throws Exception {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
		this.jdbcInsert = new SimpleJdbcInsertEx(dataSource, TestUtils
				.getDbSequence()).withTableName(getTableName())
				.usingGeneratedKeyColumns("id");
	}

	// == inner getter

	protected SimpleJdbcInsert getJdbcInsert() {
		return jdbcInsert;
	}

	@Test
	@Rollback(true)
	public void save() {
		Domain entity = new Domain("test1");
		crudDao.save(entity);
		Assert.assertTrue(entity.getId() > 0);

		// load
		entity = crudDao.load(entity.getId());
		Assert.assertNotNull(entity);
		Assert.assertEquals("test1", entity.getName());

		// repeat save
		crudDao.save(entity);
	}

	@Test
	public void saveMul() {
		List<Domain> entities = new ArrayList<Domain>();
		entities.add(new Domain("test1"));
		entities.add(new Domain("test2"));
		crudDao.save(entities);
		Assert.assertTrue(entities.get(0).getId() > 0);
		Assert.assertTrue(entities.get(1).getId() > 0);
		Assert.assertTrue(entities.get(0).getId() != entities.get(1).getId());
	}

	@Test
	public void delete() {
		Long id1 = insertOne("name");
		crudDao.delete(id1);
		Domain entity = crudDao.load(id1);
		Assert.assertNull(entity);
	}

	@Test
	public void delete_notExists() {
		Long id1 = new Long(Integer.MAX_VALUE);
		crudDao.delete(id1);
		Domain entity = crudDao.load(id1);
		Assert.assertNull(entity);
	}

	@Test
	public void deleteMul() {
		Long id1 = insertOne("name");
		Long id2 = insertOne("name1");

		crudDao.delete(new Long[] { id1, id2 });
		Domain entity = crudDao.load(id1);
		Assert.assertNull(entity);
		entity = crudDao.load(id2);
		Assert.assertNull(entity);
	}

	@Test
	public void update() {
		Long id1 = insertOne("name");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "newName");
		crudDao.update(id1, map);
		Domain entity = crudDao.load(id1);
		Assert.assertNotNull(entity);
		Assert.assertEquals("newName", entity.getName());
	}

	@Test
	public void updateMul() {
		Long id1 = insertOne("name");
		Long id2 = insertOne("name1");

		// update
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "newName");
		crudDao.update(new Long[] { id1, id2 }, map);

		Domain entity = crudDao.load(id1);
		Assert.assertNotNull(entity);
		Assert.assertEquals("newName", entity.getName());

		entity = crudDao.load(id2);
		Assert.assertNotNull(entity);
		Assert.assertEquals("newName", entity.getName());
	}

	@Test
	@Rollback(true)
	public void load() {
		Long id1 = insertOne("name");
		Domain entity = crudDao.load(id1);
		Assert.assertNotNull(entity);
		Assert.assertEquals("name", entity.getName());
	}

	@Test
	public void query_count() {
		// 插入0条
		cn.bc.core.query.Query<Domain> q = crudDao.createQuery();
		q.condition(new EqualsCondition("id", new Long(0)));
		Assert.assertNotNull(q);
		Assert.assertEquals(0, q.count());

		// 插入1条
		Long id1 = insertOne("name0");
		Assert.assertTrue(id1 > 0);
		q = crudDao.createQuery();
		q.condition(new EqualsCondition("id", id1));
		Assert.assertEquals(1, q.count());

		// 插入10条
		String uuid = UUID.randomUUID().toString();
		for (int i = 0; i < 10; i++)
			insertOne(uuid);
		q.condition(new EqualsCondition("name", uuid));
		Assert.assertEquals(10, q.count());
	}

	@Test
	public void query_singleResult() {
		Long id1 = insertOne("name");
		Assert.assertTrue(id1 > 0);
		cn.bc.core.query.Query<Domain> q = crudDao.createQuery();
		q.condition(new EqualsCondition("id", id1));
		Assert.assertNotNull(q);
		Domain e = q.singleResult();
		Assert.assertNotNull(e);
		Assert.assertEquals(id1, e.getId());
		Assert.assertEquals("name", e.getName());
	}

	@Test
	public void query_list() {
		// 插入0条
		cn.bc.core.query.Query<Domain> q = crudDao.createQuery();
		q.condition(new EqualsCondition("id", new Long(0)));
		Assert.assertNotNull(q);
		List<Domain> list = q.list();
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 0);

		// 插入1条
		Long id1 = insertOne("name0");
		Assert.assertTrue(id1 > 0);
		q = crudDao.createQuery();
		q.condition(new EqualsCondition("id", id1));
		list = q.list();
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 1);

		// 插入10条
		String uuid = UUID.randomUUID().toString();
		for (int i = 0; i < 10; i++)
			insertOne(uuid);
		q.condition(new EqualsCondition("name", uuid));
		list = q.list();
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 10);
	}

	@Test
	public void query_listWithSelect() {
		String select = "id,code";
		// 插入0条
		cn.bc.core.query.Query<Domain> q = crudDao.createQuery();
		q.condition(new EqualsCondition("id", new Long(0)));
		Assert.assertNotNull(q);
		List<Object> list = q.listWithSelect(select);
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 0);
		

		// 插入1条
		Long id1 = insertOne("name0");
		Assert.assertTrue(id1 > 0);
		q = crudDao.createQuery();
		q.condition(new EqualsCondition("id", id1));
		list = q.listWithSelect(select);
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 1);
		Assert.assertTrue(list.get(0).getClass().isArray());
		Assert.assertEquals(id1,((Object[])list.get(0))[0]);

		// 插入10条
		String uuid = UUID.randomUUID().toString();
		for (int i = 0; i < 10; i++)
			insertOne(uuid);
		q.condition(new EqualsCondition("name", uuid));
		list = q.listWithSelect(select);
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() == 10);
	}

	@Test
	public void query_overList() {
		// 插入10条，然后查询超过这些数据范围的页
		String uuid = UUID.randomUUID().toString();
		for (int i = 0; i < 10; i++)
			insertOne(uuid);
		cn.bc.core.query.Query<Domain> q = crudDao.createQuery();
		q.condition(new EqualsCondition("name", uuid));
		List<Domain> list = q.list(1, 10);
		Assert.assertNotNull(list);
		Assert.assertEquals(10, list.size());

		list = q.list(2, 10);
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());
	}

	@Test
	public void query_page() {
		// 插入0条
		cn.bc.core.query.Query<Domain> q = crudDao.createQuery();
		q.condition(new EqualsCondition("id", new Long(0)));
		Assert.assertNotNull(q);
		Page<Domain> page = q.page(1, 100);
		Assert.assertNotNull(page);
		Assert.assertTrue(page.getData() == null || page.getData().isEmpty());
		Assert.assertEquals(1, page.getPageNo());
		Assert.assertEquals(100, page.getPageSize());
		Assert.assertEquals(0, page.getPageCount());
		Assert.assertEquals(0, page.getTotalCount());
		Assert.assertEquals(0, page.getFirstResult());

		// 插入1条
		Long id1 = insertOne("name0");
		Assert.assertTrue(id1 > 0);
		q = crudDao.createQuery();
		q.condition(new EqualsCondition("id", id1));
		page = q.page(1, 100);
		Assert.assertTrue(page.getData() != null && !page.getData().isEmpty());
		Assert.assertEquals(1, page.getPageNo());
		Assert.assertEquals(100, page.getPageSize());
		Assert.assertEquals(1, page.getPageCount());
		Assert.assertEquals(1, page.getTotalCount());
		Assert.assertEquals(0, page.getFirstResult());

		// 插入10条
		String uuid = UUID.randomUUID().toString();
		for (int i = 0; i < 10; i++)
			insertOne(uuid);
		q.condition(new EqualsCondition("name", uuid));
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
	public void query_overPage() {
		// 插入10条，然后查询超过这些数据范围的页
		cn.bc.core.query.Query<Domain> q = crudDao.createQuery();
		String uuid = UUID.randomUUID().toString();
		for (int i = 0; i < 10; i++)
			insertOne(uuid);
		q.condition(new EqualsCondition("name", uuid));
		Page<Domain> page = q.page(1, 5);
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

	/**
	 * 向数据库中插入一条新数据
	 * 
	 * @return 返回主键的值
	 */
	protected abstract Long insertOne(String name);

	// 在oracle中必须指定id的值为hibernate_sequence.nextval，mysql中只需
	// bc_example(name,code)即可
	// private String insertSqlTpl =
	// "insert into bc_example(name,code) values(V)";

	// // 往数据库插入一条测试数据,返回插入数据的id值
	// private long insertOneByJdbc(String name) {
	// final String sql = insertSqlTpl
	// .replaceAll("V", "'" + name + "','code'");
	// // This support is part of the JDBC 3.0 standard;
	// KeyHolder keyHolder = new GeneratedKeyHolder();
	//
	// simpleJdbcTemplate.getJdbcOperations().update(
	// new PreparedStatementCreator() {
	// public PreparedStatement createPreparedStatement(
	// Connection conn) throws SQLException {
	// // PreparedStatement ps = conn.prepareStatement(sql,new
	// // String[]{"id"});-->oracle need it
	// PreparedStatement ps = conn.prepareStatement(sql);
	// return ps;
	// }
	// }, keyHolder);
	// return keyHolder.getKey().longValue();
	// }
}
