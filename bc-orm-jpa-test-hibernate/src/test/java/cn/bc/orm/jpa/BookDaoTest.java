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
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class BookDaoTest {
    private static Logger logger = LoggerFactory.getLogger(BookDaoTest.class);

    @Autowired
    private BookDao dao;

    @Test
    @Rollback(false)
    public void createBook() {
        Book book = new Book("书1");
        dao.create(book);
        Assert.assertNotNull(book.getId());
    }

    @Test
    @Rollback(false)
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
    @Rollback(false)
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
    @Rollback(false)
    public void load() {
        Long id = (long) 2;
        Book book = dao.loadBook(id);
        Assert.assertNotNull(book);
//		Assert.assertNotNull(book.getDetail());
        logger.debug("book={}", book);
//		logger.debug("detail={}", book.getDetail());
    }

    @Test
    @Rollback(false)
    public void modify() {
        Book book = new Book("code1");
        dao.create(book);
        Assert.assertNotNull(book.getId());

        book.setName("code1-1");
        dao.modify(book);
    }

    @Test
    @Rollback(false)
    public void remove2() {
        Book book = new Book("code1");
        BookDetail bookDetail = new BookDetail();
        bookDetail.setId(book);
        bookDetail.setPublisher("info1");
        dao.create(book);
        dao.create(bookDetail);
        //Assert.assertNotNull(book.getId());

        dao.removeBook(book.getId());
    }

    @Test
    @Rollback(false)
    public void remove() {
        Book book = new Book("code1");
        dao.create(book);
        Assert.assertNotNull(book.getId());

        dao.removeBook(book.getId());
    }

    @Test
    @Rollback(false)
    public void findAll() {
        //create();
        List<Book> books = dao.findAllBook();
        Assert.assertNotNull(books);
        Assert.assertTrue(books.size() > 0);
        logger.debug("books={}", books);
    }

    @Test
    @Rollback(false)
    public void find() {
        List list = dao.find("select id, code from Book", null);
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() > 0);
        logger.debug("list={}", list);
        for (Object one : list) {
            logger.debug("one={}", StringUtils.arrayToCommaDelimitedString((Object[]) one));
        }
    }

    @Test
    @Rollback(false)
    public void find2() {
        List<Book> list = dao.find("select b from Book b", null, Book.class);
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() > 0);
        logger.debug("list={}", list);
    }

    @Test
    @Rollback(false)
    public void findByHibernate() {
        List<Map<String, Object>> list = dao.findByHibernate("select id as id, code as code from Book");
        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() > 0);
        logger.debug("list={}", list);
    }
}
