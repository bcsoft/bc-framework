package cn.bc.orm.hibernate;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration("classpath:spring-test.xml")
public class HibernateCrudDaoTest extends AbstractSpringManageDaoTest {
	@Override
	protected Long insertOne(String name) {
		Map<String, Object> parameters = new HashMap<String, Object>(1);
		parameters.put("name", name);
		Number newId = this.getJdbcInsert().executeAndReturnKey(parameters);
		
		//mysql返回为：java.lang.Long,oracle则为java.math.BigDecimal
		//Assert.assertEquals(Long.class, newId.getClass());
		Long id = new Long(newId.longValue());
		Assert.assertTrue(id > 0);
		return id;
	}
}
