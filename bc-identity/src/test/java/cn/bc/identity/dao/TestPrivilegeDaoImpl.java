package cn.bc.identity.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test.xml")
public class TestPrivilegeDaoImpl {
	@Autowired
	private PrivilegeDao privilegeDao;
	
	@Test//10114209
	public void getActorbyHistoryActorTest() {
		Long actorid = privilegeDao.getActorbyHistoryActor(10114209L);
		System.out.println(actorid);
	}
}
