package cn.bc.orm.hibernate.jpa;

import cn.bc.core.Page;
import cn.bc.core.query.Query;
import cn.bc.db.jdbc.SqlObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test4jpa.xml")
public class HibernateJpaNativeQueryTest {
	private JdbcTemplate jdbcTemplate;
	private JpaTemplate jpaTemplate;

	protected String getTable() {
		return "BC_EXAMPLE";
	};

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Autowired
	public void setEntityManagerFactory(
			EntityManagerFactory entityManagerFactory) {
		this.jpaTemplate = new JpaTemplate(entityManagerFactory);
	}

	@Test
	public void testQueryEmpty() {
		// 清空测试表的数据
		deleteAll();

		// 构建查询器
		Query<Object[]> q = createTestQuery();

		// count
		int c = q.count();
		Assert.assertEquals(0, c);

		// list
		List<Object[]> list = q.list();
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());

		list = q.list(1, 5);
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());

		// singleResult
		Object d = q.singleResult();
		Assert.assertNull(d);
	}

	@Test
	public void testQueryCount() {
		// 清空测试表的数据
		deleteAll();

		// 插入一条测试数据
		insertOne(1);

		// 构建查询器
		Query<Object[]> q = createTestQuery();

		// count
		int c = q.count();
		Assert.assertEquals(1, c);
	}

	@Test
	public void testQueryList() {
		// 清空测试表的数据
		deleteAll();

		// 插入测试数据
		insertOne(1);
		insertOne(2);

		// 构建查询器
		Query<Object[]> q = createTestQuery();

		// list
		List<Object[]> list = q.list();
		Assert.assertNotNull(list);
		Assert.assertEquals(2, list.size());

		Object obj = list.get(0);
		Assert.assertNotNull(obj);

		Assert.assertTrue(obj.getClass().isArray());
		Object[] d = (Object[]) obj;
		Assert.assertEquals(3, d.length);
		Assert.assertEquals("1", d[0].toString());
		Assert.assertEquals("code", d[1].toString());
	}

	@Test
	public void testQuerySingleResult() {
		// 清空测试表的数据
		deleteAll();

		// 插入测试数据
		insertOne(1);

		// 构建查询器
		Query<Object[]> q = createTestQuery();

		// singleResult
		Object obj = q.singleResult();
		Assert.assertNotNull(obj);
		Assert.assertTrue(obj.getClass().isArray());
		Object[] d = (Object[]) obj;
		Assert.assertEquals(3, d.length);
		Assert.assertEquals("1", d[0].toString());
	}

	@Test
	public void testQueryPage1() {
		// 清空测试表的数据
		deleteAll();

		// 插入测试数据
		for (int i = 1; i < 11; i++) {
			insertOne(i);
		}

		// 构建查询器
		Query<Object[]> q = createTestQuery();

		// page
		List<Object[]> list = q.list(1, 5);
		Assert.assertNotNull(list);
		Assert.assertEquals(5, list.size());

		Object obj = list.get(0);
		Assert.assertTrue(obj.getClass().isArray());
		Object[] d = (Object[]) obj;
		Assert.assertEquals(3, d.length);
		Assert.assertEquals("1", d[0].toString());

		obj = list.get(4);
		Assert.assertTrue(obj.getClass().isArray());
		d = (Object[]) obj;
		Assert.assertEquals(3, d.length);
		Assert.assertEquals("5", d[0].toString());
	}

	@Test
	public void testQueryPage2() {
		// 清空测试表的数据
		deleteAll();

		// 插入测试数据
		for (int i = 1; i < 11; i++) {
			insertOne(i);
		}

		// 构建查询器
		Query<Object[]> q = createTestQuery();

		// page
		Page<Object[]> page = q.page(1, 5);
		Assert.assertNotNull(page);
		Assert.assertEquals(0, page.getFirstResult());
		Assert.assertEquals(2, page.getPageCount());
		Assert.assertEquals(5, page.getPageSize());
		Assert.assertEquals(10, page.getTotalCount());

		List<Object[]> list = page.getData();
		Assert.assertNotNull(list);
		Assert.assertEquals(5, list.size());

		Object obj = list.get(0);
		Assert.assertTrue(obj.getClass().isArray());
		Object[] d = (Object[]) obj;
		Assert.assertEquals(3, d.length);
		Assert.assertEquals("1", d[0].toString());

		obj = list.get(4);
		Assert.assertTrue(obj.getClass().isArray());
		d = (Object[]) obj;
		Assert.assertEquals(3, d.length);
		Assert.assertEquals("5", d[0].toString());
	}

	private Query<Object[]> createTestQuery() {
		SqlObject<Object[]> sqlObject = new SqlObject<Object[]>();
		sqlObject.setSql("select id,code,name from " + getTable()
				+ " order by id");
		Query<Object[]> q = new HibernateJpaNativeQuery<Object[]>(this.jpaTemplate,
				sqlObject);
		return q;
	}

	/**
	 * 删除所有数据，用于清空测试现场
	 */
	protected void deleteAll() {
		this.jdbcTemplate.execute("delete from " + this.getTable());
	}

	protected Long insertOne(int id) {
		this.jdbcTemplate.execute("insert into " + this.getTable()
				+ "(id,code,name) values(" + id + ",'code','name')");
		return new Long(id);
	}
}
