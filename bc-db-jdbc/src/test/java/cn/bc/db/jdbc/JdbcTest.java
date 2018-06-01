package cn.bc.db.jdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * postgres jbdc查询测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test.xml")
public class JdbcTest {
  private static Logger logger = LoggerFactory.getLogger(JdbcTest.class);

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  public void query() throws IOException {
    String sql = "select 1 n, 'a' m";
    logger.debug("sql={}", sql);
    List<Map<String, Object>> l = this.jdbcTemplate.query(sql, (Object[]) null, new ColumnMapRowMapper());
    logger.debug("l={}", l);
  }
}
