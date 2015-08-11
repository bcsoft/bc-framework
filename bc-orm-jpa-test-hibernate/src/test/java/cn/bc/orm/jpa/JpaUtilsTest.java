package cn.bc.orm.jpa;

import cn.bc.core.util.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class JpaUtilsTest {
	private static Logger logger = LoggerFactory.getLogger(JpaUtilsTest.class);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private BookDao dao;

	private Long createBook() {
		Book book = new Book();
		dao.create(book);
		Assert.assertNotNull(book.getId());
		return book.getId();
	}

	@Test
	@Rollback()
	public void queryForObject_single() {
		Long id = createBook();
		Query q = JpaUtils.createQuery(em, "select id from Book where id = " + id);
		Assert.assertEquals(id.toString(), JpaUtils.queryForObject(q, String.class));
		Assert.assertEquals(id, JpaUtils.queryForObject(q, Long.class));
		Assert.assertEquals((Integer) id.intValue(), JpaUtils.queryForObject(q, Integer.class));
	}

	@Test
	@Rollback()
	public void queryForObject_null() {
		Query q = JpaUtils.createQuery(em, "select id from Book where id = 0");
		Assert.assertNull(JpaUtils.queryForObject(q, String.class));

		Assert.assertNotNull(JpaUtils.queryForObject(q, Long.class));
		Assert.assertEquals(new Long(0), JpaUtils.queryForObject(q, Long.class));

		Assert.assertNotNull(JpaUtils.queryForObject(q, Integer.class));
		Assert.assertEquals(new Integer(0), JpaUtils.queryForObject(q, Integer.class));

		int n = JpaUtils.queryForObject(q, Integer.class);
		Assert.assertTrue(n == 0);
	}

	@Test(expected = NonUniqueResultException.class)
	@Rollback()
	public void queryForObject_exceptionForMoreThenOneResult() {
		Long id1 = createBook();
		Long id2 = createBook();
		Query q = JpaUtils.createQuery(em, "select id from Book where id in (" + id1 + ", " + id2 + ")");
		JpaUtils.queryForObject(q, String.class);
	}

	@Test
	@Rollback()
	public void nativeQueryForObject_single() {
		Long id = createBook();
		Query q = JpaUtils.createNativeQuery(em, "select id from bc_jpa_book where id = " + id);
		Assert.assertEquals(id.toString(), JpaUtils.queryForObject(q, String.class));
		Assert.assertEquals(id, JpaUtils.queryForObject(q, Long.class));
		Assert.assertEquals((Integer) id.intValue(), JpaUtils.queryForObject(q, Integer.class));
	}

	@Test
	@Rollback()
	public void nativeQueryForObject_null() {
		Query q = JpaUtils.createNativeQuery(em, "select id from bc_jpa_book where id = 0");
		Assert.assertNull(JpaUtils.queryForObject(q, String.class));

		Assert.assertNotNull(JpaUtils.queryForObject(q, Long.class));
		Assert.assertEquals(new Long(0), JpaUtils.queryForObject(q, Long.class));

		Assert.assertNotNull(JpaUtils.queryForObject(q, Integer.class));
		Assert.assertEquals(new Integer(0), JpaUtils.queryForObject(q, Integer.class));

		int n = JpaUtils.queryForObject(q, Integer.class);
		Assert.assertTrue(n == 0);
	}

	@Test(expected = NonUniqueResultException.class)
	@Rollback()
	public void nativeQueryForObject_exceptionForMoreThenOneResult() {
		Long id1 = createBook();
		Long id2 = createBook();
		Query q = JpaUtils.createNativeQuery(em, "select id from bc_jpa_book where id in (" + id1 + ", " + id2 + ")");
		JpaUtils.queryForObject(q, String.class);
	}


	@Test
	@Rollback()
	public void queryForObject_date() {
		String origin = "2015-07-31";
		Calendar date = DateUtils.getCalendar(origin + " 10:20:30");
		Book book = new Book();
		book.setDate(date);
		dao.create(book);
		Assert.assertNotNull(book.getId());
		Long id = book.getId();

		Query q = JpaUtils.createQuery(em, "select date from Book where id = " + id);
		Calendar date2 = JpaUtils.queryForObject(q, Calendar.class);
		Assert.assertEquals(origin + " 00:00:00", DateUtils.formatCalendar2Second(date2));
	}

	@Test
	@Rollback()
	public void queryForObject_timestamp() {
		String origin = "2015-07-31 10:20:30";
		Calendar timestamp = DateUtils.getCalendar(origin);
		Book book = new Book();
		book.setTimestamp(timestamp);
		dao.create(book);
		Assert.assertNotNull(book.getId());
		Long id = book.getId();

		Query q = JpaUtils.createQuery(em, "select timestamp from Book where id = " + id);
		Calendar timestamp2 = JpaUtils.queryForObject(q, Calendar.class);
		Assert.assertEquals(origin, DateUtils.formatCalendar2Second(timestamp2));
	}

	@Test
	@Rollback()
	public void queryForObject_time() {
		String origin = "10:20:30";
		Date time = DateUtils.getDate("2015-07-31 " + origin);
		Book book = new Book();
		book.setTime(time);
		dao.create(book);
		Assert.assertNotNull(book.getId());
		Long id = book.getId();

		Query q = JpaUtils.createQuery(em, "select time from Book where id = " + id);
		Date time2 = JpaUtils.queryForObject(q, Date.class);
		Assert.assertEquals("1970-01-01 " + origin, DateUtils.formatDateTime(time2));
	}

	@Test
	@Rollback()
	public void nativeQueryForObject_date() {
		String origin = "2015-07-31";
		Calendar date = DateUtils.getCalendar(origin + " 10:20:30");
		Book book = new Book();
		book.setDate(date);
		dao.create(book);
		Assert.assertNotNull(book.getId());
		Long id = book.getId();

		// 注：使用原生查询时，数据类型不会自动转换为 Domain 中声明的类型
		Query q = JpaUtils.createNativeQuery(em, "select date from bc_jpa_book where id = " + id);
		java.sql.Date date2 = JpaUtils.queryForObject(q, java.sql.Date.class);
		Assert.assertEquals(origin + " 00:00:00", DateUtils.formatDateTime(date2));

		java.util.Date date3 = JpaUtils.queryForObject(q, java.util.Date.class);
		Assert.assertEquals(origin + " 00:00:00", DateUtils.formatDateTime(date3));
	}

	@Test
	@Rollback()
	public void nativeQueryForObject_timestamp() {
		String origin = "2015-07-31 10:20:30";
		Calendar timestamp = DateUtils.getCalendar(origin);
		Book book = new Book();
		book.setTimestamp(timestamp);
		dao.create(book);
		Assert.assertNotNull(book.getId());
		Long id = book.getId();

		Query q = JpaUtils.createNativeQuery(em, "select timestamp from bc_jpa_book where id = " + id);
		java.sql.Timestamp timestamp2 = JpaUtils.queryForObject(q, java.sql.Timestamp.class);
		Assert.assertEquals(origin, DateUtils.formatDateTime(timestamp2));

		java.util.Date date2 = JpaUtils.queryForObject(q, java.util.Date.class);
		Assert.assertEquals(origin, DateUtils.formatDateTime(date2));
	}

	@Test
	@Rollback()
	public void nativeQueryForObject_time() {
		String origin = "10:20:30";
		Date time = DateUtils.getDate("2015-07-31 " + origin);
		Book book = new Book();
		book.setTime(time);
		dao.create(book);
		Assert.assertNotNull(book.getId());
		Long id = book.getId();

		Query q = JpaUtils.createNativeQuery(em, "select time from bc_jpa_book where id = " + id);
		java.sql.Time time2 = JpaUtils.queryForObject(q, java.sql.Time.class);
		Assert.assertEquals("1970-01-01 " + origin, DateUtils.formatDateTime(time2));

		java.util.Date time3 = JpaUtils.queryForObject(q, java.util.Date.class);
		Assert.assertEquals("1970-01-01 " + origin, DateUtils.formatDateTime(time3));
	}

	@Test
	@Rollback()
	public void queryForObject_Boolean() {
		Book book = new Book();
		book.setBool(true);
		dao.create(book);
		Assert.assertNotNull(book.getId());
		Long id = book.getId();

		Query q = JpaUtils.createQuery(em, "select bool from Book where id = " + id);
		Boolean result = JpaUtils.queryForObject(q, Boolean.class);
		Assert.assertTrue(result);

		book.setBool(null);
		dao.modify(book);
		q = JpaUtils.createQuery(em, "select bool from Book where id = " + id);
		result = JpaUtils.queryForObject(q, Boolean.class);
		Assert.assertNotNull(result);
		Assert.assertFalse(result);
	}

	@Test
	@Rollback()
	public void nativeQueryForObject_Boolean() {
		Book book = new Book();
		book.setBool(true);
		dao.create(book);
		Assert.assertNotNull(book.getId());
		Long id = book.getId();

		Query q = JpaUtils.createNativeQuery(em, "select bool from bc_jpa_book where id = " + id);
		Boolean result = JpaUtils.queryForObject(q, Boolean.class);
		Assert.assertTrue(result);

		book.setBool(null);
		dao.modify(book);
		q = JpaUtils.createQuery(em, "select bool from Book where id = " + id);
		result = JpaUtils.queryForObject(q, Boolean.class);
		Assert.assertNotNull(result);
		Assert.assertFalse(result);
	}

	@Test
	@Rollback()
	public void queryForMap_multiColumn() {
		Book book = new Book();
		book.setBool(true);
		book.setName("书");
		dao.create(book);
		Assert.assertNotNull(book.getId());
		Long id = book.getId();

		Query q = JpaUtils.createQuery(em, "select bool, name, date from Book where id = " + id);
		Map<String, Object> result = JpaUtils.queryForMap(q, new String[]{"bool", "name", "date"});
		Assert.assertNotNull(result);
		Assert.assertTrue((Boolean) result.get("bool"));
		Assert.assertEquals("书", result.get("name"));
		Assert.assertNull(result.get("date"));
	}

	@Test
	@Rollback()
	public void queryForMap_singleColumn() {
		Book book = new Book();
		book.setBool(true);
		book.setName("书");
		dao.create(book);
		Assert.assertNotNull(book.getId());
		Long id = book.getId();

		Query q = JpaUtils.createQuery(em, "select name from Book where id = " + id);
		Map<String, Object> result = JpaUtils.queryForMap(q, new String[]{"name"});
		Assert.assertNotNull(result);
		Assert.assertEquals("书", result.get("name"));
	}

	@Test(expected = NonUniqueResultException.class)
	@Rollback()
	public void queryForMap_exceptionForMoreThenOneResult() {
		Book book1 = new Book();
		book1.setBool(true);
		book1.setName("书1");
		dao.create(book1);
		Assert.assertNotNull(book1.getId());
		Book book2 = new Book();
		book2.setBool(true);
		book2.setName("书1");
		dao.create(book2);
		Assert.assertNotNull(book2.getId());

		Long id1 = book1.getId();
		Long id2 = book2.getId();
		Query q = JpaUtils.createQuery(em, "select name from Book where id in (" + id1 + ", " + id2 + ")");
		JpaUtils.queryForMap(q, new String[]{"name"});
	}
}