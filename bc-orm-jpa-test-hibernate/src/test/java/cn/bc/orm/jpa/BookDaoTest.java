package cn.bc.orm.jpa;

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
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class BookDaoTest {
	private static Logger logger = LoggerFactory.getLogger(BookDaoTest.class);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private BookDao dao;

	@Test
	@Rollback()
	public void createBook() {
		Book book = new Book("书1");
		dao.create(book);
		Assert.assertNotNull(book.getId());
	}

	@Test
	@Rollback()
	public void createBookAndDetail() {
		Book book = new Book("书2");
		dao.create(book);
		Assert.assertNotNull(book.getId());

		// 如果在Book中没有设置级联保存，则需要在保存book后显式保存detail，
		// 否则异常：TransientObjectException: object references an unsaved transient instance - save the transient instance before flushing
		BookDetail bookDetail = new BookDetail();
		bookDetail.setId(book);
		bookDetail.setPublisher("出版社2");
		dao.create(bookDetail);
		Assert.assertNotNull(bookDetail.getId());
	}

	@Test
	@Rollback()
	public void createBookAndDetail2() {
		Book book = new Book("书3");
		dao.create(book);
		Assert.assertNotNull(book.getId());

		BookDetail2 bookDetail = new BookDetail2();
		bookDetail.setBook(book);
		bookDetail.setPublisher("出版社3");
		dao.create(bookDetail);
		Assert.assertNotNull(bookDetail.getId());
	}

	@Test
	@Rollback()
	public void load() {
		Book book = new Book("test");
		em.persist(book);
		Book actual = dao.loadBook(book.getId());
		Assert.assertNotNull(actual);
	}

	@Test
	@Rollback()
	public void modify() {
		Book book = new Book("test");
		em.persist(book);
		Assert.assertNotNull(book.getId());

		book.setName("new-name");
		dao.modify(book);

		Assert.assertEquals(em.createQuery("select name from Book", String.class).getSingleResult(), "new-name");
	}

	@Test
	@Rollback()
	public void remove() {
		Book book = new Book("test");
		em.persist(book);
		Assert.assertNotNull(book.getId());

		dao.removeBook(book.getId());
		Assert.assertNull(em.find(Book.class, book.getId()));
	}

	@Test
	@Rollback()
	public void remove2() {
		Book book = new Book("test");
		BookDetail bookDetail = new BookDetail();
		bookDetail.setId(book);
		bookDetail.setPublisher("test");
		em.persist(book);
		em.persist(bookDetail);
		Assert.assertNotNull(book.getId());

		dao.removeBook(book.getId());
		Assert.assertNull(em.find(Book.class, book.getId()));
		Assert.assertNull(em.find(BookDetail.class, book.getId()));
	}

	@Test
	@Rollback()
	public void findAll() {
		Book book = new Book("test");
		em.persist(book);
		Assert.assertNotNull(book.getId());

		List<Book> books = dao.findAllBook();
		Assert.assertNotNull(books);
		Assert.assertEquals(1, books.size());
		Assert.assertEquals("test", books.get(0).getName());
	}

	@Test
	@Rollback()
	public void find() {
		Book book = new Book("test");
		em.persist(book);

		List list = dao.find("select id, name from Book", null);
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
		Object[] a = (Object[]) list.get(0);
		Assert.assertEquals(book.getId(), a[0]);
		Assert.assertEquals("test", a[1]);

	}

	@Test
	@Rollback()
	public void find2() {
		Book book = new Book("test");
		em.persist(book);

		List<Book> books = dao.find("select b from Book b", null, Book.class);
		Assert.assertNotNull(books);
		Assert.assertEquals(1, books.size());
		Assert.assertEquals("test", books.get(0).getName());
	}

	@Test
	@Rollback()
	public void findByHibernate() {
		Book book = new Book("test");
		em.persist(book);

		List<Map<String, Object>> list = dao.findByHibernate("select id as id, name as name from Book");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		logger.debug("list={}", list);
	}
}