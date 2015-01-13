package cn.bc.db.jdbc.spring;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.bc.core.Page;
import cn.bc.core.query.Query;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional("txManager")
@ContextConfiguration("classpath:spring-test.xml")
public class SpringJdbcQueryTest {
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	protected String getTable() {
		return "BC_EXAMPLE";
	};

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Test
	public void testQueryEmpty() {
		// 清空测试表的数据
		deleteAll();

		// 构建查询器
		Query<Domain> q = createTestQuery();

		// count
		int c = q.count();
		Assert.assertEquals(0, c);

		// list
		List<Domain> list = q.list();
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());

		list = q.list(1, 5);
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());

		// singleResult
		Domain d = q.singleResult();
		Assert.assertNull(d);
	}

	@Test
	public void testQueryCount() {
		// 清空测试表的数据
		deleteAll();

		// 插入一条测试数据
		insertOne(1);

		// 构建查询器
		Query<Domain> q = createTestQuery();

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
		Query<Domain> q = createTestQuery();

		// list
		List<Domain> list = q.list();
		Assert.assertNotNull(list);
		Assert.assertEquals(2, list.size());
		Assert.assertEquals(new Long(1), list.get(0).getId());
		Assert.assertEquals(new Long(2), list.get(1).getId());
	}

	@Test
	public void testQuerySingleResult() {
		// 清空测试表的数据
		deleteAll();

		// 插入测试数据
		insertOne(1);

		// 构建查询器
		Query<Domain> q = createTestQuery();

		// singleResult
		Domain d = q.singleResult();
		Assert.assertNotNull(d);
		Assert.assertEquals(new Long(1), d.getId());
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
		Query<Domain> q = createTestQuery();

		// page
		List<Domain> list = q.list(1, 5);
		Assert.assertNotNull(list);
		Assert.assertEquals(5, list.size());
		Assert.assertEquals(new Long(1), list.get(0).getId());
		Assert.assertEquals(new Long(5), list.get(4).getId());
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
		Query<Domain> q = createTestQuery();

		// page
		Page<Domain> page = q.page(1, 5);
		Assert.assertNotNull(page);
		Assert.assertEquals(0, page.getFirstResult());
		Assert.assertEquals(2, page.getPageCount());
		Assert.assertEquals(5, page.getPageSize());
		Assert.assertEquals(10, page.getTotalCount());

		List<Domain> list = page.getData();
		Assert.assertNotNull(list);
		Assert.assertEquals(5, list.size());
		Assert.assertEquals(new Long(1), list.get(0).getId());
		Assert.assertEquals(new Long(5), list.get(4).getId());
	}

	private Query<Domain> createTestQuery() {
		Query<Domain> q = new SpringJdbcQuery<Domain>(dataSource,
				new RowMapper<Domain>() {
					public Domain mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						Domain t = new Domain();
						t.setId(rs.getLong("id"));
						return t;
					}
				}).setSql("select * from " + getTable() + " order by id");
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

	public class Domain {
		private Long id;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
	}
}
